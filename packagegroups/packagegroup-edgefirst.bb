SUMMARY = "EdgeFirst Perception Platform"
DESCRIPTION = "Packagegroup for the EdgeFirst Perception Platform. The base \
package installs foundation libraries (HAL, videostream). Sub-packages add \
Zenoh infrastructure and sensor services (-zenoh), GStreamer ML pipelines \
(-gstreamer), and Python bindings (-python)."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGES = "${PN} ${PN}-zenoh ${PN}-gstreamer ${PN}-python"

# Foundation — core AI vision pipeline libraries
RDEPENDS:${PN} = " \
    edgefirst-hal \
    videostream \
    videostream-cli \
"

# Zenoh — infrastructure, schemas, and sensor services
RDEPENDS:${PN}-zenoh = " \
    ${PN} \
    zenoh-c \
    zenohd \
    edgefirst-schemas \
    edgefirst-camera \
    edgefirst-model \
    edgefirst-fusion \
    edgefirst-imu \
    edgefirst-navsat \
    edgefirst-radarpub \
    edgefirst-lidarpub \
    edgefirst-recorder \
    edgefirst-replay \
    edgefirst-websrv \
    edgefirst-webui \
"

# GStreamer — EdgeFirst GStreamer/NNStreamer ML inference pipelines
RDEPENDS:${PN}-gstreamer = " \
    ${PN} \
    edgefirst-gstreamer \
"

# Python — bindings for foundation and Zenoh libraries
RDEPENDS:${PN}-python = " \
    python3-zenoh \
    edgefirst-tflite \
    edgefirst-schemas-python \
    edgefirst-hal-python \
    videostream-python \
"
