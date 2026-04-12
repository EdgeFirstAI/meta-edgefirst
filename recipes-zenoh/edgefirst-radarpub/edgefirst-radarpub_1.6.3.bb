DESCRIPTION = "EdgeFirst Radar Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/radarpub/releases/download/v${PV}/edgefirst-radarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-radarpub;name=radarpub \
    https://github.com/EdgeFirstAI/radarpub/releases/download/v${PV}/drvegrdctl-linux-${TARGET_ARCH};downloadfilename=drvegrdctl;name=drvegrdctl \
    https://github.com/EdgeFirstAI/radarpub/releases/download/v${PV}/radarpub.default;downloadfilename=edgefirst-radarpub.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/radarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-radarpub.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "6699cc763a26dc003b7a3c2e48cc1b70d39d3d3fa365ad9c6482c1519967a9cc"

RADARPUB_SHA256SUM[aarch64] = "e0028c6a0e363b6d858037ff6383ecdc563ed7cea2a90f648e0b53b3c340d272"
RADARPUB_SHA256SUM[x86_64] = "3f8457c9384ac236b21cede42693b3abb75d120ecae1d42b2cc6878f74ab0ef7"

DRVEGRDCTL_SHA256SUM[aarch64] = "6f7435300655ed13b2b85bd3199b6ec6bf9b183dc6bb066272a2e2e3fe857396"
DRVEGRDCTL_SHA256SUM[x86_64] = "1eb97ce2a4b9c83e0547aac3a5100a431c9ef922ebd8dab56f9304fc82538648"

python () {
    arch = d.getVar('TARGET_ARCH')
    radarpub_sha256 = d.getVarFlag('RADARPUB_SHA256SUM', arch)
    drvegrdctl_sha256 = d.getVarFlag('DRVEGRDCTL_SHA256SUM', arch)
    if radarpub_sha256:
        d.setVarFlag('SRC_URI', 'radarpub.sha256sum', radarpub_sha256)
    if drvegrdctl_sha256:
        d.setVarFlag('SRC_URI', 'drvegrdctl.sha256sum', drvegrdctl_sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-radarpub.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-radarpub.default ${D}${sysconfdir}/default/edgefirst-radarpub
    install -m 0755 ${S}/edgefirst-radarpub ${D}${bindir}/edgefirst-radarpub
    install -m 0755 ${S}/drvegrdctl ${D}${bindir}/drvegrdctl
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-radarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
