# The FAT partition U-Boot reads is wherever the system has mounted it, or is
# mounted here for the length of the script.
edgefirst_mount_boot() {
    EDGEFIRST_BOOT="$(findmnt -rn -o TARGET -S LABEL=boot 2>/dev/null | head -n 1)"
    EDGEFIRST_BOOT_TMP=
    if [ -z "$EDGEFIRST_BOOT" ]; then
        EDGEFIRST_BOOT=/run/edgefirst-boot
        mkdir -p $EDGEFIRST_BOOT
        if mount -t vfat LABEL=boot $EDGEFIRST_BOOT 2>/dev/null; then
            EDGEFIRST_BOOT_TMP=1
        else
            EDGEFIRST_BOOT=
        fi
    fi
    [ -n "$EDGEFIRST_BOOT" ]
}
edgefirst_umount_boot() {
    sync
    [ -z "$EDGEFIRST_BOOT_TMP" ] || umount $EDGEFIRST_BOOT
}
