# Mission: Systems & Embedded Software Engineering (Junior Android to Senior Systems Architect)

## Why
Transition from self-taught junior Android application development to an AI-resilient Senior Systems & Automotive OS Engineer by 2027, commanding top-of-market compensation ($185k–$320k) and bridging the high-leverage gap between physical silicon hardware and Android user screens.

## Success looks like
- Build and debug bare-metal C firmware on ARM Cortex-M/RISC-V with custom linker scripts, vector tables, and DMA controllers.
- Architect real-time multi-threaded firmware on FreeRTOS and Zephyr RTOS with zero priority inversion and deterministic deadlines.
- Write production Linux kernel character device drivers (`ioctl`, waitqueues, coherent DMA, sysfs).
- Develop modern C++20 and Rust AIDL Hardware Abstraction Layers (HALs) integrated with Android's `vndservicemanager` and SELinux.
- Implement Android Automotive OS (AAOS) Vehicle HAL (VHAL) properties bound to CAN-bus frames.
- Ship the resume-defining Grand Capstone: *Heterogeneous Automotive Cockpit Gateway & Telematics Controller* (MCU Firmware + Linux BSP + Rust AIDL HAL + AAOS CarService).

## Constraints
- **Current Baseline:** Self-taught junior Android developer (Kotlin/Java); zero prior experience with C, C++, Rust, assembly, or computer architecture.
- **Working Memory Budget:** Lessons must be tightly scoped, modular, and provide rapid tangible wins within the Zone of Proximal Development (ZPD).
- **Hardware Budget:** $0 upfront (100% free emulation via QEMU on macOS); optional $25 hardware kit later (RP2040 / STM32 + USB Logic Analyzer).

## Out of scope
- Legacy 8-bit microcontrollers (Arduino AVR, 8051, PIC).
- Obsolete pre-C++11 idioms (raw pointers, manual `malloc`, deep class inheritance).
- Deprecated Android HIDL (Android 13+ standardizes strictly on AIDL HALs).
- Generic LeetCode algorithmic puzzles (focus strictly on systems memory, concurrency, cache, and bus protocols).
