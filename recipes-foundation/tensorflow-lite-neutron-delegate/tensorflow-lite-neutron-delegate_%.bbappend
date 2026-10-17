# EdgeFirst DMA-BUF support for Neutron Delegate (EDGEAI-1188)
#
# Adds hal_dmabuf_* symbol exports and /proc/self/fd scanning for
# DMA-BUF buffer discovery, enabling zero-copy GPU->NPU sharing.
# Requires the neutron kernel dma_buf_export support (EDGEAI-1186):
# the layer's patch on kernels that lack it, in-tree (053be821725d) on wrynose.
#
# dd81103 adds the per-delegate DMA-BUF registry so multiple interpreter
# contexts (worker pools for overlapped inference) can run zero-copy
# concurrently in one process.

NEUTRON_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-neutron-delegate.git;protocol=https"
SRCBRANCH_neutron = "edgefirst"

python () {
    d.setVar("SRCREV_neutron", "dd81103c1305b0ee7e49cde51624cda18f7dd51c")
}

DEPENDS:append = " edgefirst-hal"
