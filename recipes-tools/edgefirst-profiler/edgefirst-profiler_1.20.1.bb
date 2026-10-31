SUMMARY = "EdgeFirst Profiler CLI"
DESCRIPTION = "On-target profiling agent for AI vision pipelines. Models run on \
the NPU through TFLite and the VX or Neutron delegates installed on the target. \
The release also ships Hailo and TensorRT shim libraries; they need runtimes \
this layer does not provide and are not installed."
HOMEPAGE = "https://github.com/EdgeFirstAI/profiler-cli"
LICENSE = "EdgeFirst-Profiler-EULA"
NO_GENERIC_LICENSE[EdgeFirst-Profiler-EULA] = "LICENSE"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1ec8544499745706bf1a929873e67d"

COMPATIBLE_HOST = "aarch64.*-linux"

SRC_URI = "https://github.com/EdgeFirstAI/profiler-cli/releases/download/v${PV}/edgefirst-profiler-${PV}-linux-aarch64.tar.gz"
SRC_URI[sha256sum] = "53f599dc46e67ca5c5a7909e16c71fd7f6f86012bec02c83ede6d32a67454a63"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}/edgefirst-profiler-${PV}-linux-aarch64"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${S}/edgefirst-profiler ${D}${bindir}/edgefirst-profiler

    install -d ${D}${datadir}/licenses/${BPN}
    install -m 0644 ${S}/LICENSE ${S}/NOTICE ${D}${datadir}/licenses/${BPN}
}

FILES:${PN} += "${datadir}/licenses/${BPN}"

INSANE_SKIP:${PN} += "already-stripped"
