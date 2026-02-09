DESCRIPTION = "EdgeFirst Web UI Frontend"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f37ed3153b54d282ea1077ee569ffc1c"

SRC_URI = "git://github.com/EdgeFirstAI/webui.git;protocol=https;branch=main"
SRCREV = "65252e744a478ecb1ce6d74866d284ccdfbf2ab4"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${datadir}
    cp -r ${S}/src ${D}${datadir}/edgefirst-webui
}

FILES:${PN} += "${datadir}"
