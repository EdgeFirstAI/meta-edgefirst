SUMMARY = "EdgeFirst libcamera global configuration"
DESCRIPTION = "libcamera's global configuration file, which selects the order \
pipeline handlers are matched in. Without it an i.MX 95 camera is claimed by \
the imx8-isi handler and delivers raw Bayer, because that handler is \
registered before nxp/neo and matching stops at the first handler that claims \
the camera."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://configuration.yaml"

# libcamera reads ${sysconfdir}/libcamera/configuration.yaml first, then its
# own copy under ${datadir}/libcamera, so this takes precedence without
# touching anything the libcamera recipe installs.
COMPATIBLE_MACHINE = "(mx95-nxp-bsp)"

do_install() {
    install -d ${D}${sysconfdir}/libcamera
    install -m 0644 ${UNPACKDIR}/configuration.yaml ${D}${sysconfdir}/libcamera/configuration.yaml
}

FILES:${PN} = "${sysconfdir}/libcamera"
