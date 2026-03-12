DESCRIPTION = "EdgeFirst Web UI Frontend"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${BPN}-LICENSE;md5=f37ed3153b54d282ea1077ee569ffc1c"

SRC_URI = "\
    https://github.com/EdgeFirstAI/webui/releases/download/v${PV}/edgefirst-webui-${PV}.zip;name=archive;subdir=edgefirst-webui \
    https://raw.githubusercontent.com/EdgeFirstAI/webui/v${PV}/LICENSE;downloadfilename=${BPN}-LICENSE;name=license;subdir=edgefirst-webui \
"
SRC_URI[archive.sha256sum] = "dd2259d7493e12f6fb368019475fee7f9993abdfcecfa6efdec6b9a31fa7495b"
SRC_URI[license.sha256sum] = "a2e3ba06380d0e627965e004c96c0af1447fb37bbf0d9a4ddf3382f2187531fa"

S = "${@(d.getVar('UNPACKDIR') or d.getVar('WORKDIR')) + '/edgefirst-webui'}"

do_install () {
    install -d ${D}${datadir}/edgefirst/webui
    cp -r ${S}/* ${D}${datadir}/edgefirst/webui/
    # Remove the license file from the install directory
    rm -f ${D}${datadir}/edgefirst/webui/${BPN}-LICENSE
}

FILES:${PN} += "${datadir}"
