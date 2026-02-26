DESCRIPTION = "EdgeFirst LiDAR Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/edgefirst-lidarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-lidarpub;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/lidarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-lidarpub.service \
    file://edgefirst-lidarpub.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "f96df0f29be27bdc3241e5f833aa2b1e344812e99affc759443f388dae97fff0"
BINARY_SHA256SUM[x86_64] = "6a172475cf80fc42d8fdaaee60db8d4520571c58512bd9cbda49290e3b77ff8b"

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

    install -m 0644 ${S}/edgefirst-lidarpub.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-lidarpub.default ${D}${sysconfdir}/default/edgefirst-lidarpub
    install -m 0755 ${S}/edgefirst-lidarpub ${D}${bindir}/edgefirst-lidarpub
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-lidarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
