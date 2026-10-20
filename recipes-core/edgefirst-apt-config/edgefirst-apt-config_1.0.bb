SUMMARY = "apt feed for EdgeFirst Yocto for NXP"
DESCRIPTION = "Points apt at the signed EdgeFirst package feed for this BSP, \
installs the key the feed is signed with, and pins the feed so that apt \
makes the board match it even where a version string compares lower."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://edgefirst-nxp-apt.gpg"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

# Feed layout: <EDGEFIRST_FEED_URI>/<EDGEFIRST_FEED_BSP>/<package arch>/. One
# feed per BSP; moving to another BSP is a reflash. EDGEFIRST_FEED_BSP has no
# default so that a build which does not publish a feed cannot ship a source
# that points at one.
EDGEFIRST_FEED_URI ?= "https://repo.edgefirst.ai/nxp"
EDGEFIRST_FEED_BSP ?= ""

# Every architecture directory the machine installs from is listed whether or
# not it holds packages, and the publisher creates an index for each of them.
EDGEFIRST_FEED_ARCHS ?= "${PACKAGE_ARCHS}"

KEYRING = "${datadir}/keyrings/edgefirst-nxp-apt.gpg"

do_install() {
    if [ -z "${EDGEFIRST_FEED_BSP}" ]; then
        bbfatal "EDGEFIRST_FEED_BSP is not set"
    fi

    install -d ${D}${datadir}/keyrings
    install -m 0644 ${S}/edgefirst-nxp-apt.gpg ${D}${KEYRING}

    install -d ${D}${sysconfdir}/apt/sources.list.d
    for arch in ${EDGEFIRST_FEED_ARCHS}; do
        echo "deb [signed-by=${KEYRING}] ${EDGEFIRST_FEED_URI}/${EDGEFIRST_FEED_BSP}/$arch ./" \
            >> ${D}${sysconfdir}/apt/sources.list.d/edgefirst.list
    done

    install -d ${D}${sysconfdir}/apt/preferences.d
    cat > ${D}${sysconfdir}/apt/preferences.d/edgefirst <<PREF
Package: *
Pin: origin ${@d.getVar('EDGEFIRST_FEED_URI').split('/')[2]}
Pin-Priority: 1001
PREF
}

FILES:${PN} = "${datadir}/keyrings ${sysconfdir}/apt"

RDEPENDS:${PN} = "apt gnupg ca-certificates"
