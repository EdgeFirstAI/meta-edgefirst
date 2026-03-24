DESCRIPTION = "Zero Overhead Network Protocol"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://github.com/eclipse-zenoh/zenoh/releases/download/${PV}/zenoh-${PV}-${TARGET_ARCH}-unknown-linux-gnu-standalone.zip;name=standalone \
    file://zenohd.service \
    file://zenohd.default \
    file://zenohd.yaml \
"

STANDALONE_SHA256SUM[aarch64] = "78d884068d76843f191e692734b25fb4d70648b4fa14f91c0c82b2b6a2427485"
STANDALONE_SHA256SUM[x86_64] = "bc5a816d49ba0b0a1e9479bef20ee1ebdcb67d21ec78e1cf1b48fd3e4c4cfd60"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('STANDALONE_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'standalone.sha256sum', sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit features_check systemd

do_install[depends] += "unzip-native:do_populate_sysroot"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${systemd_system_unitdir}

    install -m 0644 ${S}/zenohd.default ${D}${sysconfdir}/default/zenohd
    install -m 0644 ${S}/zenohd.yaml ${D}${sysconfdir}
    install -m 0644 ${S}/zenohd.service ${D}${systemd_system_unitdir}

    ${bindir}/env unzip -q -o ${DL_DIR}/zenoh-${PV}-${TARGET_ARCH}-unknown-linux-gnu-standalone.zip -d ${D}${libdir}
    mv ${D}${libdir}/zenohd ${D}${bindir}
}

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "zenohd.service"
SYSTEMD_AUTO_ENABLE = "disable"

FILES:${PN}-dev = ""
FILES:${PN} += "${bindir}/zenohd"
FILES:${PN} += "${libdir}/libzenoh_plugin_storage_manager.so"
FILES:${PN} += "${libdir}/libzenoh_plugin_rest.so"
FILES:${PN} += "${sysconfdir}/default/zenohd"
FILES:${PN} += "${sysconfdir}/zenohd.yaml"
FILES:${PN} += "${systemd_system_unitdir}"
