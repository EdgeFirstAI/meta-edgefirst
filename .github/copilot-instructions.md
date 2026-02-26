# meta-edgefirst — AI Coding Agent Instructions

## Yocto Release Compatibility

This layer **MUST** support both **Scarthgap** and **Walnascar** Yocto releases simultaneously. All recipes must build cleanly on both.

```
LAYERSERIES_COMPAT_meta-edgefirst = "scarthgap walnascar"
```

### UNPACKDIR / S Compatibility

Walnascar introduced `UNPACKDIR` (defaults to `${WORKDIR}/sources`) to separate unpacked sources from other working directory files. Scarthgap does not define `UNPACKDIR` — all files unpack directly to `${WORKDIR}`.

**Required pattern** — use an inline Python expression for `S`:

```bitbake
S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"
```

This resolves to `UNPACKDIR` on Walnascar (where it is defined by OE-core) and falls back to `WORKDIR` on Scarthgap (where `UNPACKDIR` is `None`).

**Do NOT:**

- Set `S = "${WORKDIR}"` — Walnascar's `do_qa_unpack` fatals on raw `${WORKDIR}` references.
- Set `S = "${WORKDIR}/sources"` with `UNPACKDIR = "${S}"` — on Scarthgap the build system ignores the recipe-defined `UNPACKDIR` and still unpacks to `${WORKDIR}`, leaving `sources/` empty.
- Use `if [ "${UNPACKDIR}" != "" ]` shell guards in `do_install` — this is fragile and unnecessary when `S` is set correctly. Just reference `${S}` uniformly.
- Define `UNPACKDIR` in recipes — let OE-core manage it. Recipes should only set `S`.

### Recipe Patterns

**Binary download recipes** (e.g., edgefirst-camera, edgefirst-model):

```bitbake
S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

do_install:append () {
    install -m 0755 ${S}/edgefirst-camera ${D}${bindir}/edgefirst-camera
    install -m 0644 ${S}/edgefirst-camera.service ${D}${systemd_system_unitdir}
}
```

**Zip archive recipes** (e.g., videostream, zenoh-c):

```bitbake
python () {
    arch = d.getVar('TARGET_ARCH')
    sha256 = d.getVarFlag('SRC_URI_SHA256SUM', arch)
    if sha256:
        d.setVarFlag('SRC_URI', 'sha256sum', sha256)
}

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

do_install () {
    install -d ${D}${libdir}
    cp -P ${S}/lib/libvideostream.so* ${D}${libdir}
}
```

**Reference implementation:** See `meta-kinara/recipes-kinara/ara2/ara2_1.2.1.bb` for the original dual-compat pattern (uses shell if/else guards — the inline Python `S` approach above is preferred as it is simpler).

## General Recipe Guidelines

- Use Scarthgap override syntax (colon separator): `RDEPENDS:${PN}`, `FILES:${PN}`, `INSANE_SKIP:${PN}` — **not** the old dash syntax which is silently ignored.
- Pre-built binary recipes should set `do_configure[noexec] = "1"` and `do_compile[noexec] = "1"`.
- Always include `INHIBIT_PACKAGE_STRIP = "1"` and `INHIBIT_PACKAGE_DEBUG_SPLIT = "1"` for pre-built binaries.
- Architecture-specific checksums should use varflags and a `python()` function to set the correct checksum at parse time.
