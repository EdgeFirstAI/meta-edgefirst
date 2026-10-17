# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.
#
# Based on NXP's MM_04.11.00_2605_L6.18.20 baseline (rel_imx_6.18.20_2.0.0).
# Every Yocto series builds the rolling edgefirst-dmabuf branch.

IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"

python () {
    d.setVar("SRCBRANCH", "edgefirst-dmabuf")
    d.setVar("SRCREV", "59f9a4418489c98c552d682c0909b7d030ddfcef")
}
