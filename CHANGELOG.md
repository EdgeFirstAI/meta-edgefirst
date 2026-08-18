# Changelog — meta-edgefirst

All notable changes to the `meta-edgefirst` Yocto layer are documented here.

Each entry lists package version changes with links to the upstream
CHANGELOG. For full per-package details, follow the links.

## [Unreleased]

- **videostream 2.5.2 → 2.5.3**: Patch release. No public API or ABI changes
  — `SOVERSION` stays at `2`, exported symbol set is identical. Fixes a
  `vsl_frame_attach` dmabuf-fd leak that hit `RLIMIT_NOFILE` after ~34 s at
  30 fps and left `vsl-camhost` silently refusing new clients (`EMFILE` from
  `accept()`); a client watchdog that stayed armed after `vsl_frame_wait`
  and revoked a consumer's frame lock ~1 s after it stopped polling,
  letting the producer recycle a buffer the consumer was still reading; a
  fractional-timeout bug (`create_timer` scaled by `10e9` instead of `1e9`)
  that silently disabled the watchdog for any non-integer
  `vsl_client_set_timeout`; and a double-unlock of `vsl_mutex` on
  `vsl-camhost`'s `do_process` error path. [CHANGELOG](https://github.com/EdgeFirstAI/videostream/blob/v2.5.3/CHANGELOG.md)
