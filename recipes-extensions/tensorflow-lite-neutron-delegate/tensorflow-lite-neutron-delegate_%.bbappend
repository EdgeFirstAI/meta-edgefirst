# EdgeFirst DMA-BUF support for Neutron Delegate (EDGEAI-1188)
#
# Adds hal_dmabuf_* symbol exports and /proc/self/fd scanning for
# DMA-BUF buffer discovery, enabling zero-copy GPU->NPU sharing.
# Requires the neutron kernel driver dma_buf_export patch (EDGEAI-1186).

NEUTRON_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-neutron-delegate.git;protocol=https"
SRCBRANCH_neutron = "edgefirst-dmabuf"
SRCREV_neutron = "e95f0ce4ffd8d4d2e1119b6c069b959c84d159d2"

# HAL 0.13.1 provides hal_dmabuf_tensor_info struct definition
DEPENDS:append = " edgefirst-hal"
