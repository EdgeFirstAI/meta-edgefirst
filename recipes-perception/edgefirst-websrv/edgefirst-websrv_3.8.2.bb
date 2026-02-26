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
SRC_URI[default.sha256sum] = "567937c736f95609930b84b2b7673151fdd47075361c25608d344503f1ecca7e"

BINARY_SHA256SUM[aarch64] = "f075722e997807c30444162b329d92693af9cf55a8a69fef1cab632004f867fe"
BINARY_SHA256SUM[x86_64] = "94e44614dbabcdbf2f9a98f09a7d1a47671b96b814a704b3c74e826817c1f5c5"

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
