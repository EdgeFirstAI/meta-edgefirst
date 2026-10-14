# Install the EdgeFirst root CA as the RAUC verification keyring in place of
# PHYTEC's development CA.
#
# meta-ampliphy ships a copy of PHYTEC's published development root at
# recipes-core/rauc/files/mainca-rsa.crt.pem and finds it through
# FILESEXTRAPATHS, so repointing a search path is not enough on its own:
# RAUC_KEYRING_FILE has to name a different file.
#
# The keyring is switched on the "update" override, which the ampliphy RAUC
# distros add to DISTROOVERRIDES. Assigning it unconditionally would put the
# EdgeFirst filename into rauc-conf's SRC_URI for non-RAUC build directories
# too, where EDGEFIRST_CERT_PATH is unset and the fetcher then fails to
# checksum a file it cannot find.

FILESEXTRAPATHS:prepend := "${EDGEFIRST_CERT_PATH}/root:"

RAUC_KEYRING_FILE:update = "edgefirst-root.crt.pem"

# Fail the build rather than silently fall back to a keyring whose private
# keys are published.
python () {
    if not bb.utils.contains("DISTRO_FEATURES", "rauc", True, False, d):
        return

    certpath = d.getVar("EDGEFIRST_CERT_PATH")
    if not certpath:
        bb.fatal("EDGEFIRST_CERT_PATH is unset. Set it in conf/local.conf to "
                 "the EdgeFirst signing CA directory, e.g.\n"
                 '    EDGEFIRST_CERT_PATH = "${HOME}/edgefirst-ca"')

    keyring = os.path.join(certpath, "root", d.getVar("RAUC_KEYRING_FILE"))
    if not os.path.exists(keyring):
        bb.fatal("EdgeFirst RAUC keyring not found at %s. Generate the signing "
                 "CA before building a deployable image." % keyring)
}
