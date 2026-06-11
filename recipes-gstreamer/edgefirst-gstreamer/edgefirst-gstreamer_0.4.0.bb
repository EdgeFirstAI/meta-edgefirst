SUMMARY = "EdgeFirst Perception for GStreamer"
DESCRIPTION = "GStreamer library and plug-ins for EdgeFirst Perception \
including Zenoh bridge elements, sensor fusion processing, and HAL \
camera adaptor integration."
HOMEPAGE = "https://github.com/EdgeFirstAI/gstreamer"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3257033d1fff3bf8edd31697b409f14d"

SRC_URI = "git://github.com/EdgeFirstAI/gstreamer.git;branch=main;protocol=https"
SRCREV = "c2c9e1f61370048052b530801f746ea5af1871e2"

# Dual-compat source dir for git checkouts. Whinlatter unpacks to
# ${UNPACKDIR}/${BB_GIT_DEFAULT_DESTSUFFIX} (= ${BP}) and QA-fatals on a
# raw S = ".../git" assignment; walnascar unpacks to ${UNPACKDIR}/git;
# scarthgap to ${WORKDIR}/git. The inline expression yields the actual
# checkout path on every supported release without tripping the QA check.
S = "${@(d.getVar('UNPACKDIR') + '/' + d.getVar('BB_GIT_DEFAULT_DESTSUFFIX')) if d.getVar('BB_GIT_DEFAULT_DESTSUFFIX') else ((d.getVar('UNPACKDIR') or d.getVar('WORKDIR')) + '/git')}"

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
