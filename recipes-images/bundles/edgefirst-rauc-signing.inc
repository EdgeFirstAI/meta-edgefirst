# Sign RAUC bundles with the EdgeFirst per-vendor signing CA rather than
# PHYTEC's development CA.
#
# Chain: EdgeFirst Root CA -> EdgeFirst <VENDOR> CA -> <vendor>-1 leaf.
# Devices trust only the root (see recipes-core/rauc/rauc-conf.bbappend), so
# the intermediate travels inside the bundle via --intermediate= and either it
# or the leaf can be rotated without reflashing hardware.
#
# One vendor branch of the CA per BSP vendor keeps a leaked signing key scoped
# to that vendor's product line.
#
# EDGEFIRST_SIGNING_CERT_PATH selects the material used to SIGN, and defaults
# to the same CA the image TRUSTS. Separating the two is what makes a CA
# migration possible: a device only accepts a bundle signed by the CA it
# already trusts, so the bundle that carries a new keyring has to be signed
# with the outgoing key. Point this at the outgoing CA for that one bundle.

EDGEFIRST_SIGNING_CERT_PATH ?= "${EDGEFIRST_CERT_PATH}"
EDGEFIRST_SIGNING_VENDOR ?= "phytec"
EDGEFIRST_SIGNING_DIR = "${EDGEFIRST_SIGNING_CERT_PATH}/${EDGEFIRST_SIGNING_VENDOR}"

RAUC_KEY_FILE = "${EDGEFIRST_SIGNING_DIR}/private/${EDGEFIRST_SIGNING_VENDOR}-1.key.pem"
RAUC_CERT_FILE = "${EDGEFIRST_SIGNING_DIR}/${EDGEFIRST_SIGNING_VENDOR}-1.cert.pem"
RAUC_INTERMEDIATE_CERT_FILE = "${EDGEFIRST_SIGNING_DIR}/edgefirst-${EDGEFIRST_SIGNING_VENDOR}.crt.pem"
RAUC_KEYRING_FILE = "${EDGEFIRST_SIGNING_CERT_PATH}/root/edgefirst-root.crt.pem"

do_bundle[file-checksums] += "${RAUC_INTERMEDIATE_CERT_FILE}:False ${RAUC_KEYRING_FILE}:False"

python () {
    if not bb.utils.contains("DISTRO_FEATURES", "rauc", True, False, d):
        return

    if not d.getVar("EDGEFIRST_SIGNING_CERT_PATH"):
        bb.fatal("EDGEFIRST_CERT_PATH is unset. Set it in conf/local.conf to "
                 "the EdgeFirst signing CA directory, e.g.\n"
                 '    EDGEFIRST_CERT_PATH = "${HOME}/edgefirst-ca"')

    for var in ("RAUC_KEY_FILE", "RAUC_CERT_FILE",
                "RAUC_INTERMEDIATE_CERT_FILE", "RAUC_KEYRING_FILE"):
        path = d.getVar(var)
        if not os.path.exists(path):
            bb.fatal("%s points at %s, which does not exist. Generate the "
                     "EdgeFirst signing CA for vendor '%s' before building a "
                     "deployable bundle."
                     % (var, path, d.getVar("EDGEFIRST_SIGNING_VENDOR")))
}
