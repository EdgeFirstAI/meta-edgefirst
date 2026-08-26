SUMMARY = "EdgeFirst Model Zoo TFLite models"
DESCRIPTION = "Pre-packaged INT8 smart TFLite models from the EdgeFirst Hugging Face \
Model Zoo (YOLOv8n detection and instance segmentation). Platforms select the \
matching artifact: generic TFLite for i.MX 8M Plus, Neutron-compiled \
.imx95.tflite for i.MX 95. Subpackages allow images to install det, seg, or both."

HOMEPAGE = "https://huggingface.co/EdgeFirst"
LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/AGPL-3.0-only;md5=73f1eb20517c55bf9493b7dd6e480788"

# Platform-specific blobs — keep feeds from colliding across SoCs.
PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "(mx8mp|mx95)"

# Pinned Hugging Face revisions (not floating main).
EDGEFIRST_YOLOV8_DET_REV = "9e30a2b170b0964c266a326b6cb63b31e176ca42"
EDGEFIRST_YOLOV8_SEG_REV = "214a396ca20675a4c51f456cda673f59a0628a82"

# Select platform artifacts in anonymous python so unused SRC_URI checksums
# are never registered (avoids BitBake warnings on the other SoC).
python () {
    overrides = set((d.getVar("MACHINEOVERRIDES") or "").split(":"))
    det_rev = d.getVar("EDGEFIRST_YOLOV8_DET_REV")
    seg_rev = d.getVar("EDGEFIRST_YOLOV8_SEG_REV")

    if "mx95" in overrides:
        d.appendVar("SRC_URI", " "
            "https://huggingface.co/EdgeFirst/yolov8-det/resolve/%s/imx95/yolov8n-det-int8-smart.imx95.tflite"
            ";downloadfilename=yolov8n-det-int8-smart.tflite;name=yolov8n-det "
            "https://huggingface.co/EdgeFirst/yolov8-seg/resolve/%s/imx95/yolov8n-seg-int8-smart.imx95.tflite"
            ";downloadfilename=yolov8n-seg-int8-smart.tflite;name=yolov8n-seg" % (det_rev, seg_rev))
        d.setVarFlag("SRC_URI", "yolov8n-det.sha256sum",
                     "5fde7c12d19dbba42c3ed6b9a2bc3fa007ab9d673ff93fb89adb6378d5b28d7e")
        d.setVarFlag("SRC_URI", "yolov8n-seg.sha256sum",
                     "3f49578a60f58e97b826ecfbab86449aea57956673cb5ecd1ff89652917b3f0e")
    elif "mx8mp" in overrides:
        d.appendVar("SRC_URI", " "
            "https://huggingface.co/EdgeFirst/yolov8-det/resolve/%s/tflite/yolov8n-det-int8-smart.tflite"
            ";downloadfilename=yolov8n-det-int8-smart.tflite;name=yolov8n-det "
            "https://huggingface.co/EdgeFirst/yolov8-seg/resolve/%s/tflite/yolov8n-seg-int8-smart.tflite"
            ";downloadfilename=yolov8n-seg-int8-smart.tflite;name=yolov8n-seg" % (det_rev, seg_rev))
        d.setVarFlag("SRC_URI", "yolov8n-det.sha256sum",
                     "3baa07a3f7f776bfda360bb284226441f4567d5db29cbbad8bd76a46aba5c5bc")
        d.setVarFlag("SRC_URI", "yolov8n-seg.sha256sum",
                     "4ac280c68bcd8fbc4019b713802a77fd81c6a0b2293f2007f6f64292c26db69c")
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/edgefirst/modelzoo
    install -m 0644 ${S}/yolov8n-det-int8-smart.tflite \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-det-int8-smart.tflite
    install -m 0644 ${S}/yolov8n-seg-int8-smart.tflite \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-seg-int8-smart.tflite
}

PACKAGES =+ "${PN}-yolov8n-det ${PN}-yolov8n-seg"

FILES:${PN}-yolov8n-det = "${datadir}/edgefirst/modelzoo/yolov8n-det-int8-smart.tflite"
FILES:${PN}-yolov8n-seg = "${datadir}/edgefirst/modelzoo/yolov8n-seg-int8-smart.tflite"

# Meta package pulls both model cards; images may also RDEPEND on a subpackage.
ALLOW_EMPTY:${PN} = "1"
FILES:${PN} = ""
RDEPENDS:${PN} = "${PN}-yolov8n-det ${PN}-yolov8n-seg"

SUMMARY:${PN}-yolov8n-det = "YOLOv8n detection INT8 smart TFLite (EdgeFirst Model Zoo)"
SUMMARY:${PN}-yolov8n-seg = "YOLOv8n segmentation INT8 smart TFLite (EdgeFirst Model Zoo)"
