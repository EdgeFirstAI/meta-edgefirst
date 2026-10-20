set +e
@BOOT_FUNCTIONS@
b=/${KERNEL_DTBDEST}
if [ -z "$D" ]; then
    if ! edgefirst_mount_boot; then
        echo "ERROR: no partition is labelled boot, so U-Boot would not see these device trees." >&2
        exit 1
    fi
    (cd $b && find . \( -name '*.dtb' -o -name '*.dtbo' \) -type f) | while read -r f; do
        n=${f#./}
        mkdir -p "$EDGEFIRST_BOOT/$(dirname "$n")"
        if ! cmp -s "$b/$n" "$EDGEFIRST_BOOT/$n"; then
            cp -f "$b/$n" "$EDGEFIRST_BOOT/$n.new" && mv -f "$EDGEFIRST_BOOT/$n.new" "$EDGEFIRST_BOOT/$n" || { rm -f "$EDGEFIRST_BOOT/$n.new"; echo "ERROR: could not install $n" >&2; }
        fi
    done
    edgefirst_umount_boot
fi
set -e
