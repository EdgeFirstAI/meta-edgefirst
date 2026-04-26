SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://files.pythonhosted.org/packages/03/4d/2386f0fbdaa3a8979a2e8d9e109a93cc4d94f134b31c0b6551fcb440ebef/edgefirst_schemas-${PV}-py3-none-any.whl;name=python \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"
SRC_URI[python.sha256sum] = "2ab1eb1a6308843d8bd383ac3c0458bee052612f0189976d36b91474a86609af"

CLIB_SHA256SUM[aarch64] = "0436b07c861400b074b3b164f5e9017d9647491cf9d629eab7097169e9069393"
CLIB_SHA256SUM[x86_64] = "ecbbbd7f2a340fa4898a51a5df8d51131cc7cf53ae38ac0a3c063d011d741167"

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
    # (libedgefirst_schemas.so → .so.3 → .so.3.1 → .so.3.1.0) plus the
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
