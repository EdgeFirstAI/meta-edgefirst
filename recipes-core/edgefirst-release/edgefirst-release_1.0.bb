SUMMARY = "EdgeFirst Yocto ${EDGEFIRST_VERSION} for ${EDGEFIRST_VENDOR} BSP ${EDGEFIRST_BSP_VERSION}"
DESCRIPTION = "EdgeFirst Yocto ${EDGEFIRST_VERSION} for the ${EDGEFIRST_VENDOR} \
BSP ${EDGEFIRST_BSP_VERSION}. Ties together the packages that move as a set \
when a board is updated: the operating system identification, the kernel image, \
its device trees and its modules. The kernel image and module packages carry \
the kernel version in their names, so apt would never replace them by name; \
installing a newer edgefirst-release does."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# EDGEFIRST_VERSION is YYYY.MM.PATCH, with ~rcN or ~dev ahead of the release it
# leads up to, so that 2026.10.0~rc1 < 2026.10.0 < 2026.10.1 under apt's version
# ordering.
PV = "${@d.getVar('EDGEFIRST_VERSION') or '0'}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

python () {
    if d.getVar("EDGEFIRST_APT_FEED") != "1":
        raise bb.parse.SkipRecipe("EDGEFIRST_APT_FEED is not 1")
    if not d.getVar("EDGEFIRST_VERSION"):
        raise bb.parse.SkipRecipe("EDGEFIRST_VERSION is not set")
}

ALLOW_EMPTY:${PN} = "1"

# The vendor BSP the release is built on, which a package can depend on. A
# version in a Provides is dropped by the deb writer, so the BSP version is part
# of the name.
RPROVIDES:${PN} = "edgefirst-bsp-${EDGEFIRST_FEED_BSP}"

# Declared here as well so that the image build, which decides what goes into the
# rootfs from the task graph, knows to build and include it; the kernel packages
# are added at package time below.
RDEPENDS:${PN} = "edgefirst-apt-config util-linux-findmnt util-linux-mount util-linux-umount"

# The kernel recipe's package data names the packages of the kernel being
# built. A dependency on the bare virtual names would be satisfied by the old
# kernel that is already installed, so the exact package names and versions are
# written out instead.
do_package[depends] += "virtual/kernel:do_packagedata os-release:do_packagedata"

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
    release = runtime("os-release")
    # The kernel scripts find and mount the boot partition.
    deps = ["edgefirst-apt-config", "util-linux-findmnt", "util-linux-mount", "util-linux-umount", image["PKG:kernel-image"],
            "os-release (= %s-%s)" % (release["PKGV"], release["PKGR"])]
    # The image is renamed to carry the kernel version, so it is named without
    # a version constraint; the two constant-name packages need the version to
    # be replaced when only their contents change.
    for name in ("kernel-devicetree", "kernel-modules"):
        data = runtime(name)
        deps.append("%s (= %s-%s)" % (data.get("PKG:" + name, name), data["PKGV"], data["PKGR"]))
    d.setVar("RDEPENDS:" + d.getVar("PN"), " ".join(deps))
}
