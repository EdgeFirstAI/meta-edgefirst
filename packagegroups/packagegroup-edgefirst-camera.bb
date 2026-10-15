SUMMARY = "EdgeFirst Perception Platform - camera"
DESCRIPTION = "libcamera with its GStreamer element, plus the machine's ISP \
image processing algorithm. Board-specific sensor and dewarp configuration is \
not here - it belongs in the BSP vendor layer, because the camera modules a \
board accepts are a vendor fact."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    libcamera \
    libcamera-gst \
"

# i.MX 95 ISP: the NEO IPA module libcamera loads for the on-SoC ISP.
RDEPENDS:${PN}:append:mx95-nxp-bsp = " \
    neo-ipa-uguzzi \
"
