SUMMARY = "EdgeFirst Perception - Zenoh-based sensor services"
DESCRIPTION = "Installs the full EdgeFirst Perception stack including all \
Zenoh-based sensor services, the schemas library, and the Zenoh router daemon."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit packagegroup

RDEPENDS:${PN} = " \
    edgefirst-perception \
    zenohd \
    edgefirst-schemas \
    edgefirst-camera \
    edgefirst-imu \
    edgefirst-navsat \
    edgefirst-radarpub \
    edgefirst-lidarpub \
    edgefirst-publisher \
    edgefirst-recorder \
    edgefirst-replay \
    edgefirst-websrv \
    edgefirst-webui \
"
