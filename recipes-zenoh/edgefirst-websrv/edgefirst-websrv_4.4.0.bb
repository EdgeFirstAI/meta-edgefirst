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
SRC_URI[default.sha256sum] = "c6b6b052a319153938cf4dcb40e4dcdee61eccd81a724a6a696a0ecb6b38edbb"

BINARY_SHA256SUM[aarch64] = "d545d7a23251b5dcc72e1c5c59bef4adf34e083dee82802c5071007956dbea01"
BINARY_SHA256SUM[x86_64] = "2ade6067731b8ec05be5b1db02e44c219ec5536e9d7b95e31dbcb4b41ea9ea9a"

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
