# EdgeFirst YOLOv8n 640x640 INT8 object detection and segmentation examples
#
# Installs the unified yolov8n binary (detection + segmentation via
# edgefirstoverlay, supporting TFLite VX/Neutron and Ara-2 backends)
# plus reference baseline binaries for benchmarking.

NXP_NNSTREAMER_EXAMPLES_SRC = "git://github.com/EdgeFirstAI/nxp-nnstreamer-examples.git;protocol=https"
SRCBRANCH = "edgefirst-yolov8"
SRCREV = "7098efb5027c25c7fd5e0b1ac7f23f22e44f6f30"

DEPENDS += "edgefirst-hal edgefirst-gstreamer gstreamer1.0-plugins-base"
RDEPENDS:${PN} += "edgefirst-hal edgefirst-gstreamer"

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
IMX_NNSTREANER_DIR = "${GPNT_APPS_FOLDER}/scripts/machine_learning/nnstreamer"

# Override do_install entirely — the base recipe hardcodes ${WORKDIR}/git and
# ${WORKDIR}/build which breaks with devtool (externalsrc) and Walnascar's
# UNPACKDIR.  Use ${S} and ${B} throughout instead.
do_install() {
    # --- NXP base examples (replaces upstream do_install) ---
    install -d ${D}${IMX_NNSTREANER_DIR}

    cp ${S}/LICENSE ${D}${IMX_NNSTREANER_DIR}
    cp ${S}/SCR*.txt ${D}${IMX_NNSTREANER_DIR}

    install -d ${D}${IMX_NNSTREANER_DIR}/classification
    install -m 0755 ${B}/classification/example_classification_mobilenet_v1_tflite ${D}${IMX_NNSTREANER_DIR}/classification

    install -d ${D}${IMX_NNSTREANER_DIR}/classification_detection
    install -m 0755 ${B}/mixed-demos/example_classification_and_detection_tflite ${D}${IMX_NNSTREANER_DIR}/classification_detection

    install -d ${D}${IMX_NNSTREANER_DIR}/dual_classification
    install -m 0755 ${B}/mixed-demos/example_double_classification_tflite ${D}${IMX_NNSTREANER_DIR}/dual_classification

    install -d ${D}${IMX_NNSTREANER_DIR}/emotion_detection
    install -m 0755 ${B}/face-processing/example_emotion_classification_tflite ${D}${IMX_NNSTREANER_DIR}/emotion_detection

    install -d ${D}${IMX_NNSTREANER_DIR}/face_detection
    install -m 0755 ${B}/face-processing/example_face_detection_tflite ${D}${IMX_NNSTREANER_DIR}/face_detection

    install -d ${D}${IMX_NNSTREANER_DIR}/object_detection
    install -m 0755 ${B}/object-detection/example_detection_mobilenet_ssd_v2_tflite ${D}${IMX_NNSTREANER_DIR}/object_detection

    install -d ${D}${IMX_NNSTREANER_DIR}/pose_estimation
    install -m 0755 ${B}/pose-estimation/example_pose_movenet_tflite ${D}${IMX_NNSTREANER_DIR}/pose_estimation

    install -d ${D}${IMX_NNSTREANER_DIR}/pose_face
    install -m 0755 ${B}/mixed-demos/example_face_and_pose_detection_tflite ${D}${IMX_NNSTREANER_DIR}/pose_face

    install -d ${D}${IMX_NNSTREANER_DIR}/semantic_segmentation
    install -m 0755 ${B}/semantic-segmentation/example_segmentation_deeplab_v3_tflite ${D}${IMX_NNSTREANER_DIR}/semantic_segmentation

    # --- EdgeFirst YOLOv8n examples ---
    install -d ${D}${EDGEFIRST_DIR}
    install -m 0755 ${B}/yolov8n/yolov8n ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_reference ${D}${EDGEFIRST_DIR}/
    install -m 0755 ${B}/yolov8n/yolov8n_ara2_reference ${D}${EDGEFIRST_DIR}/
}

FILES:${PN} += "${EDGEFIRST_DIR}"
