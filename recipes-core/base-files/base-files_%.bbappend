# Mount the FAT partition U-Boot boots from on /boot, so that the kernel and
# device trees installed by apt land where U-Boot reads them. Without this
# /boot is a directory on the root filesystem that U-Boot never looks at.
# Off by default: it is only right for images laid out with a boot partition
# labelled "boot".
EDGEFIRST_BOOT_MOUNT ??= "0"

do_install:append() {
    if [ "${EDGEFIRST_BOOT_MOUNT}" = "1" ]; then
        echo "LABEL=boot /boot vfat defaults,nofail 0 2" >> ${D}${sysconfdir}/fstab
    fi
}
