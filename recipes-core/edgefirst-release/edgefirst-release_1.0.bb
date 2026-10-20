SUMMARY = "EdgeFirst release of an NXP board"
DESCRIPTION = "Names the EdgeFirst release a board runs and ties together the \
packages that move as a set when it is updated: the kernel image, its device \
trees and its modules. The kernel image and module packages carry the kernel \
version in their names, so apt would never replace them by name; installing a \
newer edgefirst-release does."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# YYYY.MM.PATCH, with ~rcN or ~dev ahead of the release it leads up to, so that
# 2026.10.0~rc1 < 2026.10.0 < 2026.10.1 under apt's version ordering.
EDGEFIRST_VERSION ?= ""
PV = "${@d.getVar('EDGEFIRST_VERSION') or '0'}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

python () {
    if not d.getVar("EDGEFIRST_VERSION"):
        raise bb.parse.SkipRecipe("EDGEFIRST_VERSION is not set")
}

do_install() {
    install -d ${D}${sysconfdir}
    echo "EDGEFIRST_RELEASE=${PV}" > ${D}${sysconfdir}/edgefirst-release
}

FILES:${PN} = "${sysconfdir}/edgefirst-release"

# Declared here as well so that the image build, which decides what goes into the
# rootfs from the task graph, knows to build and include it; the kernel packages
# are added at package time below.
RDEPENDS:${PN} = "edgefirst-apt-config"

# The kernel recipe's package data names the packages of the kernel being
# built. A dependency on the bare virtual names would be satisfied by the old
# kernel that is already installed, so the exact package names and versions are
# written out instead.
do_package[depends] += "virtual/kernel:do_packagedata"

# The runtime dependencies are written at package time from the kernel recipe's
# package data, which the build-deps check cannot see.
INSANE_SKIP:${PN} += "build-deps"

python populate_packages:prepend () {
    import oe.packagedata
    def runtime(name):
        path = os.path.join(d.getVar("PKGDATA_DIR"), "runtime", name)
        data = oe.packagedata.read_pkgdatafile(path)
        if "PKGV" not in data:
            bb.fatal("no package data for %s in %s" % (name, path))
        return data

    image = runtime("kernel-image")
    deps = ["edgefirst-apt-config", image["PKG:kernel-image"]]
    # The image is renamed to carry the kernel version, so it is named without
    # a version constraint; the two constant-name packages need the version to
    # be replaced when only their contents change.
    for name in ("kernel-devicetree", "kernel-modules"):
        data = runtime(name)
        deps.append("%s (= %s-%s)" % (data.get("PKG:" + name, name), data["PKGV"], data["PKGR"]))
    d.setVar("RDEPENDS:" + d.getVar("PN"), " ".join(deps))
}
