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
SRC_URI[default.sha256sum] = "c1725b8e41fea3d15317936d4f2f7b436fbae9fa4b9e399d6ee50cbe27659476"

BINARY_SHA256SUM[aarch64] = "6f3620b8095735e8e6e140ee9bc97c898c3669c6d58fbfbd4770ecdf04065b3f"
BINARY_SHA256SUM[x86_64] = "42cdd7a2cf9dcc3747a625e6af75ee1f7e67714c097e01d0ee1a158f713247e3"

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
