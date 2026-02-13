SUMMARY = "EdgeFirst Perception - Zenoh-based sensor services"
DESCRIPTION = "Installs the full EdgeFirst Perception stack including all \
Zenoh-based sensor services, the schemas library, and the Zenoh router daemon."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    edgefirst-perception \
    zenohd \
    zenoh-c \
    edgefirst-schemas \
    edgefirst-schemas-python \
    edgefirst-hal \
    edgefirst-hal-python \
    videostream \
    videostream-cli \
    edgefirst-camera \
    edgefirst-imu \
    edgefirst-navsat \
    edgefirst-radarpub \
    edgefirst-lidarpub \
    edgefirst-websrv \
    edgefirst-webui \
"
# TODO: Add once GitHub releases are published:
# edgefirst-publisher, edgefirst-recorder, edgefirst-replay
