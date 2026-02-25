DESCRIPTION = "EdgeFirst LiDAR Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/edgefirst-lidarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-lidarpub;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/lidarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-lidarpub.service \
    file://edgefirst-lidarpub.default \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = "ec1fd4dd6ded7fdccfe4569a02d8cca65fbfdb0d9c3f11743632bc51fc2f76ac"
BINARY_SHA256SUM[x86_64] = "8dfaf6f713c37ae371c9c6f9f251cfca50af6a21ce55636f2c380e542dfccaf4"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

S = "${WORKDIR}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-lidarpub.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/edgefirst-lidarpub.default ${D}${sysconfdir}/default/edgefirst-lidarpub
        install -m 0755 ${UNPACKDIR}/edgefirst-lidarpub ${D}${bindir}/edgefirst-lidarpub
    else
        install -m 0644 ${WORKDIR}/edgefirst-lidarpub.service ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/edgefirst-lidarpub.default ${D}${sysconfdir}/default/edgefirst-lidarpub
        install -m 0755 ${WORKDIR}/edgefirst-lidarpub ${D}${bindir}/edgefirst-lidarpub
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-lidarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
