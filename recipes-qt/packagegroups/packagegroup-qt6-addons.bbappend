# qtdeviceutilities ships libqt6networksettings6 which hard-requires
# connman (RDEPENDS in qtdeviceutilities_git.bb). meta-edgefirst
# excludes connman from imx-image-full to stop the resolv.conf
# update-alternatives fight with systemd-resolved (see CHANGELOG
# v1.2.3). Drop qtdeviceutilities from the Qt6 addons packagegroup so
# do_populate_sdk on imx-image-full does not pull connman back via the
# Qt6 dependency chain. The SDK loses the device-utilities helper
# modules (NetworkSettings, LocaleManager, TimeDateManager) — not
# used by EdgeFirst inference applications.
RDEPENDS:${PN}:remove = "qtdeviceutilities"
