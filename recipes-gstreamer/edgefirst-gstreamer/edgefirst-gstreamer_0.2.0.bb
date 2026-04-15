SUMMARY = "EdgeFirst Perception for GStreamer"
DESCRIPTION = "GStreamer library and plug-ins for EdgeFirst Perception \
including Zenoh bridge elements, sensor fusion processing, and HAL \
camera adaptor integration."
HOMEPAGE = "https://github.com/EdgeFirstAI/gstreamer"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3257033d1fff3bf8edd31697b409f14d"

SRC_URI = "git://github.com/EdgeFirstAI/gstreamer.git;branch=main;protocol=https"
SRCREV = "f8d2e2b69f3044c6a12a486381971c68726a4ad8"

S = "${WORKDIR}/git"

inherit meson pkgconfig

DEPENDS = " \
    glib-2.0 \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    json-glib \
    zenoh-c \
    edgefirst-schemas \
    edgefirst-hal \
    nnstreamer \
"

EXTRA_OEMESON = " \
    -Dtests=enabled \
    -Ddocs=disabled \
"

FILES:${PN} += " \
    ${libdir}/gstreamer-1.0 \
    ${libexecdir}/${BPN} \
"

INSANE_SKIP:${PN} += "ldflags"
