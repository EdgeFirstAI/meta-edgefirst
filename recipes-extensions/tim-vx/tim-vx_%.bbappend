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

TIM_VX_SRC = "git://github.com/EdgeFirstAI/tim-vx-imx.git;protocol=https"
SRCBRANCH = "edgefirst-dmabuf"
SRCREV = "dd3e9fd21d60e8180387ae8d1fbf5f2d82789f6c"

EXTRA_OECMAKE:append = " -DVX_CREATE_TENSOR_SUPPORT_PHYSICAL=on"
