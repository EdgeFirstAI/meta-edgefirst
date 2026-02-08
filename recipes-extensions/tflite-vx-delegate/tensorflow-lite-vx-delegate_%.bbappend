# EdgeFirst DMA-BUF zero-copy and CameraAdaptor for NPU format conversion
#
# DMA-BUF extensions (edgefirst-dmabuf):
# - vx_delegate_dmabuf.h: Public C API for DMA-BUF and CameraAdaptor
# - dmabuf_manager.h: Internal DmaBufManager (included by delegate_main.h)
#
# CameraAdaptor (edgefirst-cameraadaptor):
# - Runtime preprocessing that injects color space conversion ops into the
#   TIM-VX graph to convert camera formats (RGBA/BGRA/etc.) to model-expected
#   formats (RGB/BGR) entirely on the NPU.
# - camera_adaptor/config.h: CameraAdaptorConfig struct, DType enum
# - camera_adaptor/color_space.h: ColorSpace enum and utility functions
# - Graph injection: TRANSIENT tensors + Slice/Reverse ops with DMA-BUF support
#
# Supported conversions: RGBA/BGRA/RGBX/BGRX/ARGB/ABGR/XRGB/XBGR -> RGB/BGR

TENSORFLOW_LITE_VX_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-vx-delegate-imx.git;protocol=https"
SRCBRANCH_vx = "edgefirst-cameraadaptor"
SRCREV_vx = "a638a0fcbdb15b3914cf117e58321e9c5de9326b"

# G2D is needed by the camera_adaptor_test example for hardware-accelerated
# image resize. Provided by imx-gpu-g2d on i.MX8MP via virtual/libg2d.
DEPENDS:append = " virtual/libg2d"
