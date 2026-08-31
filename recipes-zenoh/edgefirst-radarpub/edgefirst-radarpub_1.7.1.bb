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

RADARPUB_SHA256SUM[aarch64] = "18b9b61d308fbe1a8377d73500b75501aadbb888e3a072482bfa661bb02dfaec"
RADARPUB_SHA256SUM[x86_64] = "a9ec0aded7326ec1c6531d7f69a00af2c2e42aefcd2746d70374b961235e5818"

DRVEGRDCTL_SHA256SUM[aarch64] = "c4a213ee80a2c54c973a9fbbcbe9494cf6b992c82f4b2b1c6414be7688eade04"
DRVEGRDCTL_SHA256SUM[x86_64] = "8236601d728311a084e02318048050b4c54ff2d0566d0a022fa7d0178083df2e"

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
