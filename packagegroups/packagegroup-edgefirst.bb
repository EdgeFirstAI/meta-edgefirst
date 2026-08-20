SUMMARY = "EdgeFirst Perception Platform - foundation"
DESCRIPTION = "Foundation libraries for the EdgeFirst Perception Platform \
(HAL, videostream). Split out from the Zenoh, GStreamer, and Python \
flavors (packagegroup-edgefirst-zenoh/-gstreamer/-python) so that wanting \
one flavor doesn't force bitbake to build the others' dependencies."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    edgefirst-hal \
    videostream \
    videostream-cli \
"
