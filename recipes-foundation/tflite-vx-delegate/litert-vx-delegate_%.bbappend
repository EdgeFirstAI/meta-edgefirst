# EdgeFirst DMA-BUF zero-copy and CameraAdaptor for NPU format conversion
# (see tensorflow-lite-vx-delegate bbappend for full description)

# Every Yocto series builds the rolling edgefirst branch; there are no
# per-series anchors.
TENSORFLOW_LITE_VX_DELEGATE_SRC = "git://github.com/EdgeFirstAI/tflite-vx-delegate-imx.git;protocol=https"
python () {
    d.setVar("SRCBRANCH_vx", "edgefirst")
    d.setVar("SRCREV_vx", "a65d4a23a43194b6c2d329f364c97183f507580d")
}

# The delegate's CMake fetches LiteRT itself and defaults to the wrynose tag.
# Where the recipe builds against the walnascar LiteRT (6.12) that fetches
# sources the delegate was not built with, so it is pointed at the tag the
# recipe unpacks.
python () {
    if "walnascar" in (d.getVar("LAYERSERIES_CORENAMES") or "").split():
        d.appendVar("EXTRA_OECMAKE", " -DLITERT_GIT_TAG=${SRCBRANCH_litert} -DTFLITE_GIT_TAG=${SRCBRANCH_litert}")
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
