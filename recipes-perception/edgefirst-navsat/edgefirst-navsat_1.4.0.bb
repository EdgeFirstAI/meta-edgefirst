DESCRIPTION = "EdgeFirst NavSat Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=700c2516a940487339707f533f4dd382"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/navsat/releases/download/v${PV}/edgefirst-navsat-linux-${TARGET_ARCH};downloadfilename=edgefirst-navsat;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/navsat/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-navsat.service \
"
SRC_URI[license.sha256sum] = "8bb5c73a6c6f5b301c0397fdbe9353ce856ca122dc603051b2cdbe8b24380380"

BINARY_SHA256SUM[aarch64] = "a12202b5826bbe7091e10dce8905f68d0e10f0e858cb368f7f855c822bb4bc96"
BINARY_SHA256SUM[x86_64] = "28e4f4e348d5c44383a488661f0c7306dbdcebbfec49f967e664ef6a31be9074"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-navsat.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/edgefirst-navsat ${D}${bindir}/edgefirst-navsat
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-navsat.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${bindir}"
