SUMMARY = "EdgeFirst Perception Platform - GPU and compositor"
DESCRIPTION = "The machine's GPU userspace plus the Wayland compositor. \
Gated on the imxviv and imxmali GPU overrides rather than on SoC names, so \
the same selection applies on any vendor's board carrying that GPU. The GPU \
libraries are built with BACKEND=wayland, so weston is what gives EGL a real \
surface, and weston-examples carries weston-simple-egl for a direct GLES \
smoke test."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    weston \
    weston-init \
    weston-examples \
"

# Mali (i.MX 95): GLES 1.1/2.0/3.x, EGL, GBM and the OpenCL ICD.
RDEPENDS:${PN}:append:imxmali = " \
    mali-imx \
    mali-imx-libegl \
    mali-imx-libgles1 \
    mali-imx-libgles2 \
    mali-imx-libgbm \
    mali-imx-opencl-icd \
"

# Vivante (i.MX 8M Plus): GLES 1.1/2.0/3.x, EGL, GBM, OpenCL and OpenVX on the
# GC7000UL. libgbm-imx matters on a headless board - it is what allows
# surfaceless/DRM rendering with no compositor running. No Vulkan on mx8mp,
# where PACKAGES_VULKAN is empty, so it is deliberately absent.
RDEPENDS:${PN}:append:imxviv = " \
    libgles1-imx \
    libgles2-imx \
    libegl-imx \
    libgal-imx \
    libvsc-imx \
    libgbm-imx \
    libclc-imx \
    libopencl-imx \
    libopenvx-imx \
    libglslc-imx \
    libopenvg-imx \
    libnn-imx \
    libspirv-imx \
    imx-gpu-viv-tools \
    imx-gpu-viv-demos \
    imx-gpu-g2d \
    imx-g2d-samples \
"
