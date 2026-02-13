SUMMARY = "EdgeFirst HAL C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/hal/releases/download/v${PV}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux.tar.gz;name=clib \
    https://raw.githubusercontent.com/EdgeFirstAI/hal/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

# Python wheel only available for x86_64 (native cpython-311 extension)
SRC_URI:append:x86-64 = " \
    https://files.pythonhosted.org/packages/f8/50/303a02f70e5ddd0960c8429f7a9d6144e5d00d17604dd590da477871b121/edgefirst_hal-${PV}-cp311-cp311-manylinux_2_17_x86_64.manylinux2014_x86_64.whl;name=python \
"
SRC_URI[python.sha256sum] = "14d36dc71b1e6edfe7ff68441b11131c65c8632528d70cb276129285190c50bb"

CLIB_SHA256SUM[aarch64] = "7170e9f509bfbd13042c81b71d1570413a1b126830a0ccf90165cc2eacc52318"
CLIB_SHA256SUM[x86_64] = "958ab1368a8ea3debbb35ad61ff4cca438fb75d6f930a8c1702c906ce9d01e0e"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('CLIB_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'clib.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit python3-dir

DEPENDS = "python3 unzip-native"
RDEPENDS:${PN}-python = "python3"

do_install() {
    # Install shared library
    install -d ${D}${libdir}
    install -m 0755 ${UNPACKDIR}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/lib/libedgefirst_hal.so ${D}${libdir}/libedgefirst_hal.so.${PV}
    ln -sf libedgefirst_hal.so.${PV} ${D}${libdir}/libedgefirst_hal.so

    # Install static library
    install -m 0644 ${UNPACKDIR}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/lib/libedgefirst_hal.a ${D}${libdir}/

    # Install headers
    install -d ${D}${includedir}/edgefirst
    install -m 0644 ${UNPACKDIR}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/include/edgefirst/hal.h ${D}${includedir}/edgefirst/

    # Install Python wheel (x86_64 only)
    if [ -f ${UNPACKDIR}/edgefirst_hal-${PV}-cp311-cp311-manylinux_2_17_x86_64.manylinux2014_x86_64.whl ]; then
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        unzip ${UNPACKDIR}/edgefirst_hal-${PV}-cp311-cp311-manylinux_2_17_x86_64.manylinux2014_x86_64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
    fi
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-python"

INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-python += "ldflags"

FILES:${PN} = "${libdir}/libedgefirst_hal.so.*"
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_hal.so"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_hal.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

ALLOW_EMPTY:${PN}-python = "1"
