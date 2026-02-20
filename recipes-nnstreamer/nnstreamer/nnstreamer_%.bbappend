# EdgeFirst NNStreamer fork with DMA-BUF zero-copy and Ara-2 NPU support
#
# Adds dmabuf-enabled invoke_v2 API to tensor_filter and Kinara Ara-2
# NPU support as a tensor_filter sub-plugin.
#
# Changes over upstream NXP:
# - DMA-BUF zero-copy tensor passing through GStreamer pipeline
# - tensor_filter: Ara-2 sub-plugin (dlopen's libaraclient.so.1 at runtime)
# - TFLite-VX CameraAdaptor integration for NPU color space conversion
#
# Replaces NXP patches (already integrated in the EdgeFirst fork):
# - AIR-11938 tensor-filter memcpy ethosu delegate
# - numpy include path fix (YOCIMX-8735)
# - rgb888 support
# - customfilter passthrough path fix
# - gray8 padding removal
# - default delegates fix

SRC_URI = "git://github.com/EdgeFirstAI/nnstreamer.git;branch=edgefirst-ara2;protocol=https"
SRCREV = "38cf83985e3d9a3b769f58e6671469b955fe30cf"

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
