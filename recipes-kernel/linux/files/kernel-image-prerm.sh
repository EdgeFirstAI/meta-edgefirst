set +e
@BOOT_FUNCTIONS@
b=/${KERNEL_IMAGEDEST}
src=$b/@TYPE@-${KERNEL_VERSION}
# Removing a kernel other than the one in use leaves the image alone.
if [ -z "$D" ] && [ "$1" = "remove" ] && edgefirst_mount_boot; then
    dst=$EDGEFIRST_BOOT
    if [ -f $dst/@TYPE@ ] && cmp -s $dst/@TYPE@ $src; then
        if [ -f $dst/@TYPE@.prev ]; then
            cp -f $dst/@TYPE@.prev $dst/@TYPE@.new && sync && mv -f $dst/@TYPE@.new $dst/@TYPE@ && rm -f $dst/@TYPE@.prev
        else
            rm -f $dst/@TYPE@
        fi
    fi
    edgefirst_umount_boot
fi
set -e
