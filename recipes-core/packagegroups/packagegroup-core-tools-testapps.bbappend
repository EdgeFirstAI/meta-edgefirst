# EdgeFirst: drop ConnMan from the core-tools-testapps packagegroup.
#
# This BSP runs systemd-networkd (+ systemd-resolved) as the
# authoritative network stack. ConnMan installs an
# `update-alternatives` provider for `/etc/resolv.conf` that points at
# `/etc/resolv-conf.connman` whenever `systemd` is in DISTRO_FEATURES
# (see poky/meta/recipes-connectivity/connman/connman.inc), which
# fights systemd-resolved for DNS control and produces stale or empty
# resolv.conf on target.
#
# The connman-* sub-packages are only here because Poky's stock
# packagegroup-core-tools-testapps lists them in RDEPENDS as a
# convenience for kernel test infrastructure. Strip them so the image
# doesn't pull in `connman` itself via shlib auto-rdeps. This pairs
# with PACKAGE_EXCLUDE in meta-edgefirst/conf/layer.conf which fails
# the build loudly if any other path tries to bring ConnMan back.
RDEPENDS:${PN}:remove = "connman-tools connman-tests connman-client"
