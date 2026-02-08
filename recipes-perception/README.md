# recipes-perception

Zenoh-based sensor services that publish, record, and replay perception data. Each service
runs as a systemd unit and uses EdgeFirst Schemas for message serialization.

- **edgefirst-camera** — Camera capture service (V4L2/ISP)
- **edgefirst-imu** — IMU accelerometer/gyroscope service
- **edgefirst-navsat** — GNSS/GPS navigation satellite receiver
- **edgefirst-radarpub** — Automotive radar data publisher
- **edgefirst-lidarpub** — LiDAR point cloud publisher
- **edgefirst-schemas** — Shared schema library (C + Python)
- **edgefirst-publisher** — Generic Zenoh topic publisher/bridge
- **edgefirst-recorder** — Zenoh session recorder
- **edgefirst-replay** — Zenoh session replay
- **edgefirst-webui** — Web dashboard for monitoring and configuration
