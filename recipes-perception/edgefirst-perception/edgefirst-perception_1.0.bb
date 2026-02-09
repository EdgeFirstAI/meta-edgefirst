DESCRIPTION = "EdgeFirst Perception Services"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "file://edgefirst.target"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/edgefirst.target ${D}${systemd_system_unitdir}
}

REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} = "\
    zenohd \
    edgefirst-schemas \
    edgefirst-camera \
    edgefirst-imu \
    edgefirst-navsat \
    edgefirst-websrv \
    edgefirst-webui \
"

FILES:${PN} += "${systemd_system_unitdir}"
