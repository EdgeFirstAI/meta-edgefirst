DESCRIPTION = "EdgeFirst Web UI Server"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=2c3ca8524a356ce12f8ec8ea10d087cd"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/websrv/releases/download/v${PV}/edgefirst-websrv-${PV}-linux-${TARGET_ARCH};downloadfilename=edgefirst-websrv;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/websrv/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-websrv.service \
    file://edgefirst-websrv.default \
"
SRC_URI[license.sha256sum] = "dfa98e540b9ecff38ba4f9656a86dbca2de4f7b39eecab086faa4287d2ba8310"

BINARY_SHA256SUM[aarch64] = "e40fa9267833398609e466b792ab2082be22536e52aab339aab69f2b5ceb2f1f"
BINARY_SHA256SUM[x86_64] = "c798bfdce34b8d30ad3ce16c18a0b9d4e228fb7a6677efe1a2c55805b4f93868"

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
