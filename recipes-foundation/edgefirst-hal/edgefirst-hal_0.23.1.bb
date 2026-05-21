SUMMARY = "EdgeFirst HAL C Library and Python Bindings"
HOMEPAGE = "https://github.com/EdgeFirstAI/hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/hal/releases/download/v${PV}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux.tar.gz;name=clib \
    https://raw.githubusercontent.com/EdgeFirstAI/hal/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

# Python wheel available for aarch64 (target) from GitHub releases
SRC_URI:append:aarch64 = " \
    https://github.com/EdgeFirstAI/hal/releases/download/v${PV}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl;name=python \
"
SRC_URI[python.sha256sum] = "2bf670944487cefe9cdb11ce5ad105ec7c3a515f964e914171f37b361df73e3d"

CLIB_SHA256SUM[aarch64] = "db94175ba6adffb5c869be2a03e97ef96bc4dda65f07d62e72276c4f03851764"
CLIB_SHA256SUM[x86_64] = "ed1cb4b9f74acd90d1154fb00490312f2e38d32a5c837ade0ca2ef235591535a"

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
    # (libedgefirst_hal.so → .so.0 → .so.0.23 → .so.0.23.1) plus the
    # static library and pkg-config file. Copy the lib/ tree verbatim
    # with `cp -a` to preserve the symlinks, then reset ownership to
    # root:root since `cp -a` also preserves the host-build uid/gid
    # which the Yocto package-QA rejects.
    install -d ${D}${libdir}
    cp -a ${S}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/lib/. ${D}${libdir}/
    chown -R 0:0 ${D}${libdir}

    # Install headers
    install -d ${D}${includedir}/edgefirst
    install -m 0644 ${S}/edgefirst-hal-capi-${PV}-${TARGET_ARCH}-linux/include/edgefirst/hal.h ${D}${includedir}/edgefirst/

    # Install Python wheel (aarch64 from GitHub releases)
    if [ -f ${S}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl ]; then
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        unzip ${S}/edgefirst_hal-${PV}-cp311-abi3-manylinux_2_17_aarch64.manylinux2014_aarch64.whl -d ${D}${PYTHON_SITEPACKAGES_DIR}
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
