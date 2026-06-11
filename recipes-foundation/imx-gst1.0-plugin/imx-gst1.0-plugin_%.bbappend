# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.

IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"
SRCBRANCH = "edgefirst-dmabuf"
SRCREV = "58f899e2e54605f921dfff947e067ce101d8b649"
