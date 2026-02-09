DESCRIPTION = "EdgeFirst Web UI Frontend"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f37ed3153b54d282ea1077ee569ffc1c"

SRC_URI = "https://github.com/EdgeFirstAI/webui/archive/refs/tags/v${PV}.zip;downloadfilename=${PN}-${PV}.zip"
SRC_URI[sha256sum] = "fe9b225524175bb1b7789a518b6adf580af160cf62dd311a4d6cba7bbd0bde69"

S = "${WORKDIR}/webui-${PV}"

do_install () {
    install -d ${D}${datadir}
    cp -r ${S}/src ${D}${datadir}/edgefirst-webui
}

FILES:${PN} += "${datadir}"
