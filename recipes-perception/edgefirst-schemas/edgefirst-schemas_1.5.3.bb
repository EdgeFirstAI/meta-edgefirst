SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/dc/3d/1a8db4cb0ddcd8cc21834b5a2ad6bededf6b5031acd4513b487133566eb4/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "2b3c1be71fc4d04b2d873d063373f10162602036e7645a1de16c80e6c02ed5cd"

CLIB_SHA256SUM[aarch64] = "4e7be9233eb724eb2d4eca55a307fcdb45ecf369a69fd35482e6d721c8afc184"
CLIB_SHA256SUM[x86_64] = "28951fd19f65351dd211415de8472a5a6f853fb6d4d68f82aeab81163cc0aea5"

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
