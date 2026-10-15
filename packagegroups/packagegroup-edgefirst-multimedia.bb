SUMMARY = "EdgeFirst Perception Platform - GStreamer and VPU"
DESCRIPTION = "NXP's GStreamer plugin set and VPU codecs, from \
meta-freescale-distro. On i.MX 8M Plus, MACHINE_GSTREAMER_1_0_PLUGIN resolves \
to imx-gst1.0-plugin, so this pulls the hardware elements (VPU decode, \
imxvideoconvert_g2d); meta-edgefirst bbappends that recipe to add DMA-BUF \
zero-copy, which is what lets NPU pipelines avoid a CPU copy. \
\
Empty on i.MX 95, whose GStreamer payload currently comes from the BSP \
vendor's own packagegroup rather than from meta-freescale-distro."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = ""

# i.MX 8M Plus: full NXP plugin set, plus Hantro decode, VC8000E encode and
# the daemon that arbitrates them.
RDEPENDS:${PN}:append:mx8mp-nxp-bsp = " \
    packagegroup-fsl-gstreamer1.0 \
    packagegroup-fsl-gstreamer1.0-video-bad \
    packagegroup-fsl-gstreamer1.0-network \
    imx-vpu-hantro \
    imx-vpu-hantro-vc \
    imx-vpu-hantro-daemon \
"
