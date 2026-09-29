SUMMARY = "EdgeFirst Ara-2 Python Bindings"
DESCRIPTION = "Python bindings for the Ara-2 NPU runtime built from the \
EdgeFirst ara2-rs Rust crate. Exposes Session/Model APIs with typed \
input/output tensors, qmode-9 dequantization, and InputPreprocess \
metadata for Python applications driving the Ara-2 NPU."
HOMEPAGE = "https://github.com/EdgeFirstAI/ara2-rs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=a62e14fed5f953787e6d281c379a8072"

SRC_URI = " \
    https://github.com/EdgeFirstAI/ara2-rs/releases/download/v${PV}/edgefirst_ara2-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/ara2-rs/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "b8f67b53dc742540e9ea9a8197c0e546a812a2cb39451df3e22cc4112168afe3"
SRC_URI[python.sha256sum] = "0062c08c973c1b6f19dd6287f4fe9428fbdb442b2e945df0f3a1ee1e0b316d38"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 unzip-native"

# libaraclient is loaded at runtime and its DVAPI generation probed, so the
# bindings work with either imx-nxp-ara2 packaging: NXP's rt-sdk-ara2
# (meta-imx-ml) or the Kinara SDK runtime (meta-kinara).
RDEPENDS:${PN} = "python3 python3-numpy imx-nxp-ara2"

do_install() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${S}/edgefirst_ara2-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl \
        -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}"

INSANE_SKIP:${PN} += "ldflags already-stripped"
