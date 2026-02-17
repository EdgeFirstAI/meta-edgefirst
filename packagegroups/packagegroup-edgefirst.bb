SUMMARY = "EdgeFirst Perception Platform"
DESCRIPTION = "Packagegroup for the EdgeFirst Perception Platform. The base \
package installs shared infrastructure (zenoh-c, schemas, HAL, videostream). \
Sub-packages add Zenoh sensor services and router (-zenoh), GStreamer ML \
pipelines (-gstreamer), and Python bindings (-python)."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGES = "${PN} ${PN}-zenoh ${PN}-gstreamer ${PN}-python"

# Shared infrastructure
RDEPENDS:${PN} = " \
    zenoh-c \
    edgefirst-schemas \
    edgefirst-hal \
    videostream \
    videostream-cli \
"

# Zenoh-based sensor services
RDEPENDS:${PN}-zenoh = " \
    ${PN} \
    zenohd \
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

# GStreamer/NNStreamer ML inference pipelines
RDEPENDS:${PN}-gstreamer = " \
    ${PN} \
    edgefirst-gstreamer \
"

# Python bindings
RDEPENDS:${PN}-python = " \
    python3-zenoh \
    edgefirst-schemas-python \
    edgefirst-hal-python \
"
