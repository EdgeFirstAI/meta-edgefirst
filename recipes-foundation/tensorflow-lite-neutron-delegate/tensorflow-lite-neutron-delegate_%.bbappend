# EdgeFirst DMA-BUF support for Neutron Delegate (EDGEAI-1188)
#
# Adds hal_dmabuf_* symbol exports and /proc/self/fd scanning for
# DMA-BUF buffer discovery, enabling zero-copy GPU->NPU sharing.
# Requires the neutron kernel driver dma_buf_export patch (EDGEAI-1186).

NEUTRON_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-neutron-delegate.git;protocol=https"
SRCBRANCH_neutron = "edgefirst"
SRCREV_neutron = "4a9f0af0debaa642c129cab93831ecfd0a4a7430"

DEPENDS:append = " edgefirst-hal"
