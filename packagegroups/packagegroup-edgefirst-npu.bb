SUMMARY = "EdgeFirst Perception Platform - NPU runtime"
DESCRIPTION = "TensorFlow Lite plus the NPU delegate for the machine's \
accelerator: the eIQ Neutron firmware, driver and delegate on i.MX 95, the \
Vivante VX delegate on i.MX 8M Plus. Deliberately not packagegroup-imx-ml, \
which adds pytorch, tvm and onnxruntime for a very long build and no extra \
NPU reach."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    tensorflow-lite \
"

# i.MX 95: eIQ Neutron. The neutron recipe carries the SDK firmware and
# driver library; the delegate is built from the EdgeFirst fork so it can
# import dma-buf buffers for zero-copy GPU->NPU.
RDEPENDS:${PN}:append:mx95-nxp-bsp = " \
    neutron \
    tensorflow-lite-neutron-delegate \
"

# i.MX 8M Plus: Vivante VX. galcore is built into the kernel, so there is no
# firmware package to install alongside it.
RDEPENDS:${PN}:append:mx8mp-nxp-bsp = " \
    tensorflow-lite-vx-delegate \
"
