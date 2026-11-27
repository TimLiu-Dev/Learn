# Systems & Embedded Engineering Glossary

Canonical definitions for low-level computer architecture, bench electronics, RTOS, Embedded Linux, and AOSP 17 platform engineering.

## Terms

### Hardware, Bench & Memory Architecture

**Bit**:
The fundamental unit of binary information in silicon, physically represented as a high or low voltage state (1 or 0).
_Avoid_: Flag, digit

**Byte**:
A contiguous sequence of 8 bits, representing the smallest directly addressable unit of physical memory in modern CPU architectures.
_Avoid_: Character, word

**Two's Complement**:
The standard mathematical binary encoding for signed integers where the most significant bit represents negative weight ($-2^{N-1}$).
_Avoid_: Signed binary, negative bits

**Little-Endian**:
A byte-ordering scheme where the least significant byte of a multi-byte word is stored at the lowest physical memory address.
_Avoid_: Reverse bytes, bottom-up ordering

**Memory-Mapped I/O (MMIO)**:
A hardware architecture where peripheral control and status registers share the exact same address bus and address space as physical RAM.
_Avoid_: Port I/O, hardware mapping

**Volatile**:
A C/C++ type qualifier instructing the compiler that a memory location can mutate spontaneously, forcing an explicit bus transaction on every read or write.
_Avoid_: Dynamic variable, thread-safe marker

**Direct Memory Access (DMA)**:
A dedicated hardware subsystem that transfers blocks of data between peripherals and memory over the system bus without CPU core intervention.
_Avoid_: Hardware copy, auto-transfer

**Open-Drain Pull-Up**:
A circuit topology (used by I2C) where devices only pull a shared line to GND and rely on an external resistor to VCC to return the line to logic HIGH.
_Avoid_: Active push-pull driver, internal bias wire

**Configurable Fault Status Register (CFSR)**:
An ARM Cortex-M System Control Block register (splitting into MMFSR, BFSR, and UFSR) that latches the exact hardware cause of a HardFault or MemManage/Bus/Usage fault.
_Avoid_: Generic crash code, panic string

**Differential Pair & Bus Termination**:
A balanced two-wire signaling topology (`CAN_H`/`CAN_L` or `RS485 A`/`B`) whose voltage difference rejects common-mode noise, terminated at both physical bus ends with 120Ω resistors (60Ω total parallel load).
_Avoid_: Single-ended wire, ground-referenced trace

**Dead-Time Insertion**:
A hardware timer delay injected between turning off one power transistor and turning on its complementary partner in an H-bridge to prevent destructive shoot-through current.
_Avoid_: Software sleep gap, PWM duty cap

---

### Fieldbuses, Operating Systems & Concurrency

**Interrupt Service Routine (ISR)**:
A hardware-invoked software callback executed by the CPU in response to an asynchronous electrical signal from a peripheral.
_Avoid_: Interrupt handler thread, hardware listener

**Context Switch**:
The low-level operation of saving the current CPU register state of a thread and restoring the saved register state of another thread.
_Avoid_: Thread swap, task jump

**Task Control Block (TCB)**:
A kernel data structure storing the execution state, stack pointer, priority, and identifiers of an RTOS thread.
_Avoid_: Process descriptor, thread state

**Priority Inversion**:
A real-time concurrency hazard where a high-priority task is blocked waiting for a resource held by a low-priority task, which is itself preempted by a medium task.
_Avoid_: Starvation, priority deadlock

**Modbus RTU**:
A deterministic half-duplex serial industrial protocol over RS485 using binary framing, 3.5-character inter-frame silence delimiters, and a 16-bit CRC checksum.
_Avoid_: ASCII JSON serial, full-duplex stream

**Quadrature Encoder**:
An incremental rotary sensor outputting two 90°-phase-shifted square waves (A and B) decoded by a hardware timer to measure both pulse count and rotation direction.
_Avoid_: Analog potentiometer, single-pulse tachometer

**TrustZone-M & TF-M (Trusted Firmware-M)**:
Arm Cortex-M33 hardware security partitioning separating a Secure processing environment (keys, PSA Crypto, ITS) from a Non-Secure RTOS application.
_Avoid_: Software sandbox, user-space permissions

**Virtual eFuses**:
An emulated RAM/flash OTP configuration mode (used during ESP32-S3 development) for testing Secure Boot v2 and flash encryption without permanently burning irreversible silicon fuses.
_Avoid_: Production fuse burn, soft reset flags

---

### Linux Kernel & AOSP 17 Platform

**Character Device**:
A Linux kernel driver interface exposing hardware as a sequential stream of bytes accessible via standard file operations (`read`, `write`, `ioctl`, `poll`).
_Avoid_: Byte driver, raw device

**Flattened Device Tree (FDT)**:
A hardware description data structure passed from the bootloader to the Linux kernel detailing memory base addresses, clocks, IRQs, and pin multiplexing.
_Avoid_: Hardware config, board file

**Unix Domain Socket (`AF_UNIX`)**:
A local kernel-mediated IPC socket operating over filesystem or abstract namespaces without network stack overhead, supporting stream, datagram, and file-descriptor passing.
_Avoid_: Loopback TCP port, Binder transaction

**Binder IPC**:
Android's primary inter-process communication mechanism, using a Linux kernel driver (`/dev/binder`) and single-copy `mmap` transactions between processes.
_Avoid_: Android RPC, intents engine

**AIDL HAL (Hardware Abstraction Layer)**:
A standardized Android interface defined in Android Interface Definition Language (`@VintfStability`) that decouples framework services from vendor-specific hardware daemons.
_Avoid_: HIDL, native driver

**Cuttlefish & VTS (Vendor Test Suite)**:
Google's KVM-accelerated virtual Android device platform for x86-64 Linux hosts and its corresponding compliance test suite validating vendor AIDL HAL contracts.
_Avoid_: Phone app emulator, UI unit test

---

### Modern Systems Rust & Tooling

**Embassy**:
A modern async embedded framework for Rust that provides cooperative multitasking on microcontrollers with zero dynamic heap allocation.
_Avoid_: Embedded Tokio, micro-async

**`probe-rs` & `defmt`**:
An open-source Rust embedded debugging toolkit and deferred-formatting RTT logger that shifts string formatting off the MCU onto the host PC.
_Avoid_: Printf over semi-hosting, string heap logger

**CAN-FD (Flexible Data-Rate)**:
An enhanced in-vehicle serial bus protocol supporting variable bitrates up to 8 Mbps in the data phase and payload sizes up to 64 bytes per frame.
_Avoid_: Fast CAN, high-speed bus

**CAN Database (.DBC)**:
A standardized text file defining the bit-level structure, endianness, scaling factor, and physical units of telemetry signals packed inside raw CAN frames.
_Avoid_: CAN schema, protocol spec

**epoll**:
A scalable Linux kernel I/O event notification mechanism enabling a single-threaded daemon to monitor thousands of file descriptors (sockets, pipes, `/dev` nodes) in O(1) per ready event.
_Avoid_: Busy-wait polling, select loop

**A/B OTA (Over-The-Air) Update**:
A fail-safe firmware or rootfs update architecture maintaining two redundant flash/disk slots (Active and Inactive) so a failed boot automatically rolls back via hardware watchdog.
_Avoid_: In-place overwrite, live patching

**Hardware-in-the-Loop (HIL)**:
An automated CI testing architecture where compiled firmware or Linux images execute against real target boards or QEMU emulators driven by scripted bus stimuli.
_Avoid_: Manual bench click-testing, unit-only mocks
