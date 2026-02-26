DESCRIPTION = "EdgeFirst IMU Service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=20f602f9b9b48d7f30f28541298ef146"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/imu/releases/download/v${PV}/edgefirst-imu-linux-${TARGET_ARCH};downloadfilename=edgefirst-imu;name=binary \
    https://github.com/EdgeFirstAI/imu/releases/download/v${PV}/imu.default;downloadfilename=edgefirst-imu.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/imu/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-imu.service \
"
SRC_URI[license.sha256sum] = "b075434d900a00caf30566e8efc74b1a0ce26e0f400c5323287f97f1931ee2a9"
SRC_URI[default.sha256sum] = "703cb3a750f19f7bbadb2c7cb4324cea58702cfeb1e39454fa4271500a20f95b"

BINARY_SHA256SUM[aarch64] = "c40ef918ff1b0ae0cecbc76db0c1cc367e950dee53beddbab5edebf6086dfaba"
BINARY_SHA256SUM[x86_64] = "879b27beec3e40c07a1b309df77feae6115388e995a6d1934bd54d60d2e5bc32"

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

    install -m 0644 ${S}/edgefirst-imu.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-imu.default ${D}${sysconfdir}/default/edgefirst-imu
    install -m 0755 ${S}/edgefirst-imu ${D}${bindir}/edgefirst-imu
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-imu.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
