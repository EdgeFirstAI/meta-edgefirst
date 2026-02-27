DESCRIPTION = "EdgeFirst Camera Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/camera/releases/download/v${PV}/edgefirst-camera-linux-${TARGET_ARCH};downloadfilename=edgefirst-camera;name=binary \
    https://github.com/EdgeFirstAI/camera/releases/download/v${PV}/camera.default;downloadfilename=edgefirst-camera.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/camera/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-camera.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "9d82f762c5260e2a720af5de136372dd207086b9f25030a49611edfe044c7e35"

BINARY_SHA256SUM[aarch64] = "bac031f12b314c4b3b82791806435389ce7e335823c9badd298164a22c409257"
BINARY_SHA256SUM[x86_64] = "cc77a8ff4c6269eeb78f11e2f840bdb96f847972891e505acf40e406f3fae19f"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

DEPENDS = "videostream"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    install -m 0644 ${S}/edgefirst-camera.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-camera.default ${D}${sysconfdir}/default/edgefirst-camera
    install -m 0755 ${S}/edgefirst-camera ${D}${bindir}/edgefirst-camera
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-camera.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${bindir}"
FILES:${PN} += "${sysconfdir}"
