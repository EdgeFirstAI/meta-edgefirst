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
SRC_URI[default.sha256sum] = "c3184a984bece1cb4bd91523511787e0e3eaefafa7368273392db3bee98e7ef3"

BINARY_SHA256SUM[aarch64] = "d456764733a00d10b7e65a5a2860ab8e39f338c1ea4f07150fb475c0320afeba"
BINARY_SHA256SUM[x86_64] = "833fd7d58af68ca08fa6c8d173724c31ce60e322eac0881ec00eb979172764a0"

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
