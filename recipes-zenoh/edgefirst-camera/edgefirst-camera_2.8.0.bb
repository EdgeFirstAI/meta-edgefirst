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
SRC_URI[default.sha256sum] = "c5155328e9ad61380b130f25e78ce8db9b7b454871e94cecc03e90615822da32"

BINARY_SHA256SUM[aarch64] = "04d8e409fd637c0c986ce92196bbd56ac8d710832ed88da33085cc34d1babe2c"
BINARY_SHA256SUM[x86_64] = "383864a229235408692ccf90a5d17d4a47b0d8af77005d8bd5775f9aaec5e71e"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

DEPENDS = "videostream"
RDEPENDS:${PN} = "videostream"

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
