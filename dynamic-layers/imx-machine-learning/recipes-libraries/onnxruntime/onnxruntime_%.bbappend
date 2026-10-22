# NXP's ONNX Runtime Neutron provider calls the matmul() form of the Neutron
# driver library that eIQ Neutron SDK 3.2.x replaced, and does not compile
# against the SDK this layer provides. The release that ships the typed header
# is the one wrynose images carry; older releases keep the original call.
#
# The call sites are rewritten after do_patch, not patched, because NXP's
# provider sources move between releases and a patch only applies to the one it
# was cut against. The old form takes the header as a void pointer followed by
# its size, and the new one takes NeutronMatmulHeader * alone, the same sixteen
# 32-bit words the provider builds in m_header.
edgefirst_neutron_typed_matmul() {
    dir=${S}/onnxruntime/core/providers/neutron
    sed -i -E 's/matmul\(\(const void ?\*\) ?m_header, *16 ?\* ?sizeof\(uint32_t\), */matmul((const NeutronMatmulHeader *)m_header, /' $(grep -rlE 'matmul\(' $dir)
    if grep -rE 'matmul\(\(const void ?\*\)' $dir; then
        bbfatal "matmul() call sites remain in the old form"
    fi
}

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    neutron_socs = {"mx943-nxp-bsp", "mx95-nxp-bsp", "mx952-nxp-bsp"}
    if "wrynose" in series and neutron_socs & set(d.getVar("OVERRIDES").split(":")):
        d.appendVarFlag("do_patch", "postfuncs", " edgefirst_neutron_typed_matmul")
}
