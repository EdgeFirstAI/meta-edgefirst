SUMMARY = "EdgeFirst Perception Platform - Studio agents"
DESCRIPTION = "EdgeFirst Studio command line client and the on-target \
profiling agent. Both ship as prebuilt aarch64 release tarballs, so they add \
image size but no build time. The profiler runs models through TFLite and \
whichever NPU delegate the machine installs, so it is most useful alongside \
packagegroup-edgefirst-npu."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = " \
    edgefirst-client \
    edgefirst-profiler \
"
