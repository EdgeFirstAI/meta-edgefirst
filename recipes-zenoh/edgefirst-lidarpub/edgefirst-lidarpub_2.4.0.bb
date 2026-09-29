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
SRC_URI[default.sha256sum] = "2dc6928469144d48d45193bcd14db8ea64ad24d269c8e39777d8fb85e1454922"

BINARY_SHA256SUM[aarch64] = "f400965fee9e1b013b7b32336066101533b04ca9b2275aa46fd849a4048bd50a"
BINARY_SHA256SUM[x86_64] = "2469e868c39b47eee155e79b5d8d4901539936f0a489bff894beec122c173181"

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
