# In the dual-driver configuration Mesa and the Mali DDK install the same
# sonames, and a later ldconfig run points them at Mesa whichever driver is
# selected. Keep the loader cache on the selected driver at every boot.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://edgefirst-mali-ldconf \
    file://edgefirst-ldconf.conf"

do_install:append() {
    if [ "${IMX_MALI_DUAL_DRIVER}" = "1" ]; then
        install -d ${D}${libexecdir}
        install -m 0755 ${UNPACKDIR}/edgefirst-mali-ldconf ${D}${libexecdir}/
        install -d ${D}${systemd_system_unitdir}/mali-imx.service.d
        install -m 0644 ${UNPACKDIR}/edgefirst-ldconf.conf \
            ${D}${systemd_system_unitdir}/mali-imx.service.d/
    fi
}

FILES:${PN}-dual += " \
    ${libexecdir}/edgefirst-mali-ldconf \
    ${systemd_system_unitdir}/mali-imx.service.d"
