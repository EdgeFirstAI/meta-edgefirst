SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/5a/0f/cdf6bfe343d83c7e7249383b2d2b7537686ca8ac9ebc76d8732a1dbefc93/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "4b8f3fcb00bfd8c7a5228dd848e3a1a217f3dcb7ebc97b28300d867b42eebd15"

CLIB_SHA256SUM[aarch64] = "6e3304aec8b9d2a800ded6ae30d6c5531861eba4774874a950ae431f217d5c37"
CLIB_SHA256SUM[x86_64] = "bce967f7c09f3f1730bfe7bc9ccf68684291b9460804df43e5e4e57b288fee2b"

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
    # The upstream tarball ships a correct SONAME symlink chain
    # (libedgefirst_schemas.so → .so.2 → .so.2.2 → .so.2.2.1) plus the
    # static library and pkg-config file. Copy the lib/ tree verbatim
    # with `cp -a` to preserve the symlinks, then reset ownership to
    # root:root since `cp -a` also preserves the host-build uid/gid
    # which the Yocto package-QA rejects.
    install -d ${D}${libdir}
    cp -a ${S}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}/lib/. ${D}${libdir}/
    chown -R 0:0 ${D}${libdir}

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
