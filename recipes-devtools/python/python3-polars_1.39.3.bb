SUMMARY = "Polars DataFrame library"
DESCRIPTION = "The Python package for Polars, installed from the pure-Python \
wheel. The native code is in python3-polars-runtime-32."
HOMEPAGE = "https://pola.rs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "https://files.pythonhosted.org/packages/b4/db/08f4ca10c5018813e7e0b59e4472302328b3d2ab1512f5a2157a814540e0/polars-${PV}-py3-none-any.whl"
SRC_URI[sha256sum] = "c2b955ccc0a08a2bc9259785decf3d5c007b489b523bf2390cf21cec2bb82a56"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "unzip-native"
RDEPENDS:${PN} = "python3-modules python3-polars-runtime-32"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip -q ${S}/polars-${PV}-py3-none-any.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"
