# EdgeFirst DMA-BUF zero-copy and CameraAdaptor for NPU format conversion
# (see tensorflow-lite-vx-delegate bbappend for full description)

# wrynose: edgefirst fork rebased onto NXP's lf-6.18.20_2.0.0 baseline
# (litert-imx, BUILD_FOR_LITERT=ON path) — DMA-BUF + CameraAdaptor restored.
# whinlatter: frozen edgefirst-imx-6.18.2-1.0.0 anchor, still targeting the
# litert 2.0 layout — edgefirst itself moved on to wrynose.
python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    d.setVar("TENSORFLOW_LITE_VX_DELEGATE_SRC",
             "git://github.com/EdgeFirstAI/tflite-vx-delegate-imx.git;protocol=https")
    if "wrynose" in series:
        d.setVar("SRCBRANCH_vx", "edgefirst")
        d.setVar("SRCREV_vx", "c3e44b8fcffaa9b5ba19c07bd895c616a16012d6")
    elif "whinlatter" in series:
        d.setVar("SRCBRANCH_vx", "edgefirst-imx-6.18.2-1.0.0")
        d.setVar("SRCREV_vx", "c8e52d736c2028b82816b25e81c2779db21018a3")
    else:
        d.setVar("SRCBRANCH_vx", "edgefirst-1.2.3")
        d.setVar("SRCREV_vx", "80b3409ee15edd0e68e7f64bd7f8e32d059cf2f0")
}

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
