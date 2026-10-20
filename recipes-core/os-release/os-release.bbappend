# Name the EdgeFirst release in /etc/os-release, next to the vendor's own
# version, which is left as it is. The package release follows the EdgeFirst
# version so that a rolling update replaces the file.

python () {
    version = d.getVar("EDGEFIRST_VERSION")
    if d.getVar("EDGEFIRST_APT_FEED") != "1" or not version:
        return
    d.appendVar("OS_RELEASE_FIELDS", " EDGEFIRST_RELEASE EDGEFIRST_VERSION EDGEFIRST_BSP")
    d.setVar("EDGEFIRST_BSP", d.getVar("EDGEFIRST_FEED_BSP"))
    d.setVar("PR", "r0." + version)
    d.setVar("PRETTY_NAME", "EdgeFirst Yocto %s for %s BSP %s" % (
        version, d.getVar("EDGEFIRST_VENDOR"), d.getVar("EDGEFIRST_BSP_VERSION")))
}
