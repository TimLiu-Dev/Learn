# Notes & User Preferences

## Learner Profile
- **Current Role:** Junior Android Developer (self-taught).
- **Current Strengths:** Kotlin, Android application architecture, Jetpack Compose, high-level asynchronous programming (Coroutines).
- **Goal:** Reach a Senior Systems Engineer / Automotive OS Engineer level by 2027 ($185k–$320k band) with an AI-resilient skill set.
- **Initial Knowledge Gap:** Has not studied C, C++, Rust, assembly, memory management, linkers, hardware registers, kernel drivers, or RTOS.

## Portfolio Milestone Architecture (2027 Resume Pillars)
- **1. C & C Project:** Bare-Metal DMA-Driven Protocol Sniffer (STM32/RP2040/QEMU) & Multi-Sensor Flight Controller (Zephyr RTOS).
- **2. C++ & C++ Project:** High-Throughput Zero-Copy Diagnostic & Telemetry Broker in Modern C++20 (`constexpr`, `std::span`, SPSC lock-free atomic circular queue, CRTP).
- **3. Rust & Rust Project:** High-Performance CAN-FD to Binder Telemetry Gateway in Rust (`no_std` / Embassy / SocketCAN / `binder_rs`).
- **4. Grand Capstone Project:** Heterogeneous Automotive Cockpit Gateway & Telematics Controller (Zephyr RTOS MCU + Linux SPI Kernel Driver + Rust AIDL HAL + AAOS CarService & Compose UI).

## Instructional Design Guidelines
- **Zone of Proximal Development (ZPD):** Break complex topics into modular, step-by-step lessons. Never assume familiarity with pointers, manual memory allocation, or physical silicon bus operations.
- **Desirable Difficulty:** Build storage strength via active retrieval practice, live in-browser code verification, and interactive quizzes with immediate feedback.
- **Quiz Rule:** All multiple-choice options must maintain identical word counts and balanced character lengths so formatting provides no unintended cues.
- **Hands-on Verification:** Provide runnable in-browser simulators, progressive hints, and revealable reference solutions for all coding exercises.
- **Tooling Strategy:** Emphasize free local virtual hardware (QEMU on macOS) first, graduating to low-cost physical hardware ($15 RP2040 / STM32 + logic analyzer) later in the curriculum.
