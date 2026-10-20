# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.
#
# Based on NXP's MM_04.11.00_2605_L6.18.20 baseline (rel_imx_6.18.20_2.0.0).
# Every Yocto series builds the rolling edgefirst-dmabuf branch.

# The fork is rebased onto NXP's wrynose baseline, where the physical memory
# meta header is gst/video/gstphymemmeta.h. NXP's walnascar GStreamer ships it
# as gst/allocators/gstphymemmeta.h, so there a forwarding header provides the
# wrynose path.
IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"

python () {
    d.setVar("SRCBRANCH", "edgefirst-dmabuf")
    d.setVar("SRCREV", "59f9a4418489c98c552d682c0909b7d030ddfcef")
}

edgefirst_phymem_compat() {
    install -d ${WORKDIR}/edgefirst-compat/gst/video
    echo '#include <gst/allocators/gstphymemmeta.h>' > ${WORKDIR}/edgefirst-compat/gst/video/gstphymemmeta.h
}

# The fork also installs the remote desktop tools, which the walnascar recipe
# has no package for and the board does not use.
edgefirst_drop_remote_desktop() {
    rm -f ${D}${bindir}/remote_input ${D}${bindir}/remote_desktop.py
}

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

python () {
    if "walnascar" in (d.getVar("LAYERSERIES_CORENAMES") or "").split():
        d.appendVarFlag("do_install", "postfuncs", " edgefirst_drop_remote_desktop")
        d.appendVar("SRC_URI", " file://0001-plugins-build-spdifdemux-only-with-spdifparser.patch")
        d.appendVarFlag("do_configure", "prefuncs", " edgefirst_phymem_compat")
        d.appendVar("CFLAGS", " -I${WORKDIR}/edgefirst-compat")
}

# The fork's remote-desktop tool needs libinput, which only the wrynose recipe
# declares.
DEPENDS:append = " libinput"
