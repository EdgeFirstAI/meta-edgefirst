# EdgeFirst DMA-BUF tensor API for zero-copy NPU inference
#
# Adds virtual methods to query and preserve DMA-BUF file descriptors
# through TIM-VX tensor operations, enabling zero-copy inference pipelines.
#
# Changes over upstream:
# - tensor.h: Add HasDmaBuf() and GetDmaBufFd() to Tensor interface
# - tensor_private.h: Implement dmabuf methods in TensorImpl/TensorPlaceholder
# - layout_inference.cc: Preserve dmabuf fd when creating inferred I/O tensors
# - vsi_nn_tensor.c: Skip alignment check for DMABUF (fd is not a pointer)

# Override SRC_URI directly to avoid conflict with meta-imx-ml bbappend
# which also sets SRCBRANCH/SRCREV
SRC_URI = "git://github.com/EdgeFirstAI/tim-vx-imx.git;protocol=https;branch=edgefirst-dmabuf"
SRCREV = "736c50d8c4b60a02bc6a227fee49328c1e44446a"

EXTRA_OECMAKE:append = " -DVX_CREATE_TENSOR_SUPPORT_PHYSICAL=on"
