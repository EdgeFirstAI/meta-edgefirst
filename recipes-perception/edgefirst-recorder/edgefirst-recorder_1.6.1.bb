DESCRIPTION = "EdgeFirst MCAP Recorder"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=dd1425eba06ca7b09230155041834ed7"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/recorder/releases/download/v${PV}/edgefirst-recorder-v${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-recorder;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/recorder/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-recorder.service \
    file://edgefirst-recorder.default \
"
SRC_URI[license.sha256sum] = "9d16bcb298eb6c97e272522a37cbd3b07bec66d77c0e829fdec9fb98185a2876"

BINARY_SHA256SUM[aarch64] = "0f3a6f4d3d3c3a2c236f6109b9bb171babd6bdd442b841cba4376072c75f2b1e"
BINARY_SHA256SUM[x86_64] = "d428e10d1fbea2cbbfa59f2181823481a3e8248b5042b718d15e217b74045807"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-recorder.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/edgefirst-recorder.default ${D}${sysconfdir}/default/edgefirst-recorder
        install -m 0755 ${UNPACKDIR}/edgefirst-recorder ${D}${bindir}/edgefirst-recorder
    else
        install -m 0644 ${WORKDIR}/edgefirst-recorder.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/edgefirst-recorder.default ${D}${sysconfdir}/default/edgefirst-recorder
        install -m 0755 ${WORKDIR}/edgefirst-recorder ${D}${bindir}/edgefirst-recorder
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-recorder.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
