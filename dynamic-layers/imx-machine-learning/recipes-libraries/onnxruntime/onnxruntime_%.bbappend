# NXP's ONNX Runtime Neutron provider calls the matmul() form of the Neutron
# driver library that eIQ Neutron SDK 3.2.x replaced, and does not compile
# against the SDK this layer provides. The release that ships the typed header
# is the one wrynose images carry; older releases keep the original call.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    neutron_socs = {"mx943-nxp-bsp", "mx95-nxp-bsp", "mx952-nxp-bsp"}
    if "wrynose" in series and neutron_socs & set(d.getVar("OVERRIDES").split(":")):
        d.appendVar("SRC_URI", " file://0001-neutron-call-matmul-with-the-typed-header.patch")
}
