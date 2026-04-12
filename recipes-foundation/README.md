# recipes-foundation

Yocto recipes for [EdgeFirst Perception Foundation](https://github.com/EdgeFirstAI/.github/blob/main/profile/foundation.md) — the low-level building blocks that every other layer depends on: hardware abstraction, optimized video I/O, and accelerated neural network inference on embedded SoCs.

See the [EdgeFirst organization](https://github.com/EdgeFirstAI) for the full project overview.

## EdgeFirst Libraries

| Recipe | Repo | Description |
|--------|------|-------------|
| edgefirst-hal | [hal](https://github.com/EdgeFirstAI/hal) | Hardware abstraction layer — preprocessing, post-processing (quantized NMS), model metadata, DMA-BUF tensor management. C library + Python bindings. |
| edgefirst-tflite | [tflite-rs](https://github.com/EdgeFirstAI/tflite-rs) | TensorFlow Lite bindings with NPU acceleration support. Python module. |
| videostream | [videostream](https://github.com/EdgeFirstAI/videostream) | V4L2/ISP video capture library with DMA-BUF zero-copy. GStreamer plugin, CLI tools, and Python bindings. |

## NXP i.MX NPU Extensions (bbappends)

| Recipe | Fork | Description |
|--------|------|-------------|
| tim-vx | [tim-vx-imx](https://github.com/EdgeFirstAI/tim-vx-imx) | Tensor DMA-BUF API for VeriSilicon NPU (i.MX 8M Plus) |
| tflite-vx-delegate | [tflite-vx-delegate-imx](https://github.com/EdgeFirstAI/tflite-vx-delegate-imx) | TFLite VX delegate — DMA-BUF zero-copy and CameraAdaptor graph injection |
| tensorflow-lite-neutron-delegate | [tflite-neutron-delegate](https://github.com/EdgeFirstAI/tflite-neutron-delegate) | Neutron NPU delegate with DMA-BUF support (i.MX 95) |
| imx-gst1.0-plugin | [imx-gst1.0-plugin](https://github.com/EdgeFirstAI/imx-gst1.0-plugin) | NXP i.MX GStreamer plugin fork — DMA-BUF extensions for g2d videoconvert/compositor |
