SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/c2/bf/bbb5fa4b016c904ce81931c6d37ab1f3d613c563e2a7f218a2799ad5ecf7/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "09c28396ef9f9a1420fb7438229092b064fa904c1fd49c2e156685f2aa7085a1"

CLIB_SHA256SUM[aarch64] = "7548fc59b1e40d8df45d5f7b8616c11590b41293507989ed7b810b4bacce9113"
CLIB_SHA256SUM[x86_64] = "4d6c02e4cc2d807bcff5331c9f8f7eb4a8a1201846e4c2460d6a167c0c65e517"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('CLIB_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'clib.sha256sum', sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 python3-pip-native unzip-native"
RDEPENDS:${PN}-python = "python3"

do_install() {
    # Install shared library (versioned)
    install -d ${D}${libdir}
    install -m 0755 ${S}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.so.${PV} ${D}${libdir}/
    ln -sf libedgefirst_schemas.so.${PV} ${D}${libdir}/libedgefirst_schemas.so.1
    ln -sf libedgefirst_schemas.so.${PV} ${D}${libdir}/libedgefirst_schemas.so

    # Install static library
    install -m 0644 ${S}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.a ${D}${libdir}/

    # Install pkg-config file
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/pkgconfig/edgefirst-schemas.pc ${D}${libdir}/pkgconfig/

    # Install headers
    install -d ${D}${includedir}/edgefirst
    install -m 0644 ${S}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/include/edgefirst/schemas.h ${D}${includedir}/edgefirst/

    # Install Python wheel
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip ${S}/edgefirst_schemas-${PV}-py3-none-any.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-python"

INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-python += "ldflags"

FILES:${PN} = "${libdir}/libedgefirst_schemas.so.*"
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_schemas.so ${libdir}/pkgconfig"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_schemas.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
