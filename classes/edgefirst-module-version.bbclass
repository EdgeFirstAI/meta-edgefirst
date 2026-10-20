# Out-of-tree kernel modules are rebuilt for every kernel but keep their own
# version, so apt sees no change and a board that takes a new kernel keeps the
# module built for the old one. The release, PATCH and a digest of the kernel's
# ABI version are added to the package revision, so that a module rebuilt for a
# new kernel is a new package version that sorts above the old one.
# Only recipes that inherit module are affected.

python () {
    if bb.data.inherits_class('module', d) and d.getVar('EDGEFIRST_APT_FEED') == '1' and d.getVar('EDGEFIRST_VERSION'):
        funcs = d.getVar('PACKAGEFUNCS') or ''
        d.setVar('PACKAGEFUNCS', funcs.replace('emit_pkgdata', 'edgefirst_module_revision emit_pkgdata'))
}

python edgefirst_module_revision () {
    import hashlib
    path = os.path.join(d.getVar('STAGING_KERNEL_BUILDDIR'), 'kernel-abiversion')
    with open(path) as f:
        abi = f.read().strip()
    d.setVar('PKGR', '%s.%s.%s' % (d.getVar('PKGR'), d.getVar('EDGEFIRST_REVISION'), hashlib.sha256(abi.encode()).hexdigest()[:8]))
}
