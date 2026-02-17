SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/e6/a9/f76c2044fdc65165e1cb740a887ef2799ccca93248062918ff0385dd2110/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "a2054c8d412e77380b806d26713ee84f4d9acfc74346527837e62071f6d71e09"

CLIB_SHA256SUM[aarch64] = "00eb9560a5b211324de77c178d7396212dc2e316085a20e83dd2d596529ab48b"
CLIB_SHA256SUM[x86_64] = "d2b44b614c8ab9b7cf8b018ec5a8dcc44e171a9c2b927c3a6ada2619974a4d42"

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
    # Install shared library (versioned)
    install -d ${D}${libdir}
    install -m 0755 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.so.${PV} ${D}${libdir}/
    ln -sf libedgefirst_schemas.so.${PV} ${D}${libdir}/libedgefirst_schemas.so.1
    ln -sf libedgefirst_schemas.so.${PV} ${D}${libdir}/libedgefirst_schemas.so

    # Install static library
    install -m 0644 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/libedgefirst_schemas.a ${D}${libdir}/

    # Install pkg-config file
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${UNPACKDIR}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/pkgconfig/edgefirst-schemas.pc ${D}${libdir}/pkgconfig/

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
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_schemas.so ${libdir}/pkgconfig"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_schemas.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
