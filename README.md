# meta-edgefirst

Yocto layer for the **EdgeFirst Perception Platform** targeting NXP i.MX processors.

meta-edgefirst provides the complete EdgeFirst ecosystem for embedded perception: Zenoh-based
sensor services, NNStreamer/GStreamer ML inference pipelines, NPU-accelerated model execution,
and supporting infrastructure. It is designed to work with the NXP i.MX Yocto BSP (walnascar
release) and brings multi-sensor AI perception to i.MX8M Plus and i.MX93 platforms.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                     EdgeFirst Perception Platform                   │
├─────────────────────────────────┬───────────────────────────────────┤
│   EdgeFirst Perception          │   EdgeFirst Perception            │
│   (Zenoh Services)              │   for GStreamer                   │
│                                 │                                   │
│  ┌───────────┐ ┌────────────┐  │  ┌────────────┐ ┌─────────────┐  │
│  │  camera    │ │  radarpub  │  │  │ nnstreamer │ │gst-edgefirst│  │
│  │  imu       │ │  lidarpub  │  │  │ (EdgeFirst │ │  (3D Spatial│  │
│  │  navsat    │ │  publisher │  │  │   fork)    │ │  PointCloud │  │
│  │  recorder  │ │  replay    │  │  │            │ │  RadarCube  │  │
│  │  webui     │ │            │  │  │            │ │  Fusion     │  │
│  └─────┬──────┘ └─────┬──────┘  │  └──────┬─────┘ └──────┬──────┘  │
│        │              │         │         │              │          │
│        └──────┬───────┘         │         └──────┬───────┘          │
│               │                 │                │                  │
├───────────────┼─────────────────┼────────────────┼──────────────────┤
│               ▼                 │                ▼                  │
│  ┌────────────────────────┐     │  ┌──────────────────────────┐     │
│  │   edgefirst-schemas    │     │  │   tflite-vx-delegate     │     │
│  │   (C lib + Python)     │     │  │   tim-vx                 │     │
│  └────────────────────────┘     │  │   (NPU acceleration)     │     │
│                                 │  └──────────────────────────┘     │
├─────────────────────────────────┴───────────────────────────────────┤
│                     Supporting Infrastructure                       │
│  ┌──────────────────────┐  ┌────────────────────────────────────┐   │
│  │  zenohd / libzenohc  │  │  videostream (V4L2/ISP capture)   │   │
│  │  python3-zenoh       │  │                                    │   │
│  └──────────────────────┘  └────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────────┤
│                     NXP i.MX Yocto BSP (walnascar)                  │
│          meta-imx  ·  meta-freescale  ·  meta-openembedded          │
└─────────────────────────────────────────────────────────────────────┘
```

## Recipe Groups

### recipes-perception — Zenoh-Based Sensor Services

Standalone Zenoh-native services that publish, record, and replay sensor data. Each service
runs as a systemd unit and communicates over the Zenoh protocol using EdgeFirst Schemas for
message serialization.

| Recipe | GitHub Repository | Description |
|--------|-------------------|-------------|
| `edgefirst-camera` | [EdgeFirstAI/camera](https://github.com/EdgeFirstAI/camera) | Camera capture service — V4L2/ISP acquisition, Zenoh image publishing |
| `edgefirst-imu` | [EdgeFirstAI/imu](https://github.com/EdgeFirstAI/imu) | IMU service — accelerometer/gyroscope data from I2C/SPI sensors |
| `edgefirst-navsat` | [EdgeFirstAI/navsat](https://github.com/EdgeFirstAI/navsat) | GNSS/GPS navigation satellite receiver service |
| `edgefirst-radarpub` | [EdgeFirstAI/radarpub](https://github.com/EdgeFirstAI/radarpub) | Radar publisher — automotive radar sensor data over Zenoh |
| `edgefirst-lidarpub` | [EdgeFirstAI/lidarpub](https://github.com/EdgeFirstAI/lidarpub) | LiDAR publisher — point cloud data from LiDAR sensors |
| `edgefirst-publisher` | [EdgeFirstAI/publisher](https://github.com/EdgeFirstAI/publisher) | Generic Zenoh topic publisher and bridge service |
| `edgefirst-recorder` | [EdgeFirstAI/recorder](https://github.com/EdgeFirstAI/recorder) | Session recorder — captures Zenoh streams to disk for replay |
| `edgefirst-replay` | [EdgeFirstAI/replay](https://github.com/EdgeFirstAI/replay) | Session replay — replays recorded Zenoh sessions |
| `edgefirst-webui` | [EdgeFirstAI/webui](https://github.com/EdgeFirstAI/webui) | Web dashboard for monitoring and configuring EdgeFirst services |
| `edgefirst-schemas` | [EdgeFirstAI/schemas](https://github.com/EdgeFirstAI/schemas) | Shared schema library (C + Python) for EdgeFirst message types |

### recipes-nnstreamer — ML Inference Pipelines

GStreamer/NNStreamer plugins for real-time ML inference on video and sensor streams.

| Recipe | GitHub Repository | Description |
|--------|-------------------|-------------|
| `nnstreamer` | [EdgeFirstAI/nnstreamer](https://github.com/EdgeFirstAI/nnstreamer) | EdgeFirst fork of NNStreamer — adds dmabuf zero-copy, TFLite-VX CameraAdaptor, Ara-2 sub-filter support |
| `gst-edgefirst` | [EdgeFirstAI/gst-edgefirst](https://github.com/EdgeFirstAI/gst-edgefirst) | EdgeFirst GStreamer plugins — 3D Spatial Perception, PointCloud processing, RadarCube decoding, sensor fusion elements, visualization overlays, Zenoh bridge elements |

### recipes-extensions — NPU Acceleration Libraries

Bbappend files for NXP ML libraries, pulling EdgeFirstAI forks with optimizations and fixes
for the i.MX NPU (Neural Processing Unit).

| Recipe | GitHub Repository | Description |
|--------|-------------------|-------------|
| `tim-vx` | [EdgeFirstAI/tim-vx](https://github.com/EdgeFirstAI/tim-vx) | Tensor Interface Module for OpenVX — NPU graph compiler interface |
| `tflite-vx-delegate` | [EdgeFirstAI/tflite-vx-delegate](https://github.com/EdgeFirstAI/tflite-vx-delegate) | TFLite delegate for VeriSilicon NPU — routes TFLite operations to the NPU via TIM-VX |

### recipes-support — Supporting Infrastructure

Core infrastructure services and libraries required by the perception and GStreamer stacks.

| Recipe | GitHub Repository | Description |
|--------|-------------------|-------------|
| `zenoh` | [eclipse-zenoh/zenoh](https://github.com/eclipse-zenoh/zenoh) | Zenoh router daemon (zenohd), libzenohc, and python3-zenoh bindings |
| `videostream` | [EdgeFirstAI/videostream](https://github.com/EdgeFirstAI/videostream) | V4L2/ISP video capture library with DMA-BUF support for zero-copy pipelines |

## Packagegroups

### packagegroup-edgefirst-perception

Installs the full EdgeFirst Perception stack — all Zenoh-based sensor services, the schemas
library, and the Zenoh router daemon.

**Includes:** zenohd, edgefirst-schemas, edgefirst-camera, edgefirst-imu, edgefirst-navsat,
edgefirst-radarpub, edgefirst-lidarpub, edgefirst-publisher, edgefirst-recorder,
edgefirst-replay, edgefirst-webui

### packagegroup-edgefirst-gstreamer

Installs the EdgeFirst GStreamer/NNStreamer inference pipeline stack with NPU acceleration.

**Includes:** nnstreamer, gst-edgefirst

## Dependencies

This layer requires the following layers from the NXP i.MX Yocto BSP (walnascar release):

| Layer | Repository | Purpose |
|-------|-----------|---------|
| `meta-imx` | [nxp-imx/meta-imx](https://github.com/nxp-imx/meta-imx) | NXP i.MX BSP — kernel, firmware, GPU/VPU drivers |
| `meta-imx-ml` | [nxp-imx/meta-imx](https://github.com/nxp-imx/meta-imx) | NXP ML libraries — TensorFlow Lite, TIM-VX, ONNX Runtime |
| `openembedded-core` | [openembedded/openembedded-core](https://github.com/openembedded/openembedded-core) | Core Yocto/OE recipes |
| `meta-openembedded` | [openembedded/meta-openembedded](https://github.com/openembedded/meta-openembedded) | Additional OE layers (meta-oe, meta-python, meta-networking) |

## Setup

### Adding the layer to your NXP BSP build

1. Clone meta-edgefirst into your BSP sources directory:

```bash
cd sources/
git clone https://github.com/EdgeFirstAI/meta-edgefirst.git
```

2. Add the layer to your `bblayers.conf`:

```bash
bitbake-layers add-layer sources/meta-edgefirst
```

Or manually add to `conf/bblayers.conf`:

```
BBLAYERS += "${BSPDIR}/sources/meta-edgefirst"
```

3. Verify the layer is recognized:

```bash
bitbake-layers show-layers
```

### Adding EdgeFirst packages to your image

Add a packagegroup to your image recipe or `local.conf`:

```
# Full perception stack
IMAGE_INSTALL:append = " packagegroup-edgefirst-perception"

# GStreamer ML inference pipelines
IMAGE_INSTALL:append = " packagegroup-edgefirst-gstreamer"
```

Or install individual packages:

```
IMAGE_INSTALL:append = " edgefirst-camera edgefirst-schemas zenohd"
```

## Development Roadmap

meta-edgefirst is being developed incrementally as EdgeFirst repositories are published as
open source. The layer will be built out in phases:

**Phase 1 — Foundation** (current)
- Layer structure, configuration, packagegroup skeletons
- README documentation

**Phase 2 — Supporting Infrastructure**
- zenohd, libzenohc, python3-zenoh recipes
- videostream recipe
- edgefirst-schemas recipe (C library + Python wheel)

**Phase 3 — Perception Services**
- edgefirst-camera, edgefirst-imu, edgefirst-navsat
- edgefirst-radarpub, edgefirst-lidarpub
- edgefirst-publisher, edgefirst-recorder, edgefirst-replay
- edgefirst-webui
- systemd target integration

**Phase 4 — ML/GStreamer Pipeline**
- NNStreamer EdgeFirst fork (dmabuf, TFLite-VX CameraAdaptor, Ara-2 sub-filter)
- gst-edgefirst plugins
- tim-vx and tflite-vx-delegate bbappends

## License

This layer is licensed under the [Apache License 2.0](LICENSE).

Individual recipe packages may have their own licenses — see each recipe's `LICENSE` variable
for details.
