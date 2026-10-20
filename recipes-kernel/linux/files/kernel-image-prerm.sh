set +e
b=/${KERNEL_IMAGEDEST}
v=@TYPE@-${KERNEL_VERSION}
# dpkg passes "remove in-favour <package>" when a newer kernel replaces this one;
# that package's postinst moves the image aside as the previous one itself.
if [ -z "$D" ] && [ "$1" = "remove" ] && [ "$2" != "in-favour" ]; then
    if [ -f $b/@TYPE@ ] && cmp -s $b/@TYPE@ $b/$v; then
        if [ -f $b/@TYPE@.prev ]; then
            cp -f $b/@TYPE@.prev $b/@TYPE@.new && sync && mv -f $b/@TYPE@.new $b/@TYPE@ && rm -f $b/@TYPE@.prev
        else
            rm -f $b/@TYPE@
        fi
    fi
fi
set -e
