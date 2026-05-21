DESCRIPTION = "EdgeFirst MCAP Recorder"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=dd1425eba06ca7b09230155041834ed7"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/recorder/releases/download/v${PV}/edgefirst-recorder-v${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-recorder;name=binary \
    https://github.com/EdgeFirstAI/recorder/releases/download/v${PV}/recorder.default;downloadfilename=edgefirst-recorder.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/recorder/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-recorder.service \
"
SRC_URI[license.sha256sum] = "9d16bcb298eb6c97e272522a37cbd3b07bec66d77c0e829fdec9fb98185a2876"
SRC_URI[default.sha256sum] = "8bc7cc8c859db9cab2573b5da8b6244655e04eaaf16e8ab9db06e514e2d3c6e6"

BINARY_SHA256SUM[aarch64] = "286a3e843caf1316aeb08c82ed1b0d7a462eb24a8b021b1774dbdcd578f1e1a1"
BINARY_SHA256SUM[x86_64] = "5ba0bcbbf89188a89edefb17a6a58e5d6d5e2f9092e30470da072e8b584c807f"

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

    install -m 0644 ${S}/edgefirst-recorder.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-recorder.default ${D}${sysconfdir}/default/edgefirst-recorder
    install -m 0755 ${S}/edgefirst-recorder ${D}${bindir}/edgefirst-recorder
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-recorder.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
