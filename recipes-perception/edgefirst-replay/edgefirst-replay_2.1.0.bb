DESCRIPTION = "EdgeFirst MCAP Replay"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/replay/releases/download/v${PV}/edgefirst-replay-${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-replay;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/replay/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-replay.service \
    file://edgefirst-replay.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "f5683f645f2560a8ee50d5924a80dadc5cf4dcc71d0c7b065337e58ef5869ac4"
BINARY_SHA256SUM[x86_64] = "5809f6b1b720e5503c10cee558ad3fb124f3acb07bdc2423db25f9cd7524d261"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

DEPENDS = "videostream"

S = "${WORKDIR}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-replay.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/edgefirst-replay.default ${D}${sysconfdir}/default/edgefirst-replay
        install -m 0755 ${UNPACKDIR}/edgefirst-replay ${D}${bindir}/edgefirst-replay
    else
        install -m 0644 ${WORKDIR}/edgefirst-replay.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/edgefirst-replay.default ${D}${sysconfdir}/default/edgefirst-replay
        install -m 0755 ${WORKDIR}/edgefirst-replay ${D}${bindir}/edgefirst-replay
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-replay.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
