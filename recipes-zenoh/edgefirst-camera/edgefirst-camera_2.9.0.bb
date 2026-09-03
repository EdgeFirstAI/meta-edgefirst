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
SRC_URI[default.sha256sum] = "939433a0710b5d7d924acb1096a79f5e34e828d5cbaa15a22a5cfd9204cf070b"

BINARY_SHA256SUM[aarch64] = "628e7708831bb606974317f686cb76b80816094fc39d0c56b28bc1bd1b180c27"
BINARY_SHA256SUM[x86_64] = "fe9ada5b170cc3d51785a8b7dc90facc8cb0246d926c055581fc7d14ca72f577"

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
