DESCRIPTION = "EdgeFirst LiDAR Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/edgefirst-lidarpub-linux-${TARGET_ARCH};downloadfilename=edgefirst-lidarpub;name=binary \
    https://github.com/EdgeFirstAI/lidarpub/releases/download/v${PV}/lidarpub.default;downloadfilename=edgefirst-lidarpub.default;name=default \
    https://raw.githubusercontent.com/EdgeFirstAI/lidarpub/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
    file://edgefirst-lidarpub.service \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[default.sha256sum] = "b56ec5b0518c3b5f29ee717cf64fe5adb97bde371e75de3e1a611bc3d917534c"

BINARY_SHA256SUM[aarch64] = "b46710cc366dc8516ac5ca5e244e9e3c8909a36178b6e7611036529d2daae8ff"
BINARY_SHA256SUM[x86_64] = "b6e1c43722d4af0ebf8c68828f59d13be8fa17a49bad1ae2704b0735196b389f"

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

    install -m 0644 ${S}/edgefirst-lidarpub.service ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/edgefirst-lidarpub.default ${D}${sysconfdir}/default/edgefirst-lidarpub
    install -m 0755 ${S}/edgefirst-lidarpub ${D}${bindir}/edgefirst-lidarpub
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "edgefirst-lidarpub.service"
SYSTEMD_AUTO_ENABLE = "disable"

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${sysconfdir}"
FILES:${PN} += "${bindir}"
