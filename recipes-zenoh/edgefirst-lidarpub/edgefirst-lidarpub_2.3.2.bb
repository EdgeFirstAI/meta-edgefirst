DESCRIPTION = "EdgeFirst LiDAR Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/edgefirst-lidarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-lidarpub;name=binary \
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/lidarpub.default;downloadfilename=edgefirst-lidarpub.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/lidarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-lidarpub.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "528ce3cb53ec95caea0a8ecb5137c2d0227ad3258dee4adde5590e6a53e6f079"

BINARY_SHA256SUM[aarch64] = "7fc87a576a0465fff6f1cfb20e0364a1c1f206f11098474e5fd9f43736097c67"
BINARY_SHA256SUM[x86_64] = "97e7d5bf6793c2e2c3413576433d84369e957cd0713de342e8a9cda97440988d"

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
