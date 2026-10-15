SUMMARY = "EdgeFirst Perception Platform - camera"
DESCRIPTION = "libcamera and the machine's ISP image processing algorithm. \
Pure V4L2/libcamera: the libcamera-gst bridge element is not here, it comes \
with packagegroup-edgefirst-gstreamer, so a camera works without GStreamer \
installed. Board-specific sensor and dewarp configuration is not here either \
- it belongs in the BSP vendor layer, because the camera modules a board \
accepts are a vendor fact."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    libcamera \
"

# i.MX 95 ISP: the NEO IPA module libcamera loads for the on-SoC ISP.
RDEPENDS:${PN}:append:mx95-nxp-bsp = " \
    neo-ipa-uguzzi \
"
