SUMMARY = "EdgeFirst Studio command line client"
DESCRIPTION = "Command line client for EdgeFirst Studio: dataset, snapshot and \
training session management from the target."
HOMEPAGE = "https://github.com/EdgeFirstAI/client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

COMPATIBLE_HOST = "aarch64.*-linux"

SRC_URI = "\
    https://github.com/EdgeFirstAI/client/releases/download/v${PV}/edgefirst-client-linux-arm64-${PV}.tar.gz;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/client/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license \
"
SRC_URI[binary.sha256sum] = "5687c11050b6827f34ca22af133abdf2bd3be67c48d48748afc7247a4577f956"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/edgefirst-client-linux-arm64 ${D}${bindir}/edgefirst-client
}

INSANE_SKIP:${PN} += "already-stripped"
