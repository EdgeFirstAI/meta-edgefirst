DESCRIPTION = "EdgeFirst Model Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/model/releases/download/v${PV}/edgefirst-model-linux-${TARGET_ARCH};downloadfilename=edgefirst-model;name=binary \
    https://github.com/EdgeFirstAI/model/releases/download/v${PV}/model.default;downloadfilename=edgefirst-model.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/model/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-model.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "4d8d9ca2733d52b0afd9bad43fa9928b9dffda9e8c6d37147e08d2d68afaeb26"

BINARY_SHA256SUM[aarch64] = "71ec904ab6bf42b1c07aa60f76e198f7d974742df4318b881578c6392bdb9a87"
BINARY_SHA256SUM[x86_64] = "723fe906823d843bd4b5fcfcffed9dd065e6fad75fcc829055f290a1aeef8433"

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

    install -m 0644 ${S}/edgefirst-model.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-model.default ${D}${sysconfdir}/default/edgefirst-model
    install -m 0755 ${S}/edgefirst-model ${D}${bindir}/edgefirst-model
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-model.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
