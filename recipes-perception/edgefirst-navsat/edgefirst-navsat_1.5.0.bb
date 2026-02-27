DESCRIPTION = "EdgeFirst NavSat Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=700c2516a940487339707f533f4dd382"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/navsat/releases/download/v${PV}/edgefirst-navsat-linux-${TARGET_ARCH};downloadfilename=edgefirst-navsat;name=binary \
    https://github.com/EdgeFirstAI/navsat/releases/download/v${PV}/navsat.default;downloadfilename=edgefirst-navsat.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/navsat/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-navsat.service \
"
SRC_URI[license.sha256sum] = "8bb5c73a6c6f5b301c0397fdbe9353ce856ca122dc603051b2cdbe8b24380380"
SRC_URI[default.sha256sum] = "4657c7442ac9db233e222e6b461d92846d20084784d0e009545d33fe05fbdcf3"

BINARY_SHA256SUM[aarch64] = "66950be024e52259622ed454d91754b53f84e868e3d38d3b19c04ab87aa589e8"
BINARY_SHA256SUM[x86_64] = "f56409d53c8d2ee6f5fb6ff26504aae00f52802f6b7c2f464ebe1a2bd13613b5"

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
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-navsat.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-navsat.default ${D}${sysconfdir}/default/edgefirst-navsat
    install -m 0755 ${S}/edgefirst-navsat ${D}${bindir}/edgefirst-navsat
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-navsat.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
