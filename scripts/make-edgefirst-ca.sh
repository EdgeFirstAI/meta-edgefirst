#!/bin/bash
# Generate the EdgeFirst RAUC signing CA: offline root -> per-vendor
# intermediate -> per-vendor signing leaf.
#
#   make-edgefirst-ca.sh [CA_DIR] [VENDOR]
#
# Re-running creates a new vendor branch under an existing root; it refuses to
# overwrite a root that already exists.
set -euo pipefail

CA_DIR="${1:-$HOME/edgefirst-ca}"
VENDOR="${2:-phytec}"

ORG="Au-Zone Technologies"
ORG_UNIT="EdgeFirst"
ROOT_CN="EdgeFirst Root CA"
VENDOR_CN="EdgeFirst ${VENDOR^^} CA"
LEAF_CN="EdgeFirst ${VENDOR^^} Signing 1"

ROOT_DAYS=10958   # ~30 years; devices outlive their certificates
INTER_DAYS=3653   # ~10 years
LEAF_DAYS=1096    # ~3 years

umask 077
mkdir -p "$CA_DIR/root/private" "$CA_DIR/$VENDOR/private"
chmod 700 "$CA_DIR" "$CA_DIR/root/private" "$CA_DIR/$VENDOR/private"

ROOT_KEY="$CA_DIR/root/private/edgefirst-root.key.pem"
ROOT_CRT="$CA_DIR/root/edgefirst-root.crt.pem"
INT_KEY="$CA_DIR/$VENDOR/private/edgefirst-$VENDOR.key.pem"
INT_CRT="$CA_DIR/$VENDOR/edgefirst-$VENDOR.crt.pem"
LEAF_KEY="$CA_DIR/$VENDOR/private/$VENDOR-1.key.pem"
LEAF_CRT="$CA_DIR/$VENDOR/$VENDOR-1.cert.pem"

ext() { printf '%s\n' "$@"; }

if [ -f "$ROOT_CRT" ]; then
    echo "root CA already present, reusing: $ROOT_CRT"
else
    echo "==> root CA (RSA-4096, ${ROOT_DAYS}d)"
    openssl genrsa -out "$ROOT_KEY" 4096 2>/dev/null
    ext "basicConstraints = critical, CA:TRUE, pathlen:1" \
        "keyUsage = critical, digitalSignature, cRLSign, keyCertSign" \
        "subjectKeyIdentifier = hash" \
        "authorityKeyIdentifier = keyid:always" > "$CA_DIR/root/root.ext"
    openssl req -new -sha256 -key "$ROOT_KEY" -out "$CA_DIR/root/root.csr" \
        -subj "/O=$ORG/OU=$ORG_UNIT/CN=$ROOT_CN"
    openssl x509 -req -sha256 -days "$ROOT_DAYS" \
        -in "$CA_DIR/root/root.csr" -out "$ROOT_CRT" \
        -signkey "$ROOT_KEY" -extfile "$CA_DIR/root/root.ext" 2>/dev/null
    rm -f "$CA_DIR/root/root.ext" "$CA_DIR/root/root.csr"
fi

echo "==> $VENDOR intermediate CA (RSA-4096, ${INTER_DAYS}d)"
openssl genrsa -out "$INT_KEY" 4096 2>/dev/null
openssl req -new -sha256 -key "$INT_KEY" -out "$CA_DIR/$VENDOR/int.csr" \
    -subj "/O=$ORG/OU=$ORG_UNIT/CN=$VENDOR_CN"
ext "basicConstraints = critical, CA:TRUE, pathlen:0" \
    "keyUsage = critical, digitalSignature, cRLSign, keyCertSign" \
    "subjectKeyIdentifier = hash" \
    "authorityKeyIdentifier = keyid:always" > "$CA_DIR/$VENDOR/int.ext"
openssl x509 -req -sha256 -days "$INTER_DAYS" \
    -in "$CA_DIR/$VENDOR/int.csr" -out "$INT_CRT" \
    -CA "$ROOT_CRT" -CAkey "$ROOT_KEY" -CAcreateserial \
    -extfile "$CA_DIR/$VENDOR/int.ext" 2>/dev/null
rm -f "$CA_DIR/$VENDOR/int.csr" "$CA_DIR/$VENDOR/int.ext"

echo "==> $VENDOR signing leaf (RSA-4096, ${LEAF_DAYS}d)"
openssl genrsa -out "$LEAF_KEY" 4096 2>/dev/null
openssl req -new -sha256 -key "$LEAF_KEY" -out "$CA_DIR/$VENDOR/leaf.csr" \
    -subj "/O=$ORG/OU=$ORG_UNIT/CN=$LEAF_CN"
# emailProtection is required alongside codeSigning: OpenSSL's CMS_verify
# applies the S/MIME signing purpose, which rejects a signer whose EKU is
# present but omits emailProtection.
ext "basicConstraints = critical, CA:FALSE" \
    "keyUsage = critical, digitalSignature" \
    "extendedKeyUsage = codeSigning, emailProtection" \
    "subjectKeyIdentifier = hash" \
    "authorityKeyIdentifier = keyid:always" > "$CA_DIR/$VENDOR/leaf.ext"
openssl x509 -req -sha256 -days "$LEAF_DAYS" \
    -in "$CA_DIR/$VENDOR/leaf.csr" -out "$LEAF_CRT" \
    -CA "$INT_CRT" -CAkey "$INT_KEY" -CAcreateserial \
    -extfile "$CA_DIR/$VENDOR/leaf.ext" 2>/dev/null
rm -f "$CA_DIR/$VENDOR/leaf.csr" "$CA_DIR/$VENDOR/leaf.ext"

chmod 644 "$ROOT_CRT" "$INT_CRT" "$LEAF_CRT"
chmod 600 "$ROOT_KEY" "$INT_KEY" "$LEAF_KEY"

echo
echo "==> chain verification"
openssl verify -CAfile "$ROOT_CRT" -untrusted "$INT_CRT" "$LEAF_CRT"
