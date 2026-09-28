# EdgeFirst NNStreamer fork with DMA-BUF zero-copy, Ara-2 NPU, and HAL delegate support
#
# Adds dmabuf-enabled invoke_v2 API to tensor_filter, Kinara Ara-2
# NPU support, and HAL delegate DMA-BUF probing for Neutron NPU.
#
# Changes over upstream NXP:
# - DMA-BUF zero-copy tensor passing through GStreamer pipeline
# - tensor_filter: Ara-2 sub-plugin (Kinara SDK runtime only, see below)
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

SRC_URI = "git://github.com/EdgeFirstAI/nnstreamer.git;protocol=https;branch=${EDGEFIRST_NNSTREAMER_BRANCH}"
SRCREV = "925d9ba391a2f84b5f5f9e8006548ec2e136a766"

# Same commit is reachable from both branch names; pick the series-frozen
# branch label so do_fetch does not depend on force-pushed rolling tips.
python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if series & {"whinlatter", "wrynose"}:
        d.setVar("EDGEFIRST_NNSTREAMER_BRANCH", "edgefirst")
    else:
        d.setVar("EDGEFIRST_NNSTREAMER_BRANCH", "edgefirst-1.2.3")
}

# EdgeFirst HAL delegate DMA-BUF support (EDGEAI-1189)
# Enables tensor_filter to probe for hal_dmabuf_* symbols from Neutron delegate
DEPENDS:append = " edgefirst-hal"

# Kinara Ara-2 NPU tensor_filter sub-plugin. It dlopens libaraclient.so.1
# and talks to /var/run/ara2.sock, the Kinara SDK runtime, so it is built
# only when imx-nxp-ara2 resolves to that packaging (KINARA_ARA2_RUNTIME,
# set by meta-kinara). meta-imx-ml's wrynose append enables "ara2" on
# mx8mp/mx95 whatever the runtime, so it is removed everywhere else,
# including builds without meta-kinara.
PACKAGECONFIG[ara2] = "-Dara2-support=enabled,-Dara2-support=disabled,imx-nxp-ara2,imx-nxp-ara2"
EDGEFIRST_NNSTREAMER_ARA2 = "${@'ara2' if d.getVar('KINARA_ARA2_RUNTIME') == 'kinara' else ''}"
PACKAGECONFIG:append:mx8mp-nxp-bsp = " ${EDGEFIRST_NNSTREAMER_ARA2}"
PACKAGECONFIG:append:mx95-nxp-bsp = " ${EDGEFIRST_NNSTREAMER_ARA2}"
PACKAGECONFIG:remove = "${@'' if d.getVar('EDGEFIRST_NNSTREAMER_ARA2') else 'ara2'}"

FILES:${PN}-ara2 = "${libdir}/nnstreamer/filters/libnnstreamer_filter_ara2.so"
RDEPENDS:${PN}-ara2 = "imx-nxp-ara2"
RRECOMMENDS:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'ara2', '${PN}-ara2', '', d)}"

# meta-imx-ml declares ${PN}-ara2 in PACKAGES on wrynose; earlier BSPs do
# not. Add it only when missing, since a duplicate entry fails do_package.
python () {
    if bb.utils.contains('PACKAGECONFIG', 'ara2', True, False, d):
        pkg = d.expand('${PN}-ara2')
        if pkg not in (d.getVar('PACKAGES') or '').split():
            d.prependVar('PACKAGES', pkg + ' ')
}
