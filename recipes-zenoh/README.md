# recipes-zenoh

Yocto recipes for [EdgeFirst Perception for Zenoh](https://github.com/EdgeFirstAI/.github/blob/main/profile/zenoh.md) — modular microservices that communicate over Eclipse Zenoh using CDR-encoded messages. Each service handles a single responsibility and can be composed into perception pipelines through configuration.

See the [EdgeFirst organization](https://github.com/EdgeFirstAI) for the full project overview.

## Infrastructure

| Recipe | Repo | Description |
|--------|------|-------------|
| zenoh-c | [eclipse-zenoh/zenoh-c](https://github.com/eclipse-zenoh/zenoh-c) | Zenoh C API bindings (libzenohc) |
| zenohd | [eclipse-zenoh/zenoh](https://github.com/eclipse-zenoh/zenoh) | Zenoh router daemon + storage/REST plugins |
| python3-zenoh | [eclipse-zenoh/zenoh-python](https://github.com/eclipse-zenoh/zenoh-python) | Zenoh Python bindings |
| edgefirst-schemas | [schemas](https://github.com/EdgeFirstAI/schemas) | CDR message definitions with C and Python serializers |

## Sensor Services

| Recipe | Repo | Description |
|--------|------|-------------|
| edgefirst-camera | [camera](https://github.com/EdgeFirstAI/camera) | V4L2 sensor capture with DMA buffer and ISP support |
| edgefirst-model | [model](https://github.com/EdgeFirstAI/model) | NPU-accelerated neural network inference |
| edgefirst-fusion | [fusion](https://github.com/EdgeFirstAI/fusion) | Multi-sensor correlation (camera, radar, LiDAR) |
| edgefirst-imu | [imu](https://github.com/EdgeFirstAI/imu) | Inertial measurement unit (I2C/SPI) |
| edgefirst-navsat | [navsat](https://github.com/EdgeFirstAI/navsat) | GNSS/GPS geolocation |
| edgefirst-radarpub | [radarpub](https://github.com/EdgeFirstAI/radarpub) | Automotive radar target lists and raw cube data |
| edgefirst-lidarpub | [lidarpub](https://github.com/EdgeFirstAI/lidarpub) | LiDAR point cloud capture and distribution |
| edgefirst-recorder | [recorder](https://github.com/EdgeFirstAI/recorder) | MCAP synchronized multi-sensor recording |
| edgefirst-replay | [replay](https://github.com/EdgeFirstAI/replay) | Offline MCAP session playback |
| edgefirst-websrv | [websrv](https://github.com/EdgeFirstAI/websrv) | HTTPS server with Zenoh-to-WebSocket bridge |
| edgefirst-webui | [webui](https://github.com/EdgeFirstAI/webui) | Browser-based visualization and configuration |
