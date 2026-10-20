set +e
b=/${KERNEL_IMAGEDEST}
v=@TYPE@-${KERNEL_VERSION}
if [ -n "$D" ]; then
    ln -sf $v $D$b/@TYPE@ > /dev/null 2>&1
else
    if ! grep -q " $b " /proc/mounts; then
        echo "WARNING: $b is not the boot partition, so U-Boot will not see this kernel." >&2
    fi
    if [ -L $b/@TYPE@ ]; then
        rm -f $b/@TYPE@
    fi
    if ! cmp -s $b/$v $b/@TYPE@; then
        if [ -f $b/@TYPE@ ]; then
            cp -f $b/@TYPE@ $b/@TYPE@.prev
        fi
        if ! { cp -f $b/$v $b/@TYPE@.new && sync && mv -f $b/@TYPE@.new $b/@TYPE@; }; then
            rm -f $b/@TYPE@.new
            echo "ERROR: could not install $b/@TYPE@; the previous kernel is unchanged." >&2
            exit 1
        fi
    fi
    sync
fi
set -e
