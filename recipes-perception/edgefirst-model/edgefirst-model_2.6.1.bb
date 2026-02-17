DESCRIPTION = "EdgeFirst Model Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/model/releases/download/v${PV}/edgefirst-model-linux-${TARGET_ARCH};downloadfilename=edgefirst-model;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/model/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-model.service \
    file://edgefirst-model.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "3ef978684476b1cd94bea86a5bee50384f404e9942bbaddf704584bb1ffdda95"
BINARY_SHA256SUM[x86_64] = "fd0ff9dc47eb86c716ad3ff6bfd8f0f5939e4fece7c845f2c96b09061985e2ab"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

RDEPENDS:${PN} = "tensorflow-lite"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/edgefirst-model.service ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/edgefirst-model.default ${D}${sysconfdir}/default/edgefirst-model

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/edgefirst-model ${D}${bindir}/edgefirst-model
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-model.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
