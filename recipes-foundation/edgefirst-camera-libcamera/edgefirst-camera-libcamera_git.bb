# The EdgeFirst Camera SDK reaches libcamera through this shim: a small C
# library over libcamera's C++ API with a stable C ABI. The SDK loads it with
# dlopen as libedgefirst_camera_libcamera.so.1 from the library search path,
# so nothing links against it and no shlib dependency pulls it into an image;
# whatever ships the SDK has to install it.
#
# It is built here against the image's own libcamera, so it matches that
# libcamera's minor version and C++ ABI. libcamera 0.5 or newer is required.

SUMMARY = "EdgeFirst Camera SDK libcamera shim"
DESCRIPTION = "C ABI over libcamera's C++ API, loaded at run time by the \
EdgeFirst Camera SDK libcamera backend."
HOMEPAGE = "https://github.com/EdgeFirstAI/camera"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${CAMERA_SRC_DIR}/LICENSE;md5=3929fde384c07d35ed0d6f0c925f2a12"

SRC_URI = "git://github.com/EdgeFirstAI/camera.git;protocol=https;branch=${SRCBRANCH};destsuffix=edgefirst-camera"
SRCBRANCH = "feature/EDGEAI-1498-camera-sdk"
SRCREV = "40985f05ab8b6fdf354b1912fb981ac7c8e23394"

# Sorts below the 3.0.0 release, so the released package replaces it.
PV = "3.0.0~dev+git"

CAMERA_SRC_DIR = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}/edgefirst-camera"
S = "${CAMERA_SRC_DIR}/crates/libcamera-sys/shim"

DEPENDS = "libcamera"

inherit meson pkgconfig
