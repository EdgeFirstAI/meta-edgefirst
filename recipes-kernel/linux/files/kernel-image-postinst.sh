set +e
@BOOT_FUNCTIONS@
b=/${KERNEL_IMAGEDEST}
src=$b/@TYPE@-${KERNEL_VERSION}
if [ -z "$D" ]; then
    if ! edgefirst_mount_boot; then
        echo "ERROR: no partition is labelled boot, so U-Boot would not see this kernel." >&2
        exit 1
    fi
    dst=$EDGEFIRST_BOOT
    if [ -L $dst/@TYPE@ ]; then
        rm -f $dst/@TYPE@
    fi
    if ! cmp -s $src $dst/@TYPE@; then
        if [ -f $dst/@TYPE@ ]; then
            cp -f $dst/@TYPE@ $dst/@TYPE@.prev
        fi
        if ! { cp -f $src $dst/@TYPE@.new && sync && mv -f $dst/@TYPE@.new $dst/@TYPE@; }; then
            rm -f $dst/@TYPE@.new
            edgefirst_umount_boot
            echo "ERROR: could not install $dst/@TYPE@; the previous kernel is unchanged." >&2
            exit 1
        fi
    fi
    edgefirst_umount_boot
fi
set -e
