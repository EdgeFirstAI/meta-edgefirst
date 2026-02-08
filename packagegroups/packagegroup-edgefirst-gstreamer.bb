SUMMARY = "EdgeFirst Perception for GStreamer - ML inference pipelines"
DESCRIPTION = "Installs the EdgeFirst GStreamer/NNStreamer inference pipeline \
stack with NPU acceleration for i.MX processors."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit packagegroup

# Uncomment packages as recipes are added to the layer
RDEPENDS:${PN} = " \
#    nnstreamer \
#    gst-edgefirst \
"
