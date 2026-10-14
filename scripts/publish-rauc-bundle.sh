#!/bin/bash
# Publish a RAUC bundle and the EdgeFirst root CA to the EdgeFirst repo.
#
#   publish-rauc-bundle.sh MACHINE DEPLOY_DIR [VENDOR]
#
# Layout under the vendor prefix:
#
#   phytec/edgefirst-root.crt.pem                   trust anchor, for reference
#   phytec/<machine>/<bundle>.raucb                 immutable, content-addressed
#   phytec/<machine>/latest.json                    the pointer devices poll
#
# Devices install straight from https://repo.edgefirst.ai/... : RAUC streams
# the bundle over HTTP range requests, so the bucket needs no update server.
# The prefix is anonymously readable, which is why no signed URLs are involved
# -- CloudFront re-evaluates a signed URL's expiry on every range request, and
# a streamed install is thousands of them.
set -euo pipefail

MACHINE="${1:?usage: publish-rauc-bundle.sh MACHINE DEPLOY_DIR [VENDOR]}"
DEPLOY_DIR="${2:?usage: publish-rauc-bundle.sh MACHINE DEPLOY_DIR [VENDOR]}"
VENDOR="${3:-phytec}"

BUCKET="s3://edgefirst-repo/$VENDOR"
BASE_URL="https://repo.edgefirst.ai/$VENDOR"
DISTRIBUTION_ID="ENV44D23AY6RF"
CA_DIR="${EDGEFIRST_CERT_PATH:-$HOME/edgefirst-ca}"

# Resolve the newest real bundle, not the unversioned symlink.
BUNDLE="$(readlink -f "$DEPLOY_DIR/phytec-headless-bundle-$MACHINE.raucb")"
[ -f "$BUNDLE" ] || { echo "no bundle for $MACHINE in $DEPLOY_DIR" >&2; exit 1; }
BUNDLE_NAME="$(basename "$BUNDLE")"

SHA="$(sha256sum "$BUNDLE" | cut -d' ' -f1)"
SIZE="$(stat -c%s "$BUNDLE")"
BUILD="$(sed -n 's/.*-\([0-9]\{14\}\)\.raucb$/\1/p' <<<"$BUNDLE_NAME")"
NOW="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

echo "==> $BUNDLE_NAME  ($(numfmt --to=iec "$SIZE"), sha256 ${SHA:0:16}...)"

# The bundle filename carries its build stamp, so it never changes under a
# given URL and can be cached forever.
aws s3 cp "$BUNDLE" "$BUCKET/$MACHINE/$BUNDLE_NAME" \
    --content-type application/octet-stream \
    --cache-control "public, max-age=31536000, immutable" \
    --only-show-errors

aws s3 cp "$CA_DIR/root/edgefirst-root.crt.pem" "$BUCKET/edgefirst-root.crt.pem" \
    --content-type application/x-pem-file \
    --cache-control "public, max-age=300" \
    --only-show-errors

# latest.json is the only mutable object, so it gets a short TTL. Cache it like
# the bundle and devices keep installing the previous release.
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
cat > "$TMP/latest.json" <<JSON
{
  "machine": "$MACHINE",
  "compatible": "$MACHINE",
  "build": "$BUILD",
  "bundle": "$BASE_URL/$MACHINE/$BUNDLE_NAME",
  "sha256": "$SHA",
  "size": $SIZE,
  "published": "$NOW",
  "keyring": "$BASE_URL/edgefirst-root.crt.pem"
}
JSON
aws s3 cp "$TMP/latest.json" "$BUCKET/$MACHINE/latest.json" \
    --content-type application/json \
    --cache-control "public, max-age=60, must-revalidate" \
    --only-show-errors

# Only the mutable paths need invalidating; the bundle URL is new every build.
aws cloudfront create-invalidation \
    --distribution-id "$DISTRIBUTION_ID" \
    --paths "/$VENDOR/$MACHINE/latest.json" "/$VENDOR/edgefirst-root.crt.pem" \
    --query 'Invalidation.Id' --output text

echo "==> published $BASE_URL/$MACHINE/$BUNDLE_NAME"
echo "    pointer   $BASE_URL/$MACHINE/latest.json"
