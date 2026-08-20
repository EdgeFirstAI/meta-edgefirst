SUMMARY = "EdgeFirst Perception Platform - Zenoh services"
DESCRIPTION = "Zenoh infrastructure, schemas, and sensor services for the \
EdgeFirst Perception Platform. A standalone recipe (not a packagegroup-edgefirst \
subpackage) so wanting this doesn't force bitbake to also build the \
-gstreamer or -python flavors' dependencies."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    packagegroup-edgefirst \
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
