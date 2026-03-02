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
SRC_URI[default.sha256sum] = "c8a185a82c7552d44c32972cd84cfbaae346a8e34d103fcac831128c98d32d61"

BINARY_SHA256SUM[aarch64] = "d911de0e6f88f93b8c9e685b3a8273fb8783ef9ef972452b6c13c3e58148846f"
BINARY_SHA256SUM[x86_64] = "825335ebbf4c464894091be77ab6d8b40a8010ff9f64a0c63d174ff3d4c39d73"

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
