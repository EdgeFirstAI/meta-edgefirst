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
# validated byte-for-byte against 3.2.1, which NXP releases both ways. This
# recipe therefore only redirects the fetch; packaging comes from the
# meta-freescale-ml recipe unchanged.
#
# Driver, firmware and compiler have to come from one SDK version or the
# firmware reports "Microcode version mismatch!". The compiler side is in
# EdgeFirst Studio, which selects a version to match the target.
#
# 3.2.x firmware replies 0xA1 to a mailbox reset where the 6.18.20 kernel
# driver expects 0x0 and would wait forever, so each vendor kernel also
# needs the 6.18.37 mailbox handshake backported.

require recipes-libraries/neutron/neutron_1.0.0.bb

NEUTRON_SRC = "git://github.com/EdgeFirstAI/neutron.git;protocol=https"
SRCBRANCH = "eiq-neutron-sdk"
SRCREV = "3b9a9d7c3dab89843bbba06844e07a3d5f647db3"

# 3.2.3 ships LA_OPT_NXP_Software_License v64.2 May 2026, replacing the
# v63 May 2025 text the 1.0.0 recipe pins.
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=610e715743b41d3ebde8bd7bf208791d"

NEUTRON_TARGET:mx952-nxp-bsp = "imx952"

COMPATIBLE_MACHINE = "(mx943-nxp-bsp|mx95-nxp-bsp|mx952-nxp-bsp)"
