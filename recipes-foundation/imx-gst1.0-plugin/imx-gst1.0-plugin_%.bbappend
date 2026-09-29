# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.
#
# EDGEAI-1186 follow-up, resolved: an earlier wrynose pre-release SRCREV of
# NXP's fork was mid-migration to a new gstimxsocfeatures.h/.c SoC-capability
# subsystem, missing libs/gstimxcommon.h and leaving 16 HAS_*/IS_* macros
# undefined. NXP's SRCREV now pinned by meta-imx-bsp for rel_imx_6.18.20_2.0.0
# (MM_04.11.00_2605_L6.18.20, e0b7f80a) has that migration complete —
# gstimxcommon.h/gstimxsocfeatures.h both exist and our patch's touched files
# (gstimxcommon.c, gstimxcompositor.c, gstimxvideoconvert.c/.h) don't
# reference the removed macros. Rebased edgefirst-dmabuf onto that baseline
# cleanly (no textual conflicts); build-validated on imx95-pro.
#
# whinlatter's pre-rebase tip is preserved on the edgefirst-imx-6.18.2-1.0.0
# anchor branch.

IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"wrynose"}:
        d.setVar("SRCBRANCH", "edgefirst-dmabuf")
        d.setVar("SRCREV", "59f9a4418489c98c552d682c0909b7d030ddfcef")
    elif series & {"whinlatter"}:
        d.setVar("SRCBRANCH", "edgefirst-imx-6.18.2-1.0.0")
        d.setVar("SRCREV", "58f899e2e54605f921dfff947e067ce101d8b649")
    else:
        d.setVar("SRCBRANCH", "edgefirst-1.2.3")
        d.setVar("SRCREV", "f3e158798839d8162c74d6cde6d5f550d6840a27")
}
