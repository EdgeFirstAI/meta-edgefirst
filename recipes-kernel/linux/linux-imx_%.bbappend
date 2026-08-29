# Neutron NPU: export buffers as DMA-BUF (EDGEAI-1186)
#
# Replace anon_inode_getfd() with dma_buf_export() in the Neutron
# buffer allocator, enabling zero-copy sharing with V4L2/GStreamer/GPU.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Temporarily disabled: does not apply cleanly against the wrynose/6.18.20
# kernel tree (drivers/staging/neutron/neutron_buffer.c context changed
# upstream between 6.18.2 and 6.18.20). Needs porting to the new tree
# before re-enabling. Neutron DMA-BUF zero-copy is unavailable until then.
# SRC_URI += "file://0001-staging-neutron-export-buffers-as-dma-buf.patch"
