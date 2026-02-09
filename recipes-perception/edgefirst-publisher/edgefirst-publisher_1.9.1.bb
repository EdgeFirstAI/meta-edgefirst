DESCRIPTION = "EdgeFirst MCAP Publisher"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PN}-LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "\
    https://github.com/EdgeFirstAI/publisher/releases/download/v${PV}/edgefirst-publisher-linux-${TARGET_ARCH};downloadfilename=edgefirst-publisher;name=binary \
    https://raw.githubusercontent.com/EdgeFirstAI/publisher/v${PV}/LICENSE;downloadfilename=${PN}-LICENSE;name=license \
"
SRC_URI[license.sha256sum] = "acbbda305958ff27afe43eeef4a77d48ef9d99364e772ba319d1d38ae759ae43"

BINARY_SHA256SUM[aarch64] = ""
BINARY_SHA256SUM[x86_64] = ""

python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('BINARY_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'binary.sha256sum', sha256)
}

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install:append () {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/edgefirst-publisher ${D}${bindir}/edgefirst-publisher
}

INSANE_SKIP:${PN} += "already-stripped"

FILES:${PN} += "${bindir}"
