# Neutron NPU: export buffers as DMA-BUF (EDGEAI-1186)
#
# Replace anon_inode_getfd() with dma_buf_export() in the Neutron
# buffer allocator, enabling zero-copy sharing with V4L2/GStreamer/GPU.
#
# Merged upstream by NXP in the wrynose (6.18.20) kernel tree as
# 053be821725d ("AIR-14567 staging: neutron: export buffers as dma-buf")
# with follow-up 464fd6f2e2de ("MA-24787 staging: neutron: Import
# DMA_BUF namespace for dma-buf symbols"), so the patch is only applied
# on pre-wrynose layer series where the driver still uses anon inodes.

# Neutron mailbox handshake for eIQ Neutron SDK 3.2.x firmware.
#
# 3.2.x firmware replies 0xA1 to a mailbox reset where the 6.18.20 driver
# expects 0x0 and waits until its retry budget expires, leaving the NPU
# unusable. This is NXP's a7dcf8975208 ("staging: neutron: rework
# driver-firmware mailbox handshake"), backported verbatim. The reset reply
# becomes mandatory, so this kernel requires 3.2.x firmware; the neutron
# recipe installs firmware, library and headers from one SDK tree, so a
# rootfs cannot mix generations.
#
# NXP's 6.18.37 kernel already carries the change, so it is applied only on
# older kernels, and only on the SoCs that have a Neutron NPU.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"scarthgap", "walnascar", "whinlatter"}:
        d.appendVar("SRC_URI", " file://0001-staging-neutron-export-buffers-as-dma-buf.patch")
    # MODULE_IMPORT_NS() takes an unquoted namespace before kernel 6.13; the
    # whinlatter kernel (6.18) needs the quoted form, so it is excluded.
    if series & {"scarthgap", "walnascar"}:
        d.appendVar("SRC_URI", " file://0003-staging-neutron-import-dma-buf-namespace.patch")

    neutron_socs = {"mx943-nxp-bsp", "mx95-nxp-bsp", "mx952-nxp-bsp"}
    kernel = (d.getVar("PV") or "0").split("+")[0]
    if ("wrynose" in series and neutron_socs & set(d.getVar("OVERRIDES").split(":"))
            and bb.utils.vercmp_string(kernel, "6.18.37") < 0):
        d.appendVar("SRC_URI", " file://0004-staging-neutron-rework-driver-firmware-mailbox-handshake.patch")
}

# NXP ships an os08a20+NeoISP+ADV7535 combo dtb for imx95-15x15-evk but not
# for imx95-19x19-evk (only os08a20+IT6263 combos exist there); os08a20 uses
# the dedicated CSI-2 #0 D-PHY, independent of the DSI/CSI-2 #1 combo D-PHY,
# so camera and ADV7535 HDMI output can coexist on this board.
SRC_URI:append:imx95-19x19-lpddr5-evk = " file://0002-arm64-dts-freescale-imx95-19x19-evk-add-os08a20-isp.patch"

KERNEL_DEVICETREE:append:imx95-19x19-lpddr5-evk = " freescale/imx95-19x19-evk-os08a20-isp-adv7535.dtb"

# The stock kernel-image-<type> scripts point /boot/<type> at the newest
# installed kernel with a symlink, falling back to an in-place copy on a
# filesystem without symlinks, and delete /boot/<type> whenever any kernel
# package is removed. Kernel packages carry their version in the package name,
# so removing an old kernel after upgrading deleted the new one's image.
#
# With the FAT boot partition mounted on /boot the image is a plain file that
# U-Boot loads by name. The replacement scripts install it atomically, keep the
# previous one as <type>.prev, and on removal only touch it when it is the image
# of the package being removed. KERNEL_IMAGETYPE_SYMLINK stays set because the
# deploy directory symlink that IMAGE_BOOT_FILES uses depends on it.
python () {
    kname = d.getVar("KERNEL_PACKAGE_NAME") or "kernel"
    for scriptname, var in (("kernel-image-postinst.sh", "pkg_postinst"),
                            ("kernel-image-prerm.sh", "pkg_prerm")):
        path = bb.utils.which(d.getVar("FILESPATH"), scriptname)
        if not path:
            bb.fatal("%s not found in FILESPATH" % scriptname)
        bb.parse.mark_dependency(d, path)
        with open(path) as f:
            script = f.read()
        for imagetype in (d.getVar("KERNEL_IMAGETYPES") or "").split():
            d.setVar("%s:%s-image-%s" % (var, kname, imagetype.lower()),
                     script.replace("@TYPE@", imagetype))
    for imagetype in (d.getVar("KERNEL_IMAGETYPES") or "").split():
        d.delVar("pkg_postrm:%s-image-%s" % (kname, imagetype.lower()))
}

# apt only replaces a package whose version string changed. A kernel patch or
# a change to the scripts above leaves the kernel version unchanged, so the
# revision carries a digest of everything local that goes into the kernel
# packages. The feed is pinned, so the revision only has to differ between
# builds, not increase.
python () {
    import hashlib
    digest = hashlib.sha256()
    names = [uri[len("file://"):].split(";")[0]
             for uri in (d.getVar("SRC_URI") or "").split() if uri.startswith("file://")]
    names += ["kernel-image-postinst.sh", "kernel-image-prerm.sh"]
    for name in sorted(set(names)):
        path = bb.utils.which(d.getVar("FILESPATH"), name)
        if path:
            bb.parse.mark_dependency(d, path)
            digest.update(name.encode() + bb.utils.sha256_file(path).encode())
    d.setVar("PR", "r0." + digest.hexdigest()[:8])
}
