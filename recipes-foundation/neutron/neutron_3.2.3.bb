# eIQ Neutron SDK 3.2.3 target firmware, driver library and headers.
#
# NXP publishes the newest SDK releases only as a zip behind a login on
# nxp.com and mirrors them to nxp-imx/neutron.git several releases later; its
# newest branch, lf-6.18.37_2.1.0, carries 3.2.1. BitBake cannot fetch the
# zip, so the target binaries are republished on the EdgeFirst fork, tagged
# for the SDK version rather than for the BSP a release happened to ship
# with. Tag v3.2.3 is the SRCREV below, and the branch accumulates later
# releases so only SRCREV moves.
#
# The fork's layout is identical to the one nxp-imx/neutron.git publishes,
# validated byte-for-byte against 3.2.1, which NXP releases both ways.
#
# This recipe is self-contained. Where meta-freescale-ml also provides a
# neutron recipe, this one wins on version.
#
# Driver, firmware and compiler have to come from one SDK version or the
# firmware reports "Microcode version mismatch!". The compiler side is in
# EdgeFirst Studio, which selects a version to match the target.
#
# 3.2.x firmware replies 0xA1 to a mailbox reset where the 6.18.20 kernel
# driver expects 0x0 and would wait forever, so each vendor kernel also
# needs the 6.18.37 mailbox handshake backported.

SUMMARY = "The firmware, library and converter for i.MX Neutron NPU"
DESCRIPTION = "The firmware library and converter for i.MX Neutron NPU"
LICENSE = "Proprietary"
# LA_OPT_NXP_Software_License v64.2 May 2026
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=610e715743b41d3ebde8bd7bf208791d"

SRC_URI = "${NEUTRON_SRC};branch=${SRCBRANCH}"
NEUTRON_SRC = "git://github.com/EdgeFirstAI/neutron.git;protocol=https"
SRCBRANCH = "eiq-neutron-sdk"
SRCREV = "3b9a9d7c3dab89843bbba06844e07a3d5f647db3"

# Where the git fetcher unpacks differs between Yocto series.
S = "${@(d.getVar('UNPACKDIR') + '/' + d.getVar('BB_GIT_DEFAULT_DESTSUFFIX')) if d.getVar('BB_GIT_DEFAULT_DESTSUFFIX') else ((d.getVar('UNPACKDIR') or d.getVar('WORKDIR')) + '/git')}"

NEUTRON_TARGET = ""
NEUTRON_TARGET:mx943-nxp-bsp = "imx943"
NEUTRON_TARGET:mx95-nxp-bsp = "imx95"
NEUTRON_TARGET:mx952-nxp-bsp = "imx952"

do_install () {
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${S}/${NEUTRON_TARGET}/firmware/* ${D}${nonarch_base_libdir}/firmware/

    install -d ${D}${includedir}/neutron
    cp ${S}/${NEUTRON_TARGET}/include/* ${D}${includedir}/neutron

    install -d ${D}${libdir}
    cp --no-preserve=ownership -d ${S}/${NEUTRON_TARGET}/library/* ${D}${libdir}
}

# The packaged binaries have been stripped of debug info.
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

# The library file is unversioned.
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

FILES:${PN} += "${nonarch_base_libdir}/firmware/*"
INSANE_SKIP:${PN} = "arch"

COMPATIBLE_MACHINE = "(mx943-nxp-bsp|mx95-nxp-bsp|mx952-nxp-bsp)"
