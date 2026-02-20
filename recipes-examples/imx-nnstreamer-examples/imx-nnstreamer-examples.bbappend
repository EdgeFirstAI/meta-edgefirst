# EdgeFirst YOLOv8n 640x640 INT8 object detection examples
#
# Adds five yolov8n binaries demonstrating EdgeFirst HAL optimizations
# (DMA-BUF zero-copy, quantized NMS) across i.MX 8M Plus, i.MX 95,
# and Kinara Ara-2 NPU platforms, plus reference baselines.

NXP_NNSTREAMER_EXAMPLES_SRC = "git://github.com/EdgeFirstAI/nxp-nnstreamer-examples.git;protocol=https"
SRCBRANCH = "edgefirst-gstmetaquant"
SRCREV = "df3c37f903b285d456565c78e92c60bb8811c5df"

DEPENDS += "edgefirst-hal gstreamer1.0-plugins-base"
RDEPENDS:${PN} += "edgefirst-hal"

# edgefirst-hal ships libedgefirst_hal.so without a versioned SONAME so the
# unversioned .so ends up in the -dev package; QA cannot map it automatically
INSANE_SKIP:${PN} += "file-rdeps"

# yolov8n is a standalone CMake project within the repo
do_configure:append() {
    cmake -S ${S}/yolov8n -B ${B}/yolov8n \
        -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain.cmake \
        -DCMAKE_SYSROOT=${PKG_CONFIG_SYSROOT_DIR}
}

do_compile:append() {
    cmake --build ${B}/yolov8n -- ${PARALLEL_MAKE}
}

EDGEFIRST_DIR = "/opt/edgefirst"

do_install:append() {
    install -d ${D}${EDGEFIRST_DIR}
    install -m 0755 ${B}/yolov8n/yolov8n_reference ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_imx8mp ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_imx95 ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_ara2 ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_ara2_reference ${D}${EDGEFIRST_DIR}/
}

FILES:${PN} += "${EDGEFIRST_DIR}"
