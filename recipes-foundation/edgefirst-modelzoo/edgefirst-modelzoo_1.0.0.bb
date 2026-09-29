SUMMARY = "EdgeFirst Model Zoo models"
DESCRIPTION = "Pre-packaged models from the EdgeFirst Hugging Face Model Zoo \
(YOLOv8n detection and instance segmentation). INT8 smart TFLite models select \
the matching artifact per platform: generic TFLite for i.MX 8M Plus, \
Neutron-compiled .imx95.tflite for i.MX 95. INT16 .dvm models for the Ara-2 \
PCIe NPU are the same on both. Subpackages allow images to install det, seg, \
or both, per accelerator."

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
# are never registered (avoids BitBake warnings on the other SoC). SoC
# overrides are matched by prefix: newer NXP BSPs publish only suffixed
# forms such as mx8mp-nxp-bsp and mx95-generic-bsp. Downloads keep their
# upstream names so both SoCs can share one DL_DIR.
python () {
    overrides = (d.getVar("MACHINEOVERRIDES") or "").split(":")
    def soc(name):
        return any(o == name or o.startswith(name + "-") for o in overrides)
    det_rev = d.getVar("EDGEFIRST_YOLOV8_DET_REV")
    seg_rev = d.getVar("EDGEFIRST_YOLOV8_SEG_REV")

    if soc("mx95"):
        d.setVar("MODELZOO_TFLITE_EXT", ".imx95.tflite")
        d.appendVar("SRC_URI", " "
            "https://huggingface.co/EdgeFirst/yolov8-det/resolve/%s/imx95/yolov8n-det-int8-smart.imx95.tflite"
            ";name=yolov8n-det "
            "https://huggingface.co/EdgeFirst/yolov8-seg/resolve/%s/imx95/yolov8n-seg-int8-smart.imx95.tflite"
            ";name=yolov8n-seg" % (det_rev, seg_rev))
        d.setVarFlag("SRC_URI", "yolov8n-det.sha256sum",
                     "5fde7c12d19dbba42c3ed6b9a2bc3fa007ab9d673ff93fb89adb6378d5b28d7e")
        d.setVarFlag("SRC_URI", "yolov8n-seg.sha256sum",
                     "3f49578a60f58e97b826ecfbab86449aea57956673cb5ecd1ff89652917b3f0e")
    elif soc("mx8mp"):
        d.setVar("MODELZOO_TFLITE_EXT", ".tflite")
        d.appendVar("SRC_URI", " "
            "https://huggingface.co/EdgeFirst/yolov8-det/resolve/%s/tflite/yolov8n-det-int8-smart.tflite"
            ";name=yolov8n-det "
            "https://huggingface.co/EdgeFirst/yolov8-seg/resolve/%s/tflite/yolov8n-seg-int8-smart.tflite"
            ";name=yolov8n-seg" % (det_rev, seg_rev))
        d.setVarFlag("SRC_URI", "yolov8n-det.sha256sum",
                     "3baa07a3f7f776bfda360bb284226441f4567d5db29cbbad8bd76a46aba5c5bc")
        d.setVarFlag("SRC_URI", "yolov8n-seg.sha256sum",
                     "4ac280c68bcd8fbc4019b713802a77fd81c6a0b2293f2007f6f64292c26db69c")
}

# Ara-2 models run on the PCIe NPU, not the SoC, so every platform gets the same files.
SRC_URI = " \
    https://huggingface.co/EdgeFirst/yolov8-det/resolve/${EDGEFIRST_YOLOV8_DET_REV}/ara240/yolov8n-det-int16.dvm;name=yolov8n-det-ara2 \
    https://huggingface.co/EdgeFirst/yolov8-seg/resolve/${EDGEFIRST_YOLOV8_SEG_REV}/ara240/yolov8n-seg-int16.dvm;name=yolov8n-seg-ara2 \
"
SRC_URI[yolov8n-det-ara2.sha256sum] = "eb4c507c76146687c28d465bb17afca41545c5eed14ec07453dc3d5ce4215c12"
SRC_URI[yolov8n-seg-ara2.sha256sum] = "74eeeae26bfe3cf65324dfebc473dc272674241340ace0878b95d79280657dc3"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/edgefirst/modelzoo
    install -m 0644 ${S}/yolov8n-det-int8-smart${MODELZOO_TFLITE_EXT} \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-det-int8-smart.tflite
    install -m 0644 ${S}/yolov8n-seg-int8-smart${MODELZOO_TFLITE_EXT} \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-seg-int8-smart.tflite
    install -m 0644 ${S}/yolov8n-det-int16.dvm \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-det-int16.dvm
    install -m 0644 ${S}/yolov8n-seg-int16.dvm \
        ${D}${datadir}/edgefirst/modelzoo/yolov8n-seg-int16.dvm
}

PACKAGES =+ "${PN}-yolov8n-det ${PN}-yolov8n-seg ${PN}-yolov8n-det-ara2 ${PN}-yolov8n-seg-ara2"

FILES:${PN}-yolov8n-det = "${datadir}/edgefirst/modelzoo/yolov8n-det-int8-smart.tflite"
FILES:${PN}-yolov8n-seg = "${datadir}/edgefirst/modelzoo/yolov8n-seg-int8-smart.tflite"
FILES:${PN}-yolov8n-det-ara2 = "${datadir}/edgefirst/modelzoo/yolov8n-det-int16.dvm"
FILES:${PN}-yolov8n-seg-ara2 = "${datadir}/edgefirst/modelzoo/yolov8n-seg-int16.dvm"

# Meta package pulls every model; images may also RDEPEND on a subpackage.
ALLOW_EMPTY:${PN} = "1"
FILES:${PN} = ""
RDEPENDS:${PN} = " \
    ${PN}-yolov8n-det \
    ${PN}-yolov8n-seg \
    ${PN}-yolov8n-det-ara2 \
    ${PN}-yolov8n-seg-ara2 \
"

SUMMARY:${PN}-yolov8n-det = "YOLOv8n detection INT8 smart TFLite (EdgeFirst Model Zoo)"
SUMMARY:${PN}-yolov8n-seg = "YOLOv8n segmentation INT8 smart TFLite (EdgeFirst Model Zoo)"
SUMMARY:${PN}-yolov8n-det-ara2 = "YOLOv8n detection INT16 Ara-2 DVM (EdgeFirst Model Zoo)"
SUMMARY:${PN}-yolov8n-seg-ara2 = "YOLOv8n segmentation INT16 Ara-2 DVM (EdgeFirst Model Zoo)"
