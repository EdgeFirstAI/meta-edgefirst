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
SRC_URI[default.sha256sum] = "2139ecae3de819dab6d3f09770267472017e43546df196f813c96f03529e5a16"

BINARY_SHA256SUM[aarch64] = "5ed27b0de3acf072e0fecccbb342b490cc0f8d2afcace4e55be3079257048f44"
BINARY_SHA256SUM[x86_64] = "415c637d90a577ad96325998f2d2e75e9e6492053c8075e2cfa94bb7b12b0a74"

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
