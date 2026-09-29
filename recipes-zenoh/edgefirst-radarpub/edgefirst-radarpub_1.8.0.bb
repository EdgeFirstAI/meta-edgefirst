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
SRC_URI[default.sha256sum] = "0ec49d0abac44c75fae260ab85ec6da9a1981a3f7ca4cbfa2f7bab493c3bb3ee"

RADARPUB_SHA256SUM[aarch64] = "50dcdbd3251fcb23c8d83d5c2aed341034877587b38ba28d18b34e4bc5540456"
RADARPUB_SHA256SUM[x86_64] = "6a38c38e990327f52803e9fd73edb2255366b8218ac81a4f21d1eee4d8663086"

DRVEGRDCTL_SHA256SUM[aarch64] = "1819d6be45e634dbca00be5f6dde7df8d0ca1a5d1cdf3ca3173ad6c84cf74363"
DRVEGRDCTL_SHA256SUM[x86_64] = "b6a19e1ae2212ebb504cc9ae19d3930ceadad13627adb765777803de535ba362"

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
