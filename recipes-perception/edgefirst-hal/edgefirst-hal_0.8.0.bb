SUMMARY = "EdgeFirst HAL C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/hal/releases/download/v${PV}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux.tar.gz;name=clib \
    https://raw.githubusercontent.com/EdgeFirstAI/hal/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

# Python wheel available for aarch64 (target) from PyPI
SRC_URI:append:aarch64 = " \
    https://files.pythonhosted.org/packages/7b/a7/3116d388d8f5b0d1184cba8df1b5f33b321a878c55b3a645cd146cd978c8/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
"
SRC_URI[python.sha256sum] = "741181d0ffc3303bb97aa73d1a3efb24f56bc038301592d0c2269960d4136276"

CLIB_SHA256SUM[aarch64] = "b706825712f9e2a5fa879fa4ad532bc8d532fcfac21f621e0cf1a3b017196bce"
CLIB_SHA256SUM[x86_64] = "af9fa45636bc5e9108a131f719cb771f3bcad90f9cc8cbc316d7a1c868a678c6"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('CLIB_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'clib.sha256sum', sha256)
}

S = "${WORKDIR}"

inherit python3-dir

DEPENDS = "python3 unzip-native"
RDEPENDS:${PN}-python = "python3"

do_install() {
    if [ "${UNPACKDIR}" != "" ]; then
        SRCPATH="${UNPACKDIR}"
    else
        SRCPATH="${WORKDIR}"
    fi

    # Install shared library with proper SONAME symlinks
    install -d ${D}${libdir}
    install -m 0755 ${SRCPATH}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/lib/libedgefirst_hal.so ${D}${libdir}/libedgefirst_hal.so.${PV}
    ln -sf libedgefirst_hal.so.${PV} ${D}${libdir}/libedgefirst_hal.so.0
    ln -sf libedgefirst_hal.so.${PV} ${D}${libdir}/libedgefirst_hal.so

    # Install static library
    install -m 0644 ${SRCPATH}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/lib/libedgefirst_hal.a ${D}${libdir}/

    # Install headers
    install -d ${D}${includedir}/edgefirst
    install -m 0644 ${SRCPATH}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/include/edgefirst/hal.h ${D}${includedir}/edgefirst/

    # Install pkg-config file
    install -d ${D}${libdir}/pkgconfig
    cat > ${D}${libdir}/pkgconfig/edgefirst-hal.pc <<PKGEOF
prefix=${prefix}
exec_prefix=\${prefix}
libdir=\${prefix}/lib
includedir=\${prefix}/include

Name: edgefirst-hal
Description: EdgeFirst HAL C Library
Version: ${PV}
Libs: -L\${libdir} -ledgefirst_hal
Cflags: -I\${includedir}
PKGEOF

    # Install Python wheel (aarch64 from PyPI)
    if [ -f ${SRCPATH}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl ]; then
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        unzip ${SRCPATH}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
    fi
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-python"

INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-python += "ldflags"

FILES:${PN} = "${libdir}/libedgefirst_hal.so.*"
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_hal.so ${libdir}/pkgconfig"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_hal.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

ALLOW_EMPTY:${PN}-python = "1"