- **edgefirst-schemas 3.4.0 → 3.5.0**: SONAME stable at `.so.3`, additive only
  — no symbols removed. Adds full four-language (Rust/C/C++/Python) support
  for six new `nav_msgs`/`sensor_msgs` types (`MapMetaData`, `GridCells`,
  `OccupancyGrid`, `Path`, `RelativeHumidity`, `TimeReference`) with builders
  and zero-copy views, plus a `Path` zero-copy iterator (O(n) vs the previous
  O(n²)). Fixes a `TimeReference::new()` field-offset bug and tightens the
  `Path` CDR sequence-count floor. [CHANGELOG](https://github.com/EdgeFirstAI/schemas/blob/v3.5.0/CHANGELOG.md)
- **edgefirst-tflite 0.7.0 → 0.9.0**: Python wheel only; cp38-abi3 ABI
  unchanged, so the packaged artifact is a drop-in replacement. Upstream adds
  soft-optional LiteRT Next bindings (probed at runtime, never panics when
  absent) alongside the existing classic TFLite path — not exercised by this
  layer's consumers, who use the classic `Interpreter` path.
  [CHANGELOG](https://github.com/EdgeFirstAI/tflite-rs/blob/v0.9.0/CHANGELOG.md)
- **zenoh-c / zenohd / python3-zenoh 1.9.0 → 1.10.0**: Eclipse Zenoh
  upstream release.
- **edgefirst-hal 0.24.2 → 0.28.3**: Four-minor jump, standalone C-library
  package only — nothing in this layer's `DEPENDS`/`RDEPENDS` graph links it
  except `edgefirst-gstreamer`, `nnstreamer`, and `imx-nnstreamer-examples`
  (build-time, via the C API); the prebuilt `edgefirst-camera`/`edgefirst-model`/
  `edgefirst-fusion`/etc. service binaries statically link their own Rust HAL
  dependency at their own build time and do not RDEPEND on this package.
  Upstream breaking changes land mostly in the image/codec surface (0.26.0:
  image constructors require an explicit `CpuAccess` declaration; later
  releases remove `DecodeOptions`/`opts` and the `edgefirst-image`
  `load_image()` free function, and change `edgefirst-codec` to decode to the
  source's native pixel format instead of colour-converting) — this layer's
  in-tree consumers use the tensor/decoder C API
  (`hal_tensor_set_quantization`, `hal_decoder_*`, `hal_shapes`), a different
  surface not touched by those changes; confirmed by a clean
  `torizon-core-maivin` build against 0.28.3. SONAME chain unaffected.
  [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.28.3/CHANGELOG.md)

## v1.2.3 — 2026-05-28

### Package Updates

| Package | v1.2.2 | v1.2.3 | Changelog |
|---------|--------|--------|-----------|
| edgefirst-hal | 0.18.0 | 0.24.2 | [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.24.2/CHANGELOG.md) |
| edgefirst-schemas | 3.1.0 | 3.4.0 | [CHANGELOG](https://github.com/EdgeFirstAI/schemas/blob/v3.4.0/CHANGELOG.md) |
| videostream | 2.5.1 | 2.5.2 | [CHANGELOG](https://github.com/EdgeFirstAI/videostream/blob/v2.5.2/CHANGELOG.md) |
| edgefirst-tflite | 0.5.0 | 0.7.0 | [CHANGELOG](https://github.com/EdgeFirstAI/tflite-rs/blob/v0.7.0/CHANGELOG.md) |
| edgefirst-camera | 2.6.0 | 2.7.0 | [CHANGELOG](https://github.com/EdgeFirstAI/camera/blob/v2.7.0/CHANGELOG.md) |
| edgefirst-model | 2.8.0 | 2.9.0 | [CHANGELOG](https://github.com/EdgeFirstAI/model/blob/v2.9.0/CHANGELOG.md) |
| edgefirst-recorder | 1.7.1 | 1.8.0 | [CHANGELOG](https://github.com/EdgeFirstAI/recorder/blob/v1.8.0/CHANGELOG.md) |
| edgefirst-replay | 2.2.0 | 2.3.1 | [CHANGELOG](https://github.com/EdgeFirstAI/replay/blob/v2.3.1/CHANGELOG.md) |
| edgefirst-websrv | 3.8.5 | 4.0.1 | [CHANGELOG](https://github.com/EdgeFirstAI/websrv/blob/v4.0.1/CHANGELOG.md) |
| edgefirst-webui | 3.8.0 | 4.1.1 | [CHANGELOG](https://github.com/EdgeFirstAI/webui/blob/v4.1.1/CHANGELOG.md) |
| edgefirst-gstreamer | 0.2.0 | 0.4.0 + main | [CHANGELOG](https://github.com/EdgeFirstAI/gstreamer/blob/main/CHANGELOG.md) |
| zenoh-c / zenohd / python3-zenoh | 1.8.0 | 1.9.0 | — |

### Layer Changes

- **edgefirst-hal 0.18 → 0.24.2**: Six-minor jump. C ABI delta from
  0.18: 4 symbols removed (`hal_tensor_load_image{,_file,_jpeg,_png}` —
  replaced by the `edgefirst_codec` decode-into-tensor flow at
  `hal_tensor_decode_image{,_file}`), 10 added in the 0.19–0.23 series
  (`hal_decoder_input_dims`, `hal_decoder_params_set_input_dims`,
  `hal_decoder_params_set_max_det`, `hal_decoder_params_set_pre_nms_top_k`,
  `hal_proto_data_layout`, `hal_start_tracing` / `hal_stop_tracing` /
  `hal_is_tracing_active`, `hal_tensor_decode_image`,
  `hal_tensor_decode_image_file`), 1 added in 0.24
  (`hal_tensor_set_quantization` — lets consumers attach per-tensor
  affine quantization to integer tensors wrapped from upstream
  framework buffers; required by the schema-driven per-scale decoder,
  which reads quant live from each bound tensor and fails with
  `QuantMissing` if absent). 0.24.0 also splits the decoder C-API
  errno mapping: `QuantMissing` / `InvalidShape` / `DtypeMismatch` /
  `InvalidConfig` and related caller-side preconditions now map to
  `EINVAL` (was `EIO`); `EIO` is reserved for internal faults (mutex
  poisoning, unreachable kernel dispatch, hardware-feature mismatch).
  Neither `nnstreamer` nor `edgefirst-gstreamer` reference any of the
  removed symbols. **0.24.2 patch** resets `TEXTURE_SWIZZLE_R` after the
  `RGBA → PlanarRgb` conversion so the red channel is not silently
  zeroed on the next overlay draw — fixes the green-tint regression
  observed on segmentation overlays in 0.24.0/0.24.1. SONAME chain remains
  `libedgefirst_hal.so → .so.0 → .so.0.24 → .so.0.24.2`; recipe install
  logic unchanged. Behavioural changes documented upstream: binary
  `{0, 255}` masks from `MaskResolution::Proto`/`::Scaled` (0.19.0),
  default NMS resolves from model config (`Nms::Auto`, 0.22.0),
  `max_det` default 300 (0.20.0). `edgefirst-gstreamer` explicitly
  sets `HAL_NMS_CLASS_AGNOSTIC` so the Auto default is not exposed.
  **Known issue (workaround in edgefirst-gstreamer):**
  `hal_decoder_normalized_boxes()` returns the schema's static
  `normalized` annotation, not the runtime output state. The per-scale
  pipeline pre-normalises coords to `[0, 1]` whenever
  `hal_decoder_input_dims()` succeeds, but the function still reports
  `Some(false)`. `edgefirst-gstreamer` works around the disagreement at
  start-time; the proper fix is queued for the next HAL release and
  the workaround is a no-op against a corrected HAL.
- **videostream 2.5.1 → 2.5.2**: Patch release. V4L2 encoder now
  honors `crop_region` (BGRA/YUYV), VSL client returns accurate
  `errno` (`ESTALE`/`EBADMSG`/`ENOLCK`), frame lifespan bumped 90 ms →
  200 ms and camera buffer count 4/6 → 8 in `videostream stream` /
  `camhost` to handle slow consumers. Upstream confirms no public API
  or ABI changes — `SOVERSION` stays at 2, exported symbol set is
  identical.
- **ConnMan removed from imx-image-full rootfs.** ConnMan's recipe in
  Poky registers itself as the `update-alternatives` provider for
  `/etc/resolv.conf` (pointing at `/etc/resolv-conf.connman`) whenever
  `systemd` is in `DISTRO_FEATURES`, which fights `systemd-resolved`
  for DNS on target and produces stale/empty resolv.conf at runtime.
  This BSP runs `systemd-networkd` + `systemd-resolved` as the
  authoritative network stack, so ConnMan must not be installed.
  Implemented as two complementary changes in `meta-edgefirst`:
    1. `recipes-core/packagegroups/packagegroup-core-tools-testapps.bbappend`
       removes `connman-tools / connman-tests / connman-client` from
       the `tools-testapps` packagegroup's `RDEPENDS` (the only
       in-tree path that pulled ConnMan).
    2. `conf/layer.conf` adds
       `PACKAGE_EXCLUDE:append = " connman connman-conf connman-client connman-tools connman-tests connman-gnome python3-pyconnman"`
       as a defence-in-depth guard that fails the build loudly if any
       future path tries to pull ConnMan back. Verified that the
       `imx-image-full` rootfs manifest no longer contains any
       `connman*` package on imx8mp-frdm and imx95-frdm.
- **edgefirst-replay 2.2.0 → 2.3.1**: Rebuilt against this layer's
  refreshed deps — pulls in `edgefirst-schemas` 3.4.0, `videostream`
  2.5.2, and `edgefirst-hal` 0.23.x. **Breaking on the wire**:
  `rt/camera/dma` now publishes decoder-native NV12 (matches the live
  camera contract) instead of pre-converting to RGBA. Consumers that
  needed RGBA should subscribe to the new opt-in
  `--camera-image-topic` (env `CAMERA_IMAGE_TOPIC`) which publishes
  `sensor_msgs/Image` rgba8 via the HAL `ImageProcessor` (auto-selects
  G2D / OpenGL / CPU). Replay drops G2D/turbojpeg/dma-heap/tokio
  in-tree image plumbing and routes JPEG decode through the optimized
  `edgefirst-codec` path. h264 decoder switched to
  `Decoder::create_ex(.., CodecBackend::Auto)` so it picks the v4l2
  backend (`/dev/video1 vsi_v4l2dec` on imx8mp, tighter 1920-byte NV12
  stride vs the legacy 2880-byte Hantro path). `main` is now sync to
  avoid colliding with HAL's GL backend `blocking_recv` during
  converter init. **2.3.1 patch:** clean MCAP-restart — back-to-back
  replay against the same MCAP no longer wedges the consumer pipeline
  after EOS (the producer-side DMA-BUF FDs are re-allocated cleanly so
  `edgefirst-model`'s pidfd cache rebinds on the next frame). Note
  that 2.3.1's bundled Zenoh runtime rejects empty endpoint strings:
  the shipped `/etc/default/edgefirst-replay` carries `CONNECT=""` and
  `LISTEN=""` placeholders that must be commented out for the service
  to start (upstream report queued).
- **edgefirst-websrv 3.8.5 → 4.0.1** and **edgefirst-webui 3.8.0 →
  4.1.1**: Coordinated jump to the unified `rt/model/output` schema.
  The legacy `rt/model/boxes2d` / `rt/model/mask` topics are now
  opt-in (`DETECT_TOPIC` / `MASK_TOPIC` empty by default in
  `/etc/default/edgefirst-model`). The 4.0.x websrv/webui pair still
  expected the legacy detection topic shape and rendered no overlays
  against current `edgefirst-model` 2.9 output; 4.0.1 / 4.1.1 consume
  the unified message correctly. Landing-page service cards (Camera,
  LiDAR, …) are gated on `systemctl is-active` for the corresponding
  `edgefirst-<service>.service` unit — services launched outside
  systemd (e.g. `nohup` from a manual replay session) leave the
  landing page empty, but `/camera`, `/lidar`, `/imu`, `/gps` remain
  reachable by direct URL.
- **edgefirst-gstreamer 0.2.0 → 0.4.0 + main**: Picks up the
  schema-driven `edgefirstoverlay` path for YOLOv8 smart-model
  pipelines. Three coordinated overlay changes vs 0.4.0:
    1. CAPS event handler always parses tensor shapes — the
       schema-driven path builds the decoder at `overlay_start()` from
       JSON, but the tensors chain still needs `hal_shapes[]` populated
       to bind by shape. Without this fix the per-scale decoder fails
       with `InvalidShape` on every frame.
    2. Integer outputs are tagged with their per-tensor quantization
       at chain time via `hal_tensor_set_quantization()` (sourced from
       `GstNnsTensorQuantMeta`). The schema-driven per-scale decoder
       reads quant live from each bound tensor and fails with
       `QuantMissing` if it is absent.
    3. Model input dimensions are inferred from the parsed tensor
       shapes (finest per-scale × 8, or strictly-larger single-instance
       proto × 4) when no `model-width` / `model-height` override is
       set, so `overlay_effective_letterbox()` can compute an
       aspect-preserving un-letterbox on the rendered overlays.
  Adds the HAL `normalized_boxes()` / `input_dims()` workaround
  documented under `edgefirst-hal 0.24.0`. Recipe `SRCREV` bumped from
  the v0.4.0 release commit to the head of `main`.
- **imx-nnstreamer-examples**: `yolov8n` binary now extracts the
  embedded `edgefirst.json` schema from the TFLite associated-files
  ZIP trailer and sets it on `edgefirstoverlay`'s `model-config`
  property; when the model has no embedded schema the overlay falls
  back to its `decoder-version=yolov8` + tensor-shape inference path.
  Recipe gains a `zlib` build dependency for the inflate-on-target
  step. `SRCREV` bumped to the head of `edgefirst-yolov8` on the
  EdgeFirstAI fork.
- **edgefirst-schemas 3.1.0 → 3.4.0**: SONAME stable at `.so.3`. Zero
  C symbols removed, 635 added (geometry_msgs / mavros_msgs message
  types, full builder pattern surface). 3.2.0 introduced a PyO3 rewrite
  of the Python module — recipe now pulls the arch-specific
  `cp311-abi3-manylinux_2_17_aarch64` wheel from GitHub releases instead
  of the legacy `py3-none-any` wheel from PyPI. `python3-pip-native`
  dependency dropped (no longer needed for the unzip-based install).
  Recipe adds `PRIVATE_LIBS:${PN}-python = "libedgefirst_schemas.so.3"`
  to work around an upstream wheel-build bug: `schemas.abi3.so` is
  linked with SONAME `libedgefirst_schemas.so.3` inherited from the
  Rust cdylib metadata, which without `PRIVATE_LIBS` would cause
  `do_package` to fail with "Multiple shlib providers". Track-and-fix
  upstream so the Python extension's SONAME matches its filename.
- **edgefirst-tflite 0.5.0 → 0.7.0**: Python wheel only; cp38-abi3 ABI
  unchanged.
- **edgefirst-camera 2.6.0 → 2.7.0** and **edgefirst-recorder 1.7.1 →
  1.8.0**: Minor bumps; no recipe-level changes beyond checksum refresh.
- **edgefirst-model 2.8.0 → 2.9.0**: Rebuilt against this layer's
  refreshed deps — pulls in edgefirst-hal 0.23, edgefirst-tflite 0.7,
  edgefirst-tracker 0.23, and ara2 0.10. Adds Neutron NPU support for
  i.MX 95 (auto-selects `libneutron_delegate.so` vs `libvx_delegate.so`
  by scanning the loaded model for the Neutron custom op),
  aspect-preserving letterbox preprocessing with back-projection of
  detections/masks to original-frame coordinates, full DMA-BUF
  zero-copy input pipeline across all three runtimes, and support for
  the `edgefirst.json` embedded model config alongside
  `edgefirst.yaml`. Forces the G2D image backend (HAL 0.23's threaded
  GL backend uses `tokio::mpsc::blocking_send` which panics under the
  service's tokio runtime). No recipe-level changes beyond the
  checksum refresh.
- **zenoh-c / zenohd / python3-zenoh 1.8.0 → 1.9.0**: Eclipse Zenoh
  upstream release.
- **meta-kinara edgefirst-ara2 0.5.0 → 0.10.0**: Python bindings bumped
  through five intermediate releases. The wrapped HAL surface is pinned
  to HAL 0.23 in lockstep (matches this layer's bump). `dequantize()`
  qmode-9 formula correction (0.4.0), `Session.close()`/`Model.close()`
  context-manager fix (0.4.0), `OutputQuantization.scale` removed,
  `InputQuantization.mean/scale` moved to `InputPreprocess` (0.4.0
  migration items still apply to consumers of the older Python API).

## v1.2.2 — 2026-04-26

### Package Updates

| Package | v1.2.1 | v1.2.2 | Changelog |
|---------|--------|--------|-----------|
| edgefirst-hal | 0.16.4 | 0.18.0 | [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.18.0/CHANGELOG.md) |
| edgefirst-gstreamer | 0.3.0 | 0.4.0 | [CHANGELOG](https://github.com/EdgeFirstAI/gstreamer/blob/v0.4.0/CHANGELOG.md) |
| edgefirst-schemas | 2.2.1 | 3.1.0 | [CHANGELOG](https://github.com/EdgeFirstAI/schemas/blob/v3.1.0/CHANGELOG.md) |
| videostream | 2.2.2 | 2.5.1 | [CHANGELOG](https://github.com/EdgeFirstAI/videostream/blob/v2.5.1/CHANGELOG.md) |
| edgefirst-tflite | 0.4.0 | 0.5.0 | [CHANGELOG](https://github.com/EdgeFirstAI/tflite-rs/blob/v0.5.0/CHANGELOG.md) |

### Layer Changes

- **Unified `yolov8n` binary**: Replaced 6 separate detection/segmentation
  binaries (`yolov8n_imx8mp`, `yolov8n_imx95`, `yolov8n_ara2`,
  `yolov8n_seg_ara2`, etc.) with a single `yolov8n` binary that
  auto-detects platform, NPU backend, and model type (detection vs.
  segmentation) from model metadata. Supports `-p`, `-m`, `-c`, `-v`,
  `-H`, `-I`, `-D`, `-n` flags. Reference baselines remain as separate
  `yolov8n_reference` and `yolov8n_ara2_reference` binaries.
- **edgefirst-gstreamer 0.4.0**: Fixed Vivante GC7000 proto mask regression
  (>2200 ms/frame → ~25 ms using CPU-materialized decoded masks path).
  Added model-metadata decoder path for automatic HAL configuration from
  edgefirst.json v2 schema. Removed Ara-2 dimension correction workarounds
  (fixed upstream in NNStreamer).
- **NNStreamer Ara-2 dimension fix**: Corrected Ara-2 tensor_filter output
  dimension ordering from native C-contiguous (outermost-first) to
  NNStreamer's innermost-first convention, enabling uniform handling of
  both TFLite and Ara-2 tensors without overlay workarounds.
- **edgefirst-schemas SONAME bump**: Major version change from `.so.2` to
  `.so.3`. Dependent packages must be rebuilt against schemas 3.1.0.
- **imx-nnstreamer-examples**: Updated for unified binary, VX DmaBuf
  uint8 dtype fix, color-mode flag, and save-frame support.

## v1.2.1 — 2026-04-20

### Package Updates

| Package | v1.2.0 | v1.2.1 | Changelog |
|---------|--------|--------|-----------|
| edgefirst-hal | 0.16.3 | 0.16.4 | [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.16.4/CHANGELOG.md) |

### Layer Changes

- **imx-nnstreamer-examples do\_install fix**: Replaced `do_install:append`
  with a full `do_install` override that uses `${S}` and `${B}` throughout.
  The upstream recipe in `meta-nxp-demo-experience` hardcodes
  `${WORKDIR}/git` and `${WORKDIR}/build` which breaks with `devtool`
  (externalsrc) and Walnascar's `UNPACKDIR`.

## v1.2.0 — 2026-04-16

### Package Updates

| Package | v1.1 | v1.2.0 | Changelog |
|---------|------|--------|-----------|
| edgefirst-hal | 0.8.0 | 0.16.3 | [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.16.3/CHANGELOG.md) |
| edgefirst-schemas | 1.5.5 | 2.2.1 | [CHANGELOG](https://github.com/EdgeFirstAI/schemas/blob/v2.2.1/CHANGELOG.md) |
| edgefirst-gstreamer | 0.1.1 | 0.3.0 | [CHANGELOG](https://github.com/EdgeFirstAI/gstreamer/blob/v0.3.0/CHANGELOG.md) |
| edgefirst-camera | 2.5.0 | 2.6.0 | [CHANGELOG](https://github.com/EdgeFirstAI/camera/blob/v2.6.0/CHANGELOG.md) |
| edgefirst-model | 2.7.0 | 2.8.0 | [CHANGELOG](https://github.com/EdgeFirstAI/model/blob/v2.8.0/CHANGELOG.md) |
| edgefirst-fusion | 1.6.0 | 1.7.2 | [CHANGELOG](https://github.com/EdgeFirstAI/fusion/blob/v1.7.2/CHANGELOG.md) |
| edgefirst-websrv | 3.8.4 | 4.0.0 | [CHANGELOG](https://github.com/EdgeFirstAI/websrv/blob/v4.0.0/CHANGELOG.md) |
| edgefirst-webui | 3.8.0 | 4.0.1 | [CHANGELOG](https://github.com/EdgeFirstAI/webui/blob/v4.0.1/CHANGELOG.md) |
| edgefirst-navsat | 1.5.1 | 1.6.0 | [CHANGELOG](https://github.com/EdgeFirstAI/navsat/blob/v1.6.0/CHANGELOG.md) |
| edgefirst-imu | 3.0.5 | 3.1.0 | [CHANGELOG](https://github.com/EdgeFirstAI/imu/blob/v3.1.0/CHANGELOG.md) |
| edgefirst-lidarpub | 2.1.0 | 2.2.1 | [CHANGELOG](https://github.com/EdgeFirstAI/lidarpub/blob/v2.2.1/CHANGELOG.md) |
| edgefirst-recorder | 1.7.0 | 1.7.1 | [CHANGELOG](https://github.com/EdgeFirstAI/recorder/blob/v1.7.1/CHANGELOG.md) |
| videostream | 2.2.1 | 2.2.2 | [CHANGELOG](https://github.com/EdgeFirstAI/videostream/blob/v2.2.2/CHANGELOG.md) |
| zenoh-c / zenohd / python3-zenoh | 1.7.2 | 1.8.0 | — |
| edgefirst-radarpub | 1.6.3 | 1.6.3 | *(unchanged)* |
| edgefirst-replay | 2.2.0 | 2.2.0 | *(unchanged)* |

### Layer Changes

- **Neutron NPU DMA-BUF zero-copy**: Added kernel patch
  (`staging-neutron-export-buffers-as-dma-buf.patch`), Neutron delegate
  bbappend, and VX delegate bbappend on `edgefirst` branches to enable
  zero-copy inference on i.MX 95 (EDGEAI-1185)
- **NNStreamer `edgefirst` branch**: Consolidated from separate
  `edgefirst-tflite`, `edgefirst-dmabuf`, `edgefirst-ara2` branches
  into a single `edgefirst` branch. Added proactive dlopen check in
  `tensor_filter` to prevent crash on missing delegate library.
- **edgefirst-hal SONAME fix**: HAL 0.16.2 and schemas 2.2.1 ship
  proper SONAME symlink chains. Recipes simplified from manual `ln -sf`
  to `cp -a` preserving upstream symlinks. `INSANE_SKIP file-rdeps`
  removed from `imx-nnstreamer-examples` — shlibdeps now auto-resolves.
- **edgefirst-hal 0.16.3**: Bumped to 0.16.3. Fixes Mali Valhall (i.MX 95)
  DMA-BUF pitch alignment for `draw_decoded_masks` / `draw_proto_masks`
  (GPU path was silently degrading to CPU ~10–20× slower). Also eliminates
  `glFinish`-per-instance in the segmentation draw loop (~10 ms → 9 ms
  for 39-detection crowd scene on i.MX 95).
- **edgefirstoverlay DMA-BUF double-buffer**: `edgefirstoverlay` updated for
  HAL 0.16.3 stride padding and a DMA-BUF race fix. Display image is now
  double-buffered (`display_images[2]`) so frame N's DMA-BUF fd remains valid
  while frame N+1 renders into the other buffer. HAL stride padding
  (`row_stride > w × 4` on Mali Valhall) is handled: DMA-BUF size and
  `GstVideoMeta` stride both use `hal_tensor_row_stride()`; memcpy fallback
  strips padding row-by-row. Weak symbol guards for HAL 0.15.0 removed.
- **YOLOv8n segmentation**: Added `yolov8n_seg` and `yolov8n_seg_ara2`
  binaries plus `yolov8n_seg.sh` shell script to examples recipe.
- **PipelineProbes**: Shared per-element pad probe infrastructure added
  to all YOLOv8n binaries for consistent pipeline timing instrumentation.
- **edgefirst-gstreamer 0.2.0**: `edgefirstoverlay` element redesigned
  with dual-sink for display + headless. NV12 two-fd plane import fixed
  for `edgefirstcameraadaptor`. Migrated to schemas 2.2.x API.
- **edgefirst-gstreamer 0.3.0**: `edgefirstoverlay` NV12 plane offset
  corruption fixed — now uses `GstVideoMeta` (authoritative) instead of
  `GstVideoInfo` (tight-packed assumption), eliminating the ~10-pixel
  magenta band at the top when decoding H.264 on i.MX 8M Plus. Added
  auto-letterbox computation from video/model dimensions; split-box
  detection works for both TFLite features-first and Ara-2 anchors-first
  shapes; box quantization scale adjustment is now conditional on the
  `normalized` property. Current Ara-2 DVM pipelines must set
  `normalized=false`; TFLite pipelines use the default. Known issue:
  TFLite NHWC proto tensors produce striped mask artifacts on i.MX 8M Plus
  pending a HAL-side layout fix.

## v1.1 — 2026-03-02

Initial tagged release. See
[edgefirst-imx-6.12.49-2.2.0.xml](https://github.com/EdgeFirstAI/yocto)
at tag `imx-6.12.49-2.2.0-20260301` for the pinned manifest.
