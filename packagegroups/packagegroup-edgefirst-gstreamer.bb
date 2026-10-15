SUMMARY = "EdgeFirst Perception Platform - GStreamer/NNStreamer ML pipelines"
DESCRIPTION = "EdgeFirst GStreamer/NNStreamer ML inference pipeline elements, \
together with the platform GStreamer stack they need. Enabling this is the \
single switch for GStreamer on an EdgeFirst image; nothing else installs it, \
because EdgeFirst's own components reach the camera and codec through V4L2 \
directly. \
\
A standalone recipe (not a packagegroup-edgefirst subpackage) so wanting \
this doesn't force bitbake to also build the -zenoh or -python flavors' \
dependencies, and so that a broken edgefirst-gstreamer build can't block \
consumers who only want -zenoh or -python."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    packagegroup-edgefirst \
    edgefirst-gstreamer \
"

# i.MX 8M Plus: NXP's plugin set, where MACHINE_GSTREAMER_1_0_PLUGIN resolves
# to imx-gst1.0-plugin and brings the hardware elements (VPU decode,
# imxvideoconvert_g2d). meta-edgefirst bbappends that recipe to add DMA-BUF
# zero-copy, which is what lets a GStreamer pipeline hand buffers to the NPU
# without a CPU copy. Those elements call the Hantro userspace, so the codec
# group is a dependency here rather than a parallel choice.
#
# i.MX 95 is absent deliberately: its GStreamer payload comes from the BSP
# vendor's own packagegroup, not from meta-freescale-distro.
RDEPENDS:${PN}:append:mx8mp-nxp-bsp = " \
    packagegroup-fsl-gstreamer1.0 \
    packagegroup-fsl-gstreamer1.0-video-bad \
    packagegroup-fsl-gstreamer1.0-network \
    packagegroup-edgefirst-codec \
"
