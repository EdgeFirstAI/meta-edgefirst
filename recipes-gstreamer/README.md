# recipes-gstreamer

Yocto recipes for [EdgeFirst Perception for GStreamer](https://github.com/EdgeFirstAI/.github/blob/main/profile/gstreamer.md) — spatial perception capabilities for the GStreamer multimedia framework, including ML preprocessing, Zenoh bridging, and NNStreamer integration.

See the [EdgeFirst organization](https://github.com/EdgeFirstAI) for the full project overview.

## Recipes

| Recipe | Repo | Description |
|--------|------|-------------|
| edgefirst-gstreamer | [gstreamer](https://github.com/EdgeFirstAI/gstreamer) | EdgeFirst GStreamer elements: `edgefirstcameraadaptor` (HAL-managed ML preprocessing), `edgefirstoverlay` (detection/mask rendering), `edgefirstzenohsub`/`edgefirstzenohpub` (Zenoh bridge), sensor fusion elements |
| nnstreamer | [nnstreamer](https://github.com/EdgeFirstAI/nnstreamer) | EdgeFirst fork of NNStreamer (bbappend): DMA-BUF zero-copy tensor handling, HAL delegate probing, Ara-2 NPU sub-filter |
| imx-nnstreamer-examples | [nxp-nnstreamer-examples](https://github.com/EdgeFirstAI/nxp-nnstreamer-examples) | YOLOv8n detection and segmentation demo binaries (bbappend) |
| packagegroup-imx-ml | — | Extends NXP ML packagegroup to include `nnstreamer-ara2` on Ara-2-capable machines (bbappend) |
