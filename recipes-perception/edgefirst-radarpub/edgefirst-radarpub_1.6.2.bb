DESCRIPTION = "EdgeFirst Radar Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/radarpub/releases/download/v${PV}/edgefirst-radarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-radarpub;name=radarpub \
    https://github.com/EdgeFirstAI/radarpub/releases/download/v${PV}/drvegrdctl-linux-${TARGET_ARCH};downloadfilename=drvegrdctl;name=drvegrdctl \
    https://raw.githubusercontent.com/EdgeFirstAI/radarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-radarpub.service \
    file://edgefirst-radarpub.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

RADARPUB_SHA256SUM[aarch64] = "5a0cba65d34bbb1562dbe0e1ae7180f9b80f3bdae2bded4198090121d7418f52"
RADARPUB_SHA256SUM[x86_64] = "d323c6a18539cf5492bb31b7df1d5f9cafc96ec6004693d19704636865ff6291"

DRVEGRDCTL_SHA256SUM[aarch64] = "c0be0145f191a6e05730830f4aefedcb51a26c9dac49138b3aaad9831a833ea6"
DRVEGRDCTL_SHA256SUM[x86_64] = "06ab727ce73b6716e97f688f6bf8edf76e44ba08f1ea028ecd829b338ad41ee2"

python () {
    arch = d.getVar('TARGET_ARCH')
    radarpub_sha256 = d.getVarFlag('RADARPUB_SHA256SUM', arch)
    drvegrdctl_sha256 = d.getVarFlag('DRVEGRDCTL_SHA256SUM', arch)
    if radarpub_sha256:
        d.setVarFlag('SRC_URI', 'radarpub.sha256sum', radarpub_sha256)
    if drvegrdctl_sha256:
        d.setVarFlag('SRC_URI', 'drvegrdctl.sha256sum', drvegrdctl_sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-radarpub.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/edgefirst-radarpub.default ${D}${sysconfdir}/default/edgefirst-radarpub
        install -m 0755 ${UNPACKDIR}/edgefirst-radarpub ${D}${bindir}/edgefirst-radarpub
        install -m 0755 ${UNPACKDIR}/drvegrdctl ${D}${bindir}/drvegrdctl
    else
        install -m 0644 ${WORKDIR}/edgefirst-radarpub.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/edgefirst-radarpub.default ${D}${sysconfdir}/default/edgefirst-radarpub
        install -m 0755 ${WORKDIR}/edgefirst-radarpub ${D}${bindir}/edgefirst-radarpub
        install -m 0755 ${WORKDIR}/drvegrdctl ${D}${bindir}/drvegrdctl
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-radarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
