# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.

IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"whinlatter", "wrynose"}:
        d.setVar("SRCBRANCH", "edgefirst-dmabuf")
        d.setVar("SRCREV", "58f899e2e54605f921dfff947e067ce101d8b649")
    else:
        d.setVar("SRCBRANCH", "edgefirst-1.2.3")
        d.setVar("SRCREV", "f3e158798839d8162c74d6cde6d5f550d6840a27")
}
