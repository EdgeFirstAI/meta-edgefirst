DESCRIPTION = "EdgeFirst MCAP Replay"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/replay/releases/download/v${PV}/edgefirst-replay-${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-replay;name=binary \
    https://github.com/EdgeFirstAI/replay/releases/download/v${PV}/replay.default;downloadfilename=edgefirst-replay.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/replay/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-replay.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "5c90602efa90eda159b3f15036f716b2d87ae218b23ea8e8539c57d203c64928"

BINARY_SHA256SUM[aarch64] = "013bc83b5003ed7e15fcf9e2283e5502ee4af4af12cb723ecd4b358ea01ef9d3"
BINARY_SHA256SUM[x86_64] = "9d8d631ca7b65e43cc239af43f7e570e1f71872d6404c22ecf3044fedf529570"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

DEPENDS = "videostream"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-replay.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-replay.default ${D}${sysconfdir}/default/edgefirst-replay
    install -m 0755 ${S}/edgefirst-replay ${D}${bindir}/edgefirst-replay
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-replay.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
