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

# The edgefirst branch carries NXP's delegate with the registry above on top,
# rebased onto each NXP release it follows; it keeps the earlier history, so the
# revision for an older NXP release stays reachable. NXP's recipe names the
# branch it tracks, which tells which baseline the build is on: from
# lf-6.18.37 on the delegate builds from the rebased revision (NXP's own
# hal_dmabuf API plus the registry), before that from the revision on
# lf-6.18.2.
python () {
    import re
    revision = "dd81103c1305b0ee7e49cde51624cda18f7dd51c"
    match = re.match(r"lf-(\d+\.\d+\.\d+)_", d.getVar("SRCBRANCH_neutron") or "")
    if match and bb.utils.vercmp_string(match.group(1), "6.18.37") >= 0:
        revision = "0222a8db3aa109871b7eec3d3af151563bf70d94"
    d.setVar("SRCBRANCH_neutron", "edgefirst")
    d.setVar("SRCREV_neutron", revision)
}

DEPENDS:append = " edgefirst-hal"
