SUMMARY = "Python support for Zenoh"
HOMEPAGE = "https://github.com/eclipse-zenoh/zenoh-python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://files.pythonhosted.org/packages/07/d9/cfe6b59de5b3d139ceb6277afef0f0a505524eb83bf1295d449edb53d641/eclipse_zenoh-${PV}-pp310-pypy310_pp73-manylinux_2_28_aarch64.whl"
SRC_URI[sha256sum] = "e7f9bf3849567631caa3bdeecf2edce66461b1ae511f28dfafea6089022d02e1"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit python3-dir

DEPENDS = "python3 python3-pip-native"
RDEPENDS:${PN} = "python3"

do_install() {
    mkdir -p ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${UNPACKDIR}/eclipse_zenoh-${PV}-pp310-pypy310_pp73-manylinux_2_28_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

do_install[depends] += "unzip-native:do_populate_sysroot"

INSANE_SKIP:${PN} += "ldflags"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"
