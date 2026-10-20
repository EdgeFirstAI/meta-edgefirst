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

# Everything below changes how the kernel is packaged so that apt can update it
# on a board, and applies only where EDGEFIRST_APT_FEED is 1. The vendor stacks
# that build this kernel recipe otherwise keep the stock packaging.
#
# The stock kernel-image-<type> scripts point /boot/<type> at the newest
# installed kernel with a symlink, and delete /boot/<type> whenever any kernel
# package is removed. Kernel packages carry their version in the package name,
# so removing an old kernel after upgrading deleted the new one's image.
#
# U-Boot loads the image and the device trees as plain files from the FAT boot
# partition, which cannot hold what dpkg needs to replace a file: it makes a
# hard link to the old one first. The packages therefore install into /boot on
# the root filesystem, as the stock packages do, and the scripts below copy the
# image and the device trees to the FAT partition, wherever it is mounted,
# atomically and keeping the previous image as <type>.prev. The FAT partition
# must not be mounted on /boot, which would hide the packaged files. Removing a
# kernel only touches the partition's <type> when it is that kernel's image.
# KERNEL_IMAGETYPE_SYMLINK stays set because the deploy directory symlink that
# IMAGE_BOOT_FILES uses depends on it.

# meta-freescale makes every kernel conflict with the GPU module of the others,
# which allows one kernel at a time. apt's full-upgrade never removes an
# installed package to make room for a new one and so keeps the old kernel,
# holding back the update. Without the conflict a new kernel installs beside the
# old one, as it does on a desktop distribution, and apt autoremove retires the
# old kernel and its modules, which are marked as automatically installed.
#
# apt only replaces a package whose version string changed. A kernel patch or
# a change to the scripts leaves the kernel version unchanged, so the revision
# carries a digest of everything local that goes into the kernel packages, after
# the release and PATCH so that a revision always sorts above the one it
# replaces: apt would otherwise take an update whose digest sorts lower for a
# downgrade.
python () {
    if d.getVar("EDGEFIRST_APT_FEED") != "1":
        return
    import hashlib

    kname = d.getVar("KERNEL_PACKAGE_NAME") or "kernel"
    imagetypes = (d.getVar("KERNEL_IMAGETYPES") or "").split()

    def script(name):
        path = bb.utils.which(d.getVar("FILESPATH"), name)
        if not path:
            bb.fatal("%s not found in FILESPATH" % name)
        bb.parse.mark_dependency(d, path)
        with open(path) as f:
            return f.read()

    common = script("kernel-boot-common.sh")
    for scriptname, var in (("kernel-image-postinst.sh", "pkg_postinst"),
                            ("kernel-image-prerm.sh", "pkg_prerm")):
        text = script(scriptname).replace("@BOOT_FUNCTIONS@", common)
        for imagetype in imagetypes:
            d.setVar("%s:%s-image-%s" % (var, kname, imagetype.lower()),
                     text.replace("@TYPE@", imagetype))
    d.setVar("pkg_postinst:%s-devicetree" % kname,
             script("kernel-devicetree-postinst.sh").replace("@BOOT_FUNCTIONS@", common))
    tools = " util-linux-findmnt util-linux-mount util-linux-umount"
    d.appendVar("RDEPENDS:%s-devicetree" % kname, tools)
    for imagetype in imagetypes:
        d.appendVar("RDEPENDS:%s-image-%s" % (kname, imagetype.lower()), tools)
        d.delVar("pkg_postrm:%s-image-%s" % (kname, imagetype.lower()))

    d.setVar("RCONFLICTS:%s-base:remove" % kname, "kernel-module-imx-gpu-viv")
    d.setVar("RREPLACES:%s-base:remove" % kname, "kernel-module-imx-gpu-viv")

    digest = hashlib.sha256()
    names = [uri[len("file://"):].split(";")[0]
             for uri in (d.getVar("SRC_URI") or "").split() if uri.startswith("file://")]
    names += ["kernel-image-postinst.sh", "kernel-image-prerm.sh", "kernel-devicetree-postinst.sh", "kernel-boot-common.sh"]
    for name in sorted(set(names)):
        path = bb.utils.which(d.getVar("FILESPATH"), name)
        if path:
            bb.parse.mark_dependency(d, path)
            digest.update(name.encode() + bb.utils.sha256_file(path).encode())
    d.setVar("PR", "r0.%s.%s" % (d.getVar("EDGEFIRST_REVISION"), digest.hexdigest()[:8]))
}
