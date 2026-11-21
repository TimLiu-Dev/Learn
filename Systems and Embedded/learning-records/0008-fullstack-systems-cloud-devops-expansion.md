# Full-Stack Systems, Non-AOSP Linux, Cloud/DevOps & Local Compiler Labs Expansion

Audited the 48-lesson curriculum ahead of starting Study Session 1 and upgraded the roadmap to cover the complete top-to-bottom stack—both **with and without AOSP**—plus runnable local compiler labs on macOS.

## Fixes Applied to Existing Workspace
- **`lessons/0035-capstone-rust-aidl-vhal-integration.html`:** Fixed the `vhal_parser.rs` workbench mismatch where a `.rs` lab previously contained C starter code and C solution checks; replaced with authentic Rust (`parse_vhal_speed_packet(packet: &[u8]) -> Option<f32>`).
- **Removed `lesson1.html`:** Deleted unlinked legacy draft file at repository root.
- **Reordered Progression (`0020`–`0036`):** Separated pure Non-AOSP Linux Systems (`0020`–`0020d`) and Systems Rust (`0024a`–`0027`) + Embedded Cloud/DevOps (`0028a`–`0028f`) *before* Android NDK/AOSP (`0020e`, `0021`–`0024`, `0028`) and AAOS (`0029`–`0032`), ending in the upgraded Silicon-to-Cloud Capstone (`0033`–`0036`).

## What Was Added
1. **12 New Interactive Lessons (`48` &rarr; `60` total):**
   - **Track 04 Non-AOSP Linux Systems (`0020a`–`0020d`):** POSIX `epoll`/`shm_open`/`systemd`, Yocto Project & Buildroot, `perf`/`ftrace`/`bpftrace`, and Verified Boot/`dm-verity`/TPM2 + Milestone #4.
   - **Track 06 Embedded Cloud & DevOps (`0028a`–`0028f`):** Protobuf (`nanopb`/`prost`)/CBOR, MQTT v5 + mTLS + Device Shadows, Go & Rust Telemetry Ingestion, Dockerized CI toolchains, MCUboot/RAUC A/B OTA, and HAL Mocking + QEMU/HIL testing + Milestone #5. *(Intentionally prioritized Go/Rust over Spring Boot for embedded cloud ingestion.)*
   - **Track 07 Android NDK/JNI Bridge (`0020e`):** Zero-copy `DirectByteBuffer` & `JavaVM*` thread attachment bridging Kotlin to C/C++/Rust `.so` libraries before AOSP `0021`–`0024` + `0028`.
   - **Track 09 Capstone Part 4 (`0035a`):** Closes the Silicon-to-Cloud loop with idempotent Go cloud ingestion, Protobuf/MQTT forwarding, and CI/OTA automation.
2. **2 Print-Ready Reference Cheat Sheets (`0006`, `0007`):**
   - `reference/0006-linux-systems-yocto-ota-cheat-sheet.html`
   - `reference/0007-embedded-cloud-devops-go-cheat-sheet.html`
3. **Runnable Local Compiler & Simulator Labs (`./labs/`):**
   - `labs/01_c_bridge` (C11 + ASan/UBSan), `labs/02_cpp20_telemetry` (C++20 lock-free SPSC), `labs/03_rust_gateway` (`no_std` Cargo tests), `labs/04_go_cloud_ingestor` (`go test -race`), and `labs/05_capstone_e2e` (live C11 &rarr; Rust &rarr; Go pipeline).

## Verification
- Verified all **60 lessons**, **7 reference sheets**, and `index.html`: zero broken links, unbroken 60-lesson forward navigation chain, exact equal-word-count quiz parity across all new/updated lessons, and starter-fail vs solution-pass execution in macOS JavaScriptCore (`jsc`).
- Compiled and executed `make -C labs test` with zero warnings across `clang` (C11 ASan/UBSan), `clang++` (C++20), `cargo test`, `go test -race`, and the live E2E pipeline.
