# Mission: Systems & Embedded Software Engineering (Junior Android to Senior Systems Architect)

## Why
Transition from self-taught junior Android application development to an AI-resilient Senior Systems, Embedded Linux & Automotive OS Engineer by 2027, commanding top-of-market compensation ($185k–$320k) and owning the complete silicon-to-cloud stack—both with and without AOSP.

## Success looks like
- Build, bring up, and debug bare-metal C firmware on ARM Cortex-M/RISC-V (custom linker scripts, vector tables, DMA, SWD/OpenOCD, logic analyzer waveforms).
- Architect real-time multi-threaded firmware on FreeRTOS and Zephyr RTOS with zero priority inversion and deterministic deadlines.
- Write Modern C++20 zero-overhead drivers, lock-free SPSC atomic queues, and production Linux kernel character device drivers (`ioctl`, waitqueues, coherent DMA, sysfs).
- Engineer **non-AOSP Embedded Linux systems**: POSIX `epoll`/IPC/`signalfd` daemons, non-blocking sockets & SocketCAN, MMU/storage/power-loss safety (`fsync`+`rename`), `udev`/Capabilities, `PREEMPT_RT` + `libgpiod`, Yocto/Buildroot images, `perf`/`ftrace`/`eBPF` profiling, and cryptographically signed A/B OTA updates.
- Develop Systems Rust (`no_std`, Embassy async, Linux kernel modules) and **Embedded-to-Cloud** pipelines (Protobuf/CBOR, MQTT v5 + mTLS, Go/Rust telemetry ingestion, Docker CI + QEMU/HIL testing).
- Bridge Android apps and platform via **NDK/JNI**, Binder IPC, Modern C++20/Rust **AIDL HALs**, and SELinux (plus optional Automotive Specialization: **AAOS VHAL**, CAN-FD, ISO 26262, Hypervisors).
- Ship the Grand Capstone: *Silicon-to-Cloud Heterogeneous Gateway* (Zephyr MCU + Linux SPI Driver + Yocto/OTA + Rust AIDL/HAL + Compose UI + Go/Rust Cloud Telemetry Ingestor + Docker CI).

## Constraints
- **Current Baseline:** Self-taught junior Android developer (Kotlin/Java); zero prior experience with C, C++, Rust, Go, Linux internals, or computer architecture.
- **Working Memory Budget:** Lessons must be tightly scoped, modular, and provide rapid tangible wins within the Zone of Proximal Development (ZPD).
- **Hardware & Cloud Budget:** $0 upfront (local macOS `clang`/`c++`/`rustc`/`cargo`/`go` + QEMU emulation); optional $25 hardware kit later (RP2040 / STM32 + USB Logic Analyzer).

## Out of scope
- Legacy 8-bit microcontrollers (Arduino AVR, 8051, PIC).
- Obsolete pre-C++11 idioms (raw pointers, manual `malloc`, deep class inheritance).
- Deprecated Android HIDL (Android 13+ standardizes strictly on AIDL HALs).
- Generic Android app UI tutorials (already mastered) and heavy enterprise Java/Spring Boot boilerplate (prefer Go/Rust for embedded cloud services).
- Generic LeetCode algorithmic puzzles (focus strictly on systems memory, concurrency, cache, bus protocols, and distributed device telemetry).
