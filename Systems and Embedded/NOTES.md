# Notes & User Preferences

## Learner Profile
- **Current Role:** Junior Android Developer (self-taught, ~2 years experience).
- **Current Strengths:** Kotlin, Android application architecture, Jetpack Compose, high-level asynchronous programming (Coroutines).
- **Goal:** Reach a Senior Systems Engineer / Embedded Linux & Automotive OS Engineer level by 2027 ($185k–$320k band) with an AI-resilient, top-to-bottom stack skill set.
- **Initial Knowledge Gap:** Has not studied C, C++, Rust, Go, assembly, memory management, linkers, hardware registers, kernel drivers, RTOS, Yocto/OTA, or embedded cloud/DevOps.

## Curriculum Decisions & Preferences
- **Language Progression:** Strictly `C11 -> Modern C++20 -> Rust` (plus `Go/Rust` for embedded cloud ingestion services—intentionally skipping Java Spring Boot enterprise bloat).
- **Non-AOSP Before AOSP:** Master standalone Embedded Linux systems (POSIX IPC, `systemd`, Yocto/Buildroot, `perf`/`ftrace`/`eBPF`, Secure Boot, OTA) and Systems Rust *before* specializing into Android NDK/JNI, AOSP Binder/AIDL, and AAOS.
- **Cloud & DevOps Scope ("Enough for Embedded"):** Keep focused on edge-relevant protocols and automation: Protobuf/CBOR, MQTT v5 + mTLS X.509, Go/Rust telemetry ingestion, Dockerized cross-compiles + CI, MCUboot/RAUC A/B OTA, and mocking/QEMU HIL testing.
- **Real Local Compiler Labs:** Pair HTML lessons (for theory + retrieval quizzes) with runnable local `./labs/` exercises verified by `clang`, `clang++`, `rustc`, `cargo`, `go`, and `make`.

## Portfolio Milestone Architecture (2027 Resume Pillars)
- **1. C & Bring-Up Project:** Bare-Metal DMA-Driven Protocol Sniffer (STM32/RP2040/QEMU) + SWD/Logic Analyzer verification.
- **2. RTOS Project:** Deterministic Multi-Sensor Controller (Zephyr RTOS).
- **3. C++20 Project:** High-Throughput Zero-Copy Diagnostic & Telemetry Broker in Modern C++20 (`constexpr`, `std::span`, SPSC lock-free atomic circular queue, CRTP).
- **4. Non-AOSP Linux Systems Project:** Yocto/POSIX Edge Gateway Daemon (`epoll`, `signalfd`, `shm_open`, SocketCAN, `PREEMPT_RT`, crash-safe persistence, `systemd` + `udev`/Capabilities, `bpftrace` tuned).
- **5. Systems Rust Project:** Async Embassy `no_std` Sensor Node + Linux 6.x+ Kernel Rust Character Device Driver.
- **6. Embedded Cloud & DevOps Project:** Protobuf/mTLS MQTT Edge Agent + Go/Rust Cloud Telemetry Ingestor + Docker/QEMU CI + Signed A/B OTA.
- **7. Android AOSP & Rust HAL Project:** Rust SocketCAN/Device-Node to AIDL HAL service + Kotlin NDK/Compose bridge.
- *(Optional Specialization — Track 08):* AAOS VHAL, ISO 26262 Functional Safety & Cockpit Hypervisors (skip unless targeting automotive OEM/Tier-1 roles).
- **8. Grand End-to-End Capstone:** *Silicon-to-Cloud Heterogeneous Gateway* uniting MCU Firmware + Linux Kernel Driver + Yocto/OTA + Rust Service/HAL + Kotlin UI + Go Cloud Ingestor + CI.

## Instructional Design Guidelines
- **Zone of Proximal Development (ZPD):** Break complex topics into modular, step-by-step lessons. Never assume familiarity with pointers, manual memory allocation, or physical silicon bus operations.
- **Desirable Difficulty:** Build storage strength via active retrieval practice, live in-browser code verification, local compiler labs, and interactive quizzes with immediate feedback.
- **Quiz Rule:** All multiple-choice options must maintain identical word counts and balanced character lengths so formatting provides no unintended cues.
