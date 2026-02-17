# recipes-perception

Zenoh-based sensor services that publish, record, and replay perception data. Each service
runs as a systemd unit and uses EdgeFirst Schemas for message serialization.

- **edgefirst-camera** — Camera capture service (V4L2/ISP)
- **edgefirst-model** — Model inference service (NPU-accelerated detection/classification)
- **edgefirst-fusion** — Sensor fusion service (radar-camera BEV fusion)
- **edgefirst-imu** — IMU accelerometer/gyroscope service
- **edgefirst-navsat** — GNSS/GPS navigation satellite receiver
- **edgefirst-radarpub** — Automotive radar data publisher
- **edgefirst-lidarpub** — LiDAR point cloud publisher
- **edgefirst-recorder** — Zenoh session recorder
- **edgefirst-replay** — Zenoh session replay
- **edgefirst-hal** — Hardware abstraction layer (C library + Python bindings)
- **edgefirst-schemas** — Shared schema library (C + Python)
- **edgefirst-websrv** — Web UI backend server
- **edgefirst-webui** — Web dashboard for monitoring and configuration
