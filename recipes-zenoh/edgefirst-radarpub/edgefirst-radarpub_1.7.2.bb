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
SRC_URI[default.sha256sum] = "7e7c387172bee24589262fb281655bb07b76e9340cb33c5d02d3a9bf2c0b8675"

RADARPUB_SHA256SUM[aarch64] = "9eb804833d86d288ced2b4205a45204f8d7c9b094050cfd6fd81907c89e1247a"
RADARPUB_SHA256SUM[x86_64] = "a4d9b9fb0cf2864100d374b2a8cb069d28129990f789d35a85bb851b8311c529"

DRVEGRDCTL_SHA256SUM[aarch64] = "c26711c11e1dd893e87df610146acd4963d748b04b4889e8b367f95ad3a9ca85"
DRVEGRDCTL_SHA256SUM[x86_64] = "f3151f7b6eb667c4136a6903e1d6bcc2593bb220cd979ed3e013f9d07b91c462"

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
