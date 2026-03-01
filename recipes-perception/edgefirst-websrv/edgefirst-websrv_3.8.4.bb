DESCRIPTION = "EdgeFirst Web UI Server"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=2c3ca8524a356ce12f8ec8ea10d087cd"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/websrv/releases/download/v${PV}/edgefirst-websrv-${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-websrv;name=binary \
    https://github.com/EdgeFirstAI/websrv/releases/download/v${PV}/websrv.default;downloadfilename=edgefirst-websrv.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/websrv/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-websrv.service \
"
SRC_URI[license.sha256sum] = "dfa98e540b9ecff38ba4f9656a86dbca2de4f7b39eecab086faa4287d2ba8310"
SRC_URI[default.sha256sum] = "4c07aadc3fe34e224c9ad16b6cc6fe938ec751b0d2d522d3fc2a15a31f980963"

BINARY_SHA256SUM[aarch64] = "9d59a2e8a7d7cb810b617bb78b7c17992f346e3d0dd1df462564a84931110b0f"
BINARY_SHA256SUM[x86_64] = "9fc07d7828274e8762beb235bc3dc10002e18d1ba49e739c5641536db59bd69d"

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

    install -m 0644 ${S}/edgefirst-websrv.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-websrv.default ${D}${sysconfdir}/default/edgefirst-websrv
    install -m 0755 ${S}/edgefirst-websrv ${D}${bindir}/edgefirst-websrv
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-websrv.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
