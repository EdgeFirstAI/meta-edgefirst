DESCRIPTION = "EdgeFirst Sensor Fusion Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/fusion/releases/download/v${PV}/edgefirst-fusion-linux-${TARGET_ARCH};downloadfilename=edgefirst-fusion;name=binary \
    https://github.com/EdgeFirstAI/fusion/releases/download/v${PV}/fusion.default;downloadfilename=edgefirst-fusion.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/fusion/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-fusion.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "399147fe26b076caa2551c421d0aa9c474c346ab3baa9bf3a5609ab4c16e5f5a"

BINARY_SHA256SUM[aarch64] = "31e685e8f0fac221fa184d545fa5f485a0b26766f28cb5fb6c41e1942eea4acd"
BINARY_SHA256SUM[x86_64] = "c41d58925ecee726d453a17d54a8d3bb9855bc39848fd57bef7f268877a1e8d3"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

RDEPENDS:${PN} = "tensorflow-lite"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-fusion.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-fusion.default ${D}${sysconfdir}/default/edgefirst-fusion
    install -m 0755 ${S}/edgefirst-fusion ${D}${bindir}/edgefirst-fusion
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-fusion.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
