SUMMARY = "apt feed for EdgeFirst Yocto for NXP"
DESCRIPTION = "Points apt at the signed EdgeFirst package feed for this BSP, \
installs the key the feed is signed with, and pins the feed so that apt \
makes the board match it even where a version string compares lower."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://edgefirst-nxp-apt.gpg file://edgefirst-release-upgrade"

# Follows the release so that moving to a new quarterly release installs the
# configuration of its feed.
PV = "${@d.getVar('EDGEFIRST_VERSION') or '1.0'}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

# Feed layout: <EDGEFIRST_FEED_URI>/<YYYY.MM>/<EDGEFIRST_FEED_BSP>/<package arch>/.
# A feed is a rolling channel: PATCH updates of a quarterly release replace its
# contents. A board follows its release's feed until edgefirst-release-upgrade
# moves it to the next one. EDGEFIRST_FEED_BSP has no default so that a build
# which does not publish a feed cannot ship a source that points at one.
EDGEFIRST_FEED_URI ?= "https://repo.edgefirst.ai/nxp"

# Every architecture directory the machine installs from is listed whether or
# not it holds packages, and the publisher creates an index for each of them.
EDGEFIRST_FEED_ARCHS ?= "${PACKAGE_ARCHS}"

KEYRING = "${datadir}/keyrings/edgefirst-nxp-apt.gpg"

do_install() {
    if [ -z "${EDGEFIRST_FEED_BSP}" ] || [ -z "${EDGEFIRST_RELEASE}" ]; then
        bbfatal "EDGEFIRST_FEED_BSP and EDGEFIRST_VERSION must be set"
    fi

    install -d ${D}${datadir}/keyrings
    install -m 0644 ${S}/edgefirst-nxp-apt.gpg ${D}${KEYRING}

    install -d ${D}${sysconfdir}/apt/sources.list.d
    for arch in ${EDGEFIRST_FEED_ARCHS}; do
        echo "deb [signed-by=${KEYRING}] ${EDGEFIRST_FEED_URI}/${EDGEFIRST_RELEASE}/${EDGEFIRST_FEED_BSP}/$arch ./" \
            >> ${D}${sysconfdir}/apt/sources.list.d/edgefirst.list
    done

    install -d ${D}${sysconfdir}/edgefirst
    cat > ${D}${sysconfdir}/edgefirst/feed.conf <<CONF
EDGEFIRST_FEED_URI=${EDGEFIRST_FEED_URI}
EDGEFIRST_FEED_MACHINE_ARCH=${MACHINE_ARCH}
EDGEFIRST_KEYRING=${KEYRING}
CONF

    install -d ${D}${sbindir}
    install -m 0755 ${S}/edgefirst-release-upgrade ${D}${sbindir}/edgefirst-release-upgrade

    install -d ${D}${sysconfdir}/apt/preferences.d
    cat > ${D}${sysconfdir}/apt/preferences.d/edgefirst <<PREF
Package: *
Pin: origin ${@d.getVar('EDGEFIRST_FEED_URI').split('/')[2]}
Pin-Priority: 1001
PREF
}

FILES:${PN} = "${datadir}/keyrings ${sysconfdir}/apt ${sysconfdir}/edgefirst ${sbindir}/edgefirst-release-upgrade"

RDEPENDS:${PN} = "apt gnupg ca-certificates"
