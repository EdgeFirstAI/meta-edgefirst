SUMMARY = "EdgeFirst Perception Platform - hardware video codecs"
DESCRIPTION = "Userspace needed to drive the SoC video encoder and decoder \
through the V4L2 memory-to-memory API. No GStreamer: EdgeFirst's own \
components open the V4L2 codec nodes directly, so this is separable from \
packagegroup-edgefirst-gstreamer and useful on its own. \
\
What userspace is required differs by SoC. The i.MX 8M Plus Hantro blocks \
need a vendor library for decode and the VC8000E encoder, plus the daemon \
that arbitrates the single VPU between clients. The i.MX 95 WAVE5 driver \
needs only its firmware blob."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = ""

# Gated on the machine rather than the imxvpu override: imxvpu also covers the
# i.MX 6 CODA VPU, which these recipes are not COMPATIBLE_MACHINE for.
RDEPENDS:${PN}:append:mx8mp-nxp-bsp = " \
    imx-vpu-hantro \
    imx-vpu-hantro-vc \
    imx-vpu-hantro-daemon \
"

# The WAVE5 V4L2 driver is in-kernel and needs no userspace library. Its
# firmware otherwise reaches the image only through MACHINE_FIRMWARE, which
# lands in MACHINE_EXTRA_RRECOMMENDS and can be dropped by BAD_RECOMMENDATIONS
# or NO_RECOMMENDATIONS; naming it here makes the codec capability a hard
# dependency of anything that asks for it.
RDEPENDS:${PN}:append:mx95-nxp-bsp = " \
    firmware-imx-vpu-wave \
"
