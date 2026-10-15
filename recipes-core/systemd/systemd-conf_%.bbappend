FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://edgefirst-dhcp-client-id.conf"

# systemd-networkd derives its DHCPv4 client identifier from the machine ID,
# and images ship /etc/machine-id empty so that the first boot generates one.
# On a RAUC A/B system every slot activation is a first boot, so the client
# identifier - and with it the DHCP lease - changes on every update even though
# the MAC never does. Pinning the identifier to the MAC keeps the lease stable.
#
# This runs as a postfunc rather than a do_install:append because the .network
# files it iterates over are installed by other layers' do_install:append
# fragments, and the order of those fragments relative to ours is not defined.
# Postfuncs run after do_install has completed in full.
edgefirst_pin_dhcp_client_id() {
    for unit in ${D}${systemd_unitdir}/network/*.network; do
        [ -e "$unit" ] || continue
        grep -qE '^[[:space:]]*DHCP=' "$unit" || continue
        install -d "$unit.d"
        install -m 0644 ${UNPACKDIR}/edgefirst-dhcp-client-id.conf "$unit.d/"
    done
}
do_install[postfuncs] += "edgefirst_pin_dhcp_client_id"
