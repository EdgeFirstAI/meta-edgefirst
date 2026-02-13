SUMMARY = "EdgeFirst SDK - development libraries for cross-compilation"
DESCRIPTION = "Installs -dev and -staticdev packages for EdgeFirst libraries \
into the SDK toolchain sysroot for cross-compilation support."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    videostream-dev \
    zenoh-c-dev \
    zenoh-c-staticdev \
    edgefirst-schemas-dev \
    edgefirst-schemas-staticdev \
    edgefirst-hal-dev \
    edgefirst-hal-staticdev \
"
