# Walnascar pins pseudo 1.9.0, which has no openat2() wrapper. Host GNU tar
# 1.35 (Ubuntu 24.04) opens the extraction directory with openat2() and works
# relative to that fd, so pseudo never records the dirfd and do_package fails
# with "got *at() syscall for unknown directory, fd N". Walnascar is end of
# life, so move it to the pseudo-1.9 tip, 1.9.8, which wraps openat2().
# 0001-configure-Prune-PIE-flags.patch is upstream in 1.9.8, and
# older-glibc-symbols.patch is refreshed for its $(LIBPSEUDO) link rule.

EDGEFIRST_PSEUDO_198_FILES := "${THISDIR}/pseudo-1.9.8"

python () {
    series = set((d.getVar("LAYERSERIES_CORENAMES") or "").split())
    if "walnascar" not in series:
        return
    d.setVar("SRCREV", "823895ba708c63f6ae4dcbfc266210f26c02c698")
    d.setVar("PV", "1.9.8+git")
    d.setVar("SRC_URI", " ".join(u for u in d.getVar("SRC_URI").split()
                                 if u != "file://0001-configure-Prune-PIE-flags.patch"))
    d.prependVar("FILESEXTRAPATHS", d.getVar("EDGEFIRST_PSEUDO_198_FILES") + ":")
}
