DESCRIPTION = "EdgeFirst Sensor Fusion Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/fusion/releases/download/v${PV}/edgefirst-fusion-linux-${TARGET_ARCH};downloadfilename=edgefirst-fusion;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/fusion/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-fusion.service \
    file://edgefirst-fusion.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "fdbfc141763c508fbc38c2903b281d5812335bbb0cf6ed4c196e87cf37ad8e9c"
BINARY_SHA256SUM[x86_64] = "4c771a293a20b6dac4009626d36f58936a913d56312feb498540714185ee9b22"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

RDEPENDS:${PN} = "tensorflow-lite"

S = "${WORKDIR}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-fusion.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/edgefirst-fusion.default ${D}${sysconfdir}/default/edgefirst-fusion
        install -m 0755 ${UNPACKDIR}/edgefirst-fusion ${D}${bindir}/edgefirst-fusion
    else
        install -m 0644 ${WORKDIR}/edgefirst-fusion.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/edgefirst-fusion.default ${D}${sysconfdir}/default/edgefirst-fusion
        install -m 0755 ${WORKDIR}/edgefirst-fusion ${D}${bindir}/edgefirst-fusion
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-fusion.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
