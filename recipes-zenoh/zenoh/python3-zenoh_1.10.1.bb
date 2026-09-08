SUMMARY = "Python support for Zenoh"
HOMEPAGE = "https://github.com/eclipse-zenoh/zenoh-python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://files.pythonhosted.org/packages/d0/21/976cd9e4091362bc25ed5031f229d72032363c611bd5260950284583861e/eclipse_zenoh-${PV}-cp39-abi3-manylinux_2_28_aarch64.whl"
SRC_URI[sha256sum] = "1950fdc8de72e5a3847d845d57e72dedf35275725e42be46a26454cfabb54c8c"

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
