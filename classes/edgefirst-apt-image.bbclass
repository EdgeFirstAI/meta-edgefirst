# Mark the kernel packages of an image as automatically installed, so that
# apt autoremove retires a kernel and its modules once edgefirst-release has
# moved on to a newer one. apt records the mark in extended_states; the rootfs
# is built without apt, so nothing writes it.

ROOTFS_POSTPROCESS_COMMAND += "edgefirst_mark_kernel_auto;"

edgefirst_mark_kernel_auto () {
    status=${IMAGE_ROOTFS}${localstatedir}/lib/dpkg/status
    if [ -z "${EDGEFIRST_VERSION}" ] || [ "${IMAGE_PKGTYPE}" != "deb" ] || ! grep -q '^Package: edgefirst-release$' $status 2>/dev/null; then
        return 0
    fi
    install -d ${IMAGE_ROOTFS}${localstatedir}/lib/apt
    awk 'BEGIN { RS = ""; FS = "\n" }
        {
            pkg = ""; arch = ""
            for (i = 1; i <= NF; i++) {
                if ($i ~ /^Package: kernel-/) pkg = substr($i, 10)
                if ($i ~ /^Architecture: /) arch = substr($i, 15)
            }
            if (pkg != "") printf "Package: %s\nArchitecture: %s\nAuto-Installed: 1\n\n", pkg, arch
        }' $status >> ${IMAGE_ROOTFS}${localstatedir}/lib/apt/extended_states
}
