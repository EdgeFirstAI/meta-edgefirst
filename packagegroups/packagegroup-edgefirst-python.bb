SUMMARY = "EdgeFirst Perception Platform - Python bindings"
DESCRIPTION = "Python bindings for the EdgeFirst foundation and Zenoh \
libraries. A standalone recipe (not a packagegroup-edgefirst subpackage) \
so wanting this doesn't force bitbake to also build the -zenoh or \
-gstreamer flavors' dependencies."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    python3-zenoh \
    edgefirst-tflite \
    edgefirst-schemas-python \
    edgefirst-hal-python \
    videostream-python \
"
