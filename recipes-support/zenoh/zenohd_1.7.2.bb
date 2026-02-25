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

STANDALONE_SHA256SUM[aarch64] = "2fcf8415b59a3cb6b529676b789aac7cd442aa850b94e2dcc548be5a4fc3b0b6"
STANDALONE_SHA256SUM[x86_64] = "d59cb50835078bfe3e49e7a3b3041c3dbb48e243a1e853f6012565ac91f1305a"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('STANDALONE_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'standalone.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit features_check systemd

do_install[depends] += "unzip-native:do_populate_sysroot"

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${systemd_system_unitdir}

    if [ "${UNPACKDIR}" != "" ]; then
        install -m 0644 ${UNPACKDIR}/zenohd.default ${D}${sysconfdir}/default/zenohd
        install -m 0644 ${UNPACKDIR}/zenohd.yaml ${D}${sysconfdir}
        install -m 0644 ${UNPACKDIR}/zenohd.service ${D}${systemd_system_unitdir}
    else
        install -m 0644 ${WORKDIR}/zenohd.default ${D}${sysconfdir}/default/zenohd
        install -m 0644 ${WORKDIR}/zenohd.yaml ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/zenohd.service ${D}${systemd_system_unitdir}
    fi

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
