# Changelog — meta-edgefirst

All notable changes to the `meta-edgefirst` Yocto layer are documented here.

Each entry lists package version changes with links to the upstream
CHANGELOG. For full per-package details, follow the links.

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
