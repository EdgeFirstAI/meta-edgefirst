DESCRIPTION = "EdgeFirst IMU Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=20f602f9b9b48d7f30f28541298ef146"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/imu/releases/download/v${PV}/edgefirst-imu-linux-${TARGET_ARCH};downloadfilename=edgefirst-imu;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/imu/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-imu.service \
"
SRC_URI[license.sha256sum] = "b075434d900a00caf30566e8efc74b1a0ce26e0f400c5323287f97f1931ee2a9"

BINARY_SHA256SUM[aarch64] = "f286bd5a4ed60945768de3ff407a27f5b906be1d6a31cfb18521b5286454752f"
BINARY_SHA256SUM[x86_64] = "b6f5d2b738d2533e37f3656130a9d48f536dafdb9d87d3c5270eeaa478759752"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${bindir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/edgefirst-imu.service ${D}${systemd_system_unitdir}
        install -m 0755 ${UNPACKDIR}/edgefirst-imu ${D}${bindir}/edgefirst-imu
    else
        install -m 0644 ${WORKDIR}/edgefirst-imu.service ${D}${systemd_system_unitdir}
        install -m 0755 ${WORKDIR}/edgefirst-imu ${D}${bindir}/edgefirst-imu
    fi
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-imu.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${bindir}"
