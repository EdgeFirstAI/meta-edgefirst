SUMMARY = "EdgeFirst TFLite Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/tflite-rs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = " \
    https://github.com/EdgeFirstAI/tflite-rs/releases/download/v${PV}/edgefirst_tflite-${PV}-cp38-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/tflite-rs/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30"
SRC_URI[python.sha256sum] = "e5230b3637a00c033d8656d29b3806518e1e1eaa7fefe1ce71d5aefe3690ba20"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 unzip-native"
RDEPENDS:${PN} = "python3"

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${S}/edgefirst_tflite-${PV}-cp38-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl \
        -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

INSANE_SKIP:${PN} += "ldflags already-stripped"
