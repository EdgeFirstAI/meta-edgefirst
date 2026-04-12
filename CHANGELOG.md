# Changelog — meta-edgefirst

All notable changes to the `meta-edgefirst` Yocto layer are documented here.

Each entry lists package version changes with links to the upstream
CHANGELOG. For full per-package details, follow the links.

## [Unreleased] — Changes since v1.1 (2026-03-02)

### Package Updates

| Package | v1.1 | Current | Changelog |
|---------|------|---------|-----------|
| edgefirst-hal | 0.8.0 | 0.16.2 | [CHANGELOG](https://github.com/EdgeFirstAI/hal/blob/v0.16.2/CHANGELOG.md) |
| edgefirst-schemas | 1.5.5 | 2.2.1 | [CHANGELOG](https://github.com/EdgeFirstAI/schemas/blob/v2.2.1/CHANGELOG.md) |
| edgefirst-gstreamer | 0.1.1 | 0.2.0 | [CHANGELOG](https://github.com/EdgeFirstAI/gstreamer/blob/main/CHANGELOG.md) |
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
- **YOLOv8n segmentation**: Added `yolov8n_seg` and `yolov8n_seg_ara2`
  binaries plus `yolov8n_seg.sh` shell script to examples recipe.
- **PipelineProbes**: Shared per-element pad probe infrastructure added
  to all YOLOv8n binaries for consistent pipeline timing instrumentation.
- **edgefirst-gstreamer 0.2.0**: `edgefirstoverlay` element redesigned
  with dual-sink for display + headless. NV12 two-fd plane import fixed
  for `edgefirstcameraadaptor`. Migrated to schemas 2.2.x API.

## v1.1 — 2026-03-02

Initial tagged release. See
[edgefirst-imx-6.12.49-2.2.0.xml](https://github.com/EdgeFirstAI/yocto)
at tag `imx-6.12.49-2.2.0-20260301` for the pinned manifest.
