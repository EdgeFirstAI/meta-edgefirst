# EdgeFirst NNStreamer fork with DMA-BUF zero-copy, Ara-2 NPU, and HAL delegate support
#
# Adds dmabuf-enabled invoke_v2 API to tensor_filter, Kinara Ara-2
# NPU support, and HAL delegate DMA-BUF probing for Neutron NPU.
#
# Changes over upstream NXP:
# - DMA-BUF zero-copy tensor passing through GStreamer pipeline
# - tensor_filter: Ara-2 sub-plugin (dlopen's libaraclient.so.1 at runtime)
# - tensor_filter V2: flexible tensor input support (header-stripping fallback)
# - TFLite VX delegate CameraAdaptor integration (i.MX 8M Plus)
# - TFLite HAL delegate DMA-BUF probing for Neutron NPU (i.MX 95, EDGEAI-1189)
#
# Replaces NXP patches (already integrated in the EdgeFirst fork):
# - AIR-11938 tensor-filter memcpy ethosu delegate
# - numpy include path fix (YOCIMX-8735)
# - rgb888 support
# - customfilter passthrough path fix
# - gray8 padding removal
# - default delegates fix

SRC_URI = "git://github.com/EdgeFirstAI/nnstreamer.git;branch=edgefirst;protocol=https"
SRCREV = "64635d79f86372c74f91ab343517600836096379"

# EdgeFirst HAL delegate DMA-BUF support (EDGEAI-1189)
# Enables tensor_filter to probe for hal_dmabuf_* symbols from Neutron delegate
DEPENDS:append = " edgefirst-hal"

# Kinara Ara-2 NPU tensor_filter sub-plugin
# Build: requires dvapi.h from ara2-dev
# Runtime: dlopen's libaraclient.so.1, communicates via /var/run/ara2.sock
PACKAGECONFIG[ara2] = "\
    -Dara2-support=enabled, \
    -Dara2-support=disabled, \
    ara2, \
    , \
"

# Enable Ara-2 on platforms with Kinara PCIe NPU support
PACKAGECONFIG_SOC:mx8mp-nxp-bsp:append = " ara2"
PACKAGECONFIG_SOC:mx9-nxp-bsp:append = " ara2"

# Package the ara2 tensor_filter sub-plugin
PACKAGES =+ "${@bb.utils.contains('PACKAGECONFIG', 'ara2', '${PN}-ara2', '', d)}"

FILES:${PN}-ara2 = "\
    ${libdir}/nnstreamer/filters/libnnstreamer_filter_ara2.so \
"

RDEPENDS:${PN}-ara2 = "ara2"
