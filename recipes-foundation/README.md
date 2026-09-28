# recipes-foundation

Yocto recipes for [EdgeFirst Perception Foundation](https://github.com/EdgeFirstAI/.github/blob/main/profile/foundation.md) — the low-level building blocks that every other layer depends on: hardware abstraction, optimized video I/O, and accelerated neural network inference on embedded SoCs.

See the [EdgeFirst organization](https://github.com/EdgeFirstAI) for the full project overview.

## EdgeFirst Libraries

| Recipe | Repo | Description |
|--------|------|-------------|
| edgefirst-hal | [hal](https://github.com/EdgeFirstAI/hal) | Hardware abstraction layer — preprocessing, post-processing (quantized NMS), model metadata, DMA-BUF tensor management. C library + Python bindings. |
| edgefirst-ara2 | [ara2-rs](https://github.com/EdgeFirstAI/ara2-rs) | Ara-2 NPU Python bindings. Works with either `imx-nxp-ara2` runtime packaging (NXP's meta-imx-ml or the Kinara SDK from meta-kinara). |
| edgefirst-tflite | [tflite-rs](https://github.com/EdgeFirstAI/tflite-rs) | TensorFlow Lite bindings with NPU acceleration support. Python module. |
| edgefirst-modelzoo | [Hugging Face Model Zoo](https://huggingface.co/EdgeFirst) | Pre-installed YOLOv8n det/seg models. INT8 smart TFLite in subpackages `edgefirst-modelzoo-yolov8n-det` and `-yolov8n-seg` (`mx8mp` gets generic `.tflite`, `mx95` gets `.imx95.tflite`); INT16 Ara-2 `.dvm` in `-yolov8n-det-ara2` and `-yolov8n-seg-ara2`, the same on both. `edgefirst-modelzoo` installs all four. Installs under `/usr/share/edgefirst/modelzoo/`. |
| videostream | [videostream](https://github.com/EdgeFirstAI/videostream) | V4L2/ISP video capture library with DMA-BUF zero-copy. GStreamer plugin, CLI tools, and Python bindings. |

## NXP i.MX NPU Extensions (bbappends)

| Recipe | Fork | Description |
|--------|------|-------------|
| tim-vx | [tim-vx-imx](https://github.com/EdgeFirstAI/tim-vx-imx) | Tensor DMA-BUF API for VeriSilicon NPU (i.MX 8M Plus) |
| tflite-vx-delegate | [tflite-vx-delegate-imx](https://github.com/EdgeFirstAI/tflite-vx-delegate-imx) | TFLite VX delegate — DMA-BUF zero-copy and CameraAdaptor graph injection |
| tensorflow-lite-neutron-delegate | [tflite-neutron-delegate](https://github.com/EdgeFirstAI/tflite-neutron-delegate) | Neutron NPU delegate with DMA-BUF support (i.MX 95) |
| imx-gst1.0-plugin | [imx-gst1.0-plugin](https://github.com/EdgeFirstAI/imx-gst1.0-plugin) | NXP i.MX GStreamer plugin fork — DMA-BUF extensions for g2d videoconvert/compositor |
