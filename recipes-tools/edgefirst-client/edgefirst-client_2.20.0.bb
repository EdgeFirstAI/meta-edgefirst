SUMMARY = "EdgeFirst Studio command line client"
DESCRIPTION = "Command line client for EdgeFirst Studio: dataset, snapshot and \
training session management from the target. The -python package carries the \
Python SDK (import edgefirst_client)."
HOMEPAGE = "https://github.com/EdgeFirstAI/client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

COMPATIBLE_HOST = "aarch64.*-linux"

SRC_URI = "\
    https://github.com/EdgeFirstAI/client/releases/download/v${PV}/edgefirst-client-linux-arm64-${PV}.tar.gz;name=binary \
    https://github.com/EdgeFirstAI/client/releases/download/v${PV}/edgefirst_client-${PV}-cp38-abi3-manylinux_2_17_${TARGET_ARCH}.manylinux2014_${TARGET_ARCH}.whl;name=wheel \
    https://raw.githubusercontent.com/EdgeFirstAI/client/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[binary.sha256sum] = "5687c11050b6827f34ca22af133abdf2bd3be67c48d48748afc7247a4577f956"
SRC_URI[wheel.sha256sum] = "52dded5206f986fec5f1b308ed260f7e9872d2a2ad50297e2b7ea11c1ef035b7"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "unzip-native"
PACKAGES = "${PN}-python ${PN}"
RDEPENDS:${PN}-python = "python3"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/edgefirst-client-linux-arm64 ${D}${bindir}/edgefirst-client

    # The wheel's bundled CLI script is not used: the standalone binary above
    # is the CLI.
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip -q ${S}/edgefirst_client-${PV}-cp38-abi3-manylinux_2_17_${TARGET_ARCH}.manylinux2014_${TARGET_ARCH}.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
    rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/edgefirst_client-${PV}.data
}

INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN}-python += "already-stripped"

FILES:${PN} += "${bindir}"
FILES:${PN}-python += "${PYTHON_SITEPACKAGES_DIR}"
