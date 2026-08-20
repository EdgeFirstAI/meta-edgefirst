SUMMARY = "EdgeFirst Perception Platform - GStreamer/NNStreamer ML pipelines"
DESCRIPTION = "EdgeFirst GStreamer/NNStreamer ML inference pipeline elements. \
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
