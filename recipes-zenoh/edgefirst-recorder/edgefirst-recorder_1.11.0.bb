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
SRC_URI[default.sha256sum] = "5e7be219427c3465e4d487938449effe313603d1c6b4eb06e8573084e979a608"

BINARY_SHA256SUM[aarch64] = "b8bc99611d15a4e883b019432d5d47709eb426a2b390a97df9f17cd6253c9fd0"
BINARY_SHA256SUM[x86_64] = "60af1947f704df671ad0df5107a0f468e167954c9604000ca9fcd36881152718"

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
