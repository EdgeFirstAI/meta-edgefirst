SUMMARY = "EdgeFirst Perception Platform - on-target diagnostics"
DESCRIPTION = "Tools for checking the camera, GPU and NPU stack on target. \
Kept separable because devmem2 maps arbitrary physical memory from userspace, \
which a production image may not want to ship."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    v4l-utils \
    devmem2 \
"
