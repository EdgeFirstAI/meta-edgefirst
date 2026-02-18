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
    https://files.pythonhosted.org/packages/6e/c8/46ca9863f0fb0e0e45113b112d864d8579da49ca0a844c4d2f0d5d901e6b/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_x86_64.manylinux2014_x86_64.whl;name=python \
"
SRC_URI[python.sha256sum] = "fd19ff3e35fcc3f54fa08aeb5fdc57b39199fee5d090ce902e0a34e1d783006e"

CLIB_SHA256SUM[aarch64] = "9804e068ec0aaecd68ca7f9227f6a23f63fea07adfbaea0cd7770ba0a4353be9"
CLIB_SHA256SUM[x86_64] = "96c2b917c7c826613d79aa48831fa79b9f7d8fd4cf48cdbb6456f055ccf5a841"

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
    if [ -f ${UNPACKDIR}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_x86_64.manylinux2014_x86_64.whl ]; then
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        unzip ${UNPACKDIR}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_x86_64.manylinux2014_x86_64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
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
