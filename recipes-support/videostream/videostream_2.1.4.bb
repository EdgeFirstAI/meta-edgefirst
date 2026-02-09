DESCRIPTION = "VideoStream provides an SDK and GStreamer plugin for zero-copy video frame sharing across processes."
HOMEPAGE = "https://github.com/EdgeFirstAI/videostream"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://share/doc/VideoStream/LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "https://github.com/EdgeFirstAI/videostream/releases/download/v${PV}/videostream-${PV}-linux-${TARGET_ARCH}.zip"

SRC_URI_SHA256SUM[aarch64] = "e216931a5641f4273a64c1b5b68802f39a463e14778da98a03af22ab1a33c26b"
SRC_URI_SHA256SUM[x86_64] = "0ed95a627dca8e0fda4fb36dafc9f9cd6ad5939f6734fa697e1e835f4530b984"

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('SRC_URI_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

# Debian multiarch triplet used in the ZIP layout
VIDEOSTREAM_LIBDIR = "lib/${TARGET_ARCH}-linux-gnu"

inherit python3-dir

DEPENDS = "python3 gstreamer1.0 gstreamer1.0-plugins-base"
RDEPENDS:${PN}-python = "python3"

PACKAGES = "${PN}-dev ${PN}-python ${PN}-gstreamer ${PN}-camhost ${PN}-samples ${PN}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    # Shared libraries
    install -d ${D}${libdir}
    cp -P ${S}/${VIDEOSTREAM_LIBDIR}/libvideostream.so* ${D}${libdir}

    # GStreamer plugin
    install -d ${D}${libdir}/gstreamer-1.0
    install -m 0755 ${S}/${VIDEOSTREAM_LIBDIR}/gstreamer-1.0/libgstvideostream.so ${D}${libdir}/gstreamer-1.0

    # CMake config
    install -d ${D}${libdir}/cmake
    cp -r ${S}/${VIDEOSTREAM_LIBDIR}/cmake/VideoStream ${D}${libdir}/cmake

    # pkg-config
    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${S}/${VIDEOSTREAM_LIBDIR}/pkgconfig/videostream.pc ${D}${libdir}/pkgconfig

    # Headers
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/videostream.h ${D}${includedir}

    # Python bindings
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}/videostream
    install -m 0644 ${S}/${VIDEOSTREAM_LIBDIR}/python3/dist-packages/videostream/*.py ${D}${PYTHON_SITEPACKAGES_DIR}/videostream

    # Camera host binary
    install -d ${D}${bindir}
    install -m 0755 ${S}/bin/vsl-camhost ${D}${bindir}

    # Samples
    install -d ${D}${datadir}/videostream/samples
    install -m 0644 ${S}/share/videostream/samples/* ${D}${datadir}/videostream/samples

    chown -R root:root "${D}"
}

FILES_SOLIBSDEV = ""

FILES:${PN} = "${libdir}/libvideostream.so*"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"
FILES:${PN}-gstreamer = "${libdir}/gstreamer-1.0"
FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN}-camhost = "${bindir}/vsl-camhost"
FILES:${PN}-samples = "${datadir}/videostream"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"

INSANE_SKIP:${PN} += "dev-so ldflags"
INSANE_SKIP:${PN}-gstreamer += "ldflags"
INSANE_SKIP:${PN}-camhost += "ldflags"
