DESCRIPTION = "EdgeFirst Camera Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/camera/releases/download/v${PV}/edgefirst-camera-linux-${TARGET_ARCH};downloadfilename=edgefirst-camera;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/camera/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-camera.service \
    file://edgefirst-camera.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "49fa0e58d04a93ddefc02b06bdf34ae239adda34e0f3f36c699e03c5e74a1961"
BINARY_SHA256SUM[x86_64] = "c4bb4a65b4b6e824c9f6df5c2739d27b98a780307277c0937f374f8d32eaec9c"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

DEPENDS = "videostream"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/edgefirst-camera.service ${D}${systemd_system_unitdir}

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/edgefirst-camera.default ${D}${sysconfdir}/default/edgefirst-camera

    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/edgefirst-camera ${D}${bindir}/edgefirst-camera
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-camera.service"
SYSTEMD_AUTO_ENABLE = "enable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${bindir}"
FILES:${PN} += "${sysconfdir}"
