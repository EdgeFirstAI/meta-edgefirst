SUMMARY = "Python support for Zenoh"
HOMEPAGE = "https://github.com/eclipse-zenoh/zenoh-python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://files.pythonhosted.org/packages/ab/33/c3116f1bf7647ee0ea8972efbe0fe5710ae75ea7226440a8fda7f04a4cbc/eclipse_zenoh-${PV}-cp39-abi3-manylinux_2_28_aarch64.whl"
SRC_URI[sha256sum] = "8c139a43706c8ff3c94fa625008af8667687c161a8395ad1fa3faff29c16fae4"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 python3-pip-native"
RDEPENDS:${PN} = "python3"

do_install() {
    mkdir -p ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${S}/eclipse_zenoh-${PV}-cp39-abi3-manylinux_2_28_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

do_install[depends] += "unzip-native:do_populate_sysroot"

INSANE_SKIP:${PN} += "ldflags"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"
