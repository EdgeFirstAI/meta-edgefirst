SUMMARY = "C bindings for the Zenoh protocol"
DESCRIPTION = "zenoh-c provides C API bindings for the Zenoh zero-overhead pub/sub, store/query and compute protocol."
HOMEPAGE = "https://github.com/eclipse-zenoh/zenoh-c"
LICENSE = "Apache-2.0 | EPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://github.com/eclipse-zenoh/zenoh-c/releases/download/${PV}/zenoh-c-${PV}-${TARGET_ARCH}-unknown-linux-gnu-standalone.zip"

SRC_URI_SHA256SUM[aarch64] = "6f686b9596a385b005ae9c83708b5f1410c8c3946cccee79d866f119d7eca2ee"
SRC_URI_SHA256SUM[x86_64] = "6ef75d92a95ae3c2aaf3c1b854734014f7fce9f566ee2cbf514ce8704e3cac84"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('SRC_URI_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'sha256sum', sha256)

    # Scarthgap has no UNPACKDIR; files unpack directly to WORKDIR
    if d.getVar('UNPACKDIR') is None:
        d.setVar('S', d.getVar('WORKDIR'))
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

PACKAGES = "${PN}-dev ${PN}-staticdev ${PN}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    # Shared library
    install -d ${D}${libdir}
    install -m 0755 ${S}/lib/libzenohc.so ${D}${libdir}

    # Static library
    install -m 0644 ${S}/lib/libzenohc.a ${D}${libdir}

    # CMake config
    install -d ${D}${libdir}/cmake
    cp -r ${S}/lib/cmake/zenohc ${D}${libdir}/cmake

    # pkg-config
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/lib/pkgconfig/zenohc.pc ${D}${libdir}/pkgconfig

    # Headers
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/*.h ${D}${includedir}

    chown -R root:root "${D}"
}

FILES_SOLIBSDEV = ""

FILES:${PN} = "${libdir}/libzenohc.so"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"
FILES:${PN}-staticdev = "${libdir}/libzenohc.a"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP:${PN} += "dev-so ldflags"
