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
SRC_URI[default.sha256sum] = "b07c962aff1d252db93530cadba92560c96160391db9b41531042ee2c9a6d954"

RADARPUB_SHA256SUM[aarch64] = "053ddc9a1487a2995e452c0f618e1870254f71649b5413fc68ea858503261039"
RADARPUB_SHA256SUM[x86_64] = "ea30900811d11e8893b654bd6b6a586e55d67922318145501ca0b47612f71c38"

DRVEGRDCTL_SHA256SUM[aarch64] = "5dbba4a758ce5ad1a45b9dc5ab63ef4602cd670dccffa99c7055f84cee924fed"
DRVEGRDCTL_SHA256SUM[x86_64] = "806e85fea54c64661df96f9e82788a147399f9286614e698a75de721a26ac4e6"

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
