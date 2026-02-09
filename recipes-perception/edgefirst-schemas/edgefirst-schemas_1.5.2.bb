SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/29/ca/c97d66c12564b826eb4dc1d58de0b1a256b76588932bc87363202fb69b70/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "0cc9cc3fb22f10e5d9b475b19a0a274ba32026819a28d609966db64d6889fc76"

CLIB_SHA256SUM[aarch64] = "cd0c3fa39b6f451c7f6fdaee41e30b836669bfb3abb7afe5f38d8fa13607f063"
CLIB_SHA256SUM[x86_64] = "977dc4da35da7382cf79471b2f1b39209063593938e764f54ca8b01d5dab75e3"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('CLIB_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'clib.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit python3-dir

DEPENDS = "python3 python3-pip-native unzip-native"
RDEPENDS:${PN}-python = "python3"

do_install() {
    # Install shared library
    install -d ${D}${libdir}
    install -m 0755 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.so ${D}${libdir}/libedgefirst_schemas.so.${PV}
    ln -sf libedgefirst_schemas.so.${PV} ${D}${libdir}/libedgefirst_schemas.so

    # Install static library
    install -m 0644 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.a ${D}${libdir}/

    # Install headers
    install -d ${D}${includedir}/edgefirst
    install -m 0644 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/include/edgefirst/schemas.h ${D}${includedir}/edgefirst/

    # Install Python wheel
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${UNPACKDIR}/edgefirst_schemas-${PV}-py3-none-any.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-python"

INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-python += "ldflags"

FILES:${PN} = "${libdir}/libedgefirst_schemas.so.*"
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_schemas.so"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_schemas.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
