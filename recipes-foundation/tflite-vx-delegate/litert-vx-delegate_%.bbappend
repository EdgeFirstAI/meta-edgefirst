# EdgeFirst DMA-BUF zero-copy and CameraAdaptor for NPU format conversion
# (see tensorflow-lite-vx-delegate bbappend for full description)

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if "wrynose" in series:
        # Keep NXP's stock source on wrynose: the edgefirst fork's cmake
        # targets the litert 2.0 layout and fails do_configure against
        # litert 2.1.0 (Findtensorflow.cmake: no "tensorflow-lite"
        # target). The classic tensorflow-lite-vx-delegate recipe still
        # builds the fork with the DMA-BUF + CameraAdaptor features.
        # Restore the fork here once it is rebased onto NXP's
        # lf-6.18.20_2.0.0 baseline.
        return
    d.setVar("TENSORFLOW_LITE_VX_DELEGATE_SRC",
             "git://github.com/EdgeFirstAI/tflite-vx-delegate-imx.git;protocol=https")
    if "whinlatter" in series:
        d.setVar("SRCBRANCH_vx", "edgefirst")
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
