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

BINARY_SHA256SUM[aarch64] = "8afb0df74cc2853cc1fd9e31157f9ad6001b50d4d0a9e009883617e6dd59eeb3"
BINARY_SHA256SUM[x86_64] = "6e26f475adb591d19772fda08a7fdd1265467b5d9d9ceb66c90ac6277f6ac5aa"

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
