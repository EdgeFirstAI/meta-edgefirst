# Neutron NPU: export buffers as DMA-BUF (EDGEAI-1186)
#
# Replace anon_inode_getfd() with dma_buf_export() in the Neutron
# buffer allocator, enabling zero-copy sharing with V4L2/GStreamer/GPU.
#
# Merged upstream by NXP in the wrynose (6.18.20) kernel tree as
# 053be821725d ("AIR-14567 staging: neutron: export buffers as dma-buf")
# with follow-up 464fd6f2e2de ("MA-24787 staging: neutron: Import
# DMA_BUF namespace for dma-buf symbols"), so the patch is only applied
# on pre-wrynose layer series where the driver still uses anon inodes.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"scarthgap", "walnascar", "whinlatter"}:
        d.appendVar("SRC_URI", " file://0001-staging-neutron-export-buffers-as-dma-buf.patch")
}
