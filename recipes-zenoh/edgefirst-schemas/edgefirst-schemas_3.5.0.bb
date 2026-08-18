SUMMARY = "EdgeFirst Schemas C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/schemas"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst-schemas-linux_${TARGET_ARCH}-${PV}.zip;name=clib \
    https://raw.githubusercontent.com/EdgeFirstAI/schemas/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

# Python wheel switched from pure-Python `py3-none-any` (pycdr2-based)
# to arch-specific pyo3 extension in 3.2.0. Target wheel is aarch64-only;
# x86_64 SDK builds skip the Python package.
SRC_URI:append:aarch64 = " \
    https://github.com/EdgeFirstAI/schemas/releases/download/v${PV}/edgefirst_schemas-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
"
SRC_URI[python.sha256sum] = "28a3ced8589e7f0875e1587f04b3fa4b2f7f055a958ba329fff3fae7b1156834"

CLIB_SHA256SUM[aarch64] = "bef431c8dd58a9172a8376f87803885f06a975391cd6d767c7fa15b002bf0357"
CLIB_SHA256SUM[x86_64] = "82ef7cc79db7ec05597c67f8de929cea3cb76ce91e881811729eae7f2e8e2ad4"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('CLIB_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'clib.sha256sum', sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit python3-dir

DEPENDS = "python3 unzip-native"
RDEPENDS:${PN}-python = "python3"

do_install() {
    # The upstream tarball ships a correct SONAME symlink chain
    # (libedgefirst_schemas.so → .so.3 → .so.3.4 → .so.3.4.0) plus the
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

    # Install Python wheel (aarch64 only; pyo3 extension introduced in 3.2.0)
    if [ -f ${S}/edgefirst_schemas-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl ]; then
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        unzip ${S}/edgefirst_schemas-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
    fi
}

PACKAGES = "${PN} ${PN}-dev ${PN}-staticdev ${PN}-python"

INSANE_SKIP:${PN} += "ldflags already-stripped"
INSANE_SKIP:${PN}-python += "ldflags"

# The PyO3 wheel's schemas.abi3.so is linked with SONAME
# `libedgefirst_schemas.so.3` (matches the standalone C library) — an
# upstream build-config inheritance from the Rust cdylib metadata.
# Mark the python-bundled library as private so it doesn't compete with
# the real C library as a shlibs provider (which would fail
# `do_package` with "Multiple shlib providers"). Track upstream fix
# request: SONAME on the .abi3.so should match its filename.
PRIVATE_LIBS:${PN}-python = "libedgefirst_schemas.so.3"

FILES:${PN} = "${libdir}/libedgefirst_schemas.so.*"
FILES:${PN}-dev = "${includedir} ${libdir}/libedgefirst_schemas.so ${libdir}/pkgconfig"
FILES:${PN}-staticdev = "${libdir}/libedgefirst_schemas.a"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"

ALLOW_EMPTY:${PN}-python = "1"
