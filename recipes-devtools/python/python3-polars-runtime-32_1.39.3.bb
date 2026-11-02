SUMMARY = "Polars compiled runtime (32-bit row index)"
DESCRIPTION = "Native extension that python3-polars loads. Installed from the \
published manylinux wheel, since there is no Rust build for it here."
HOMEPAGE = "https://pola.rs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_HOST = "aarch64.*-linux"

SRC_URI = "https://files.pythonhosted.org/packages/3d/3e/e65236d9d0d9babfa0ecba593413c06530fca60a8feb8f66243aa5dba92e/polars_runtime_32-${PV}-cp310-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl"
SRC_URI[sha256sum] = "06b47f535eb1f97a9a1e5b0053ef50db3a4276e241178e37bbb1a38b1fa53b14"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "unzip-native"
RDEPENDS:${PN} = "python3-core"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip -q ${S}/polars_runtime_32-${PV}-cp310-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"
