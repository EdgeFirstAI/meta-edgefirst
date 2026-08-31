# EdgeFirst DMA-BUF extensions for NXP i.MX GStreamer plugin
#
# Adds DMA-BUF zero-copy buffer sharing to imxvideoconvert_g2d and other
# i.MX GStreamer elements, enabling zero-copy inference pipelines with
# NNStreamer and the NPU.
#
# Temporarily disabled on wrynose (EDGEAI-1186 follow-up): the fork's
# edgefirst-dmabuf branch (and edgefirst-1.2.3, same issue) is missing
# libs/gstimxcommon.h — only gstimxcommon.c survived — which several
# files still #include, breaking do_compile the first time this fork
# was build-tested for i.MX95 (armv8a-mx95). Restoring the header
# exposed a much bigger gap: 16 SoC-capability macros (HAS_DCSS,
# HAS_DPU, HAS_G2D, HAS_G3D, HAS_IPU, HAS_PXP, HAS_VPU, IS_AMPHION,
# IS_HANTRO, IS_IMX6Q, IS_IMX8MM, IS_IMX8MP, IS_IMX8Q, IS_IMX8ULP,
# IS_IMX95, IS_IMX952) are used throughout the fork but defined
# nowhere. NXP's real upstream (meta-imx-bsp's own SRCREV, not our
# fork) now provides these via a proper gstimxsocfeatures.h/.c +
# imx_soc_features.ini subsystem (added in their commit a31ad60
# "MMFMWK-9614 imxsocfeatures: add new apis for soc features map",
# with imx_2d_device.h/etc. switched over in 5a6ec33 "remove
# dependency of gstimxcommon.h"). Porting our fork onto that is real
# work — stubbing the macros per-build would be wrong since this same
# header is shared across every machine this fork builds for
# (i.MX8MP, i.MX8MP EVK, i.MX95 variants), not just i.MX95.
#
# Also found and worth carrying into the port: imx_2d_device.c uses
# FILE/fopen/fseek/ftell/fread/fclose/SEEK_END/SEEK_SET without
# including <stdio.h>, and imxoverlaycompositionmeta.c includes
# <gst/allocators/gstphymemmeta.h> but the header this fork actually
# ships lands at <gst/video/gstphymemmeta.h> instead.
#
# Falling back to NXP's stock source for now (loses DMA-BUF zero-copy
# on i.MX95 until the fork is ported) so the image build succeeds.
#IMXGST_SRC = "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"wrynose"}:
        return
    d.setVar("IMXGST_SRC", "git://github.com/EdgeFirstAI/imx-gst1.0-plugin.git;protocol=https")
    if series & {"whinlatter"}:
        d.setVar("SRCBRANCH", "edgefirst-dmabuf")
        d.setVar("SRCREV", "58f899e2e54605f921dfff947e067ce101d8b649")
    else:
        d.setVar("SRCBRANCH", "edgefirst-1.2.3")
        d.setVar("SRCREV", "f3e158798839d8162c74d6cde6d5f550d6840a27")
}
