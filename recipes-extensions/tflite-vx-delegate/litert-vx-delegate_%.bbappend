# EdgeFirst DMA-BUF zero-copy and CameraAdaptor for NPU format conversion
# (see tensorflow-lite-vx-delegate bbappend for full description)

TENSORFLOW_LITE_VX_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-vx-delegate-imx.git;protocol=https"
SRCBRANCH_vx = "edgefirst"
SRCREV_vx = "33f9152ab2a28d1a3ee83509e28ca95b704f5409"

# G2D is needed by the camera_adaptor_test example for hardware-accelerated
# image resize. Provided by imx-gpu-g2d on i.MX8MP via virtual/libg2d.
DEPENDS:append = " virtual/libg2d"

# Upstream litert-vx-delegate omits header installation; add it here so the
# dmabuf and camera_adaptor APIs are available in the SDK/toolchain.
# Use litert-vx-delegate/ to avoid file conflicts with tensorflow-lite-vx-delegate-dev.
do_install:append() {
    install -d ${D}${includedir}/litert-vx-delegate
    cd ${S}
    cp --parents \
        $(find . -name "*.h*") \
        ${D}${includedir}/litert-vx-delegate
}
