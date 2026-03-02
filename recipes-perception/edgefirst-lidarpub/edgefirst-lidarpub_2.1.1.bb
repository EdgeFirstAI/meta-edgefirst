DESCRIPTION = "EdgeFirst LiDAR Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/edgefirst-lidarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-lidarpub;name=binary \
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/lidarpub.default;downloadfilename=edgefirst-lidarpub.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/lidarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-lidarpub.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "5fa6d858f1d9243b892a95a953fced6c9de73226ffb1f90dc30586078e31d8d7"

BINARY_SHA256SUM[aarch64] = "62b67a32bc419f767fd3f29bfa0d5d4a58391475c561ef95139ecc83a942e24f"
BINARY_SHA256SUM[x86_64] = "0369a1fbbe74fe946299da0294e22f6eec8660fe03807bba9a57c076161b1905"

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

    install -m 0644 ${S}/edgefirst-lidarpub.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-lidarpub.default ${D}${sysconfdir}/default/edgefirst-lidarpub
    install -m 0755 ${S}/edgefirst-lidarpub ${D}${bindir}/edgefirst-lidarpub
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-lidarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
