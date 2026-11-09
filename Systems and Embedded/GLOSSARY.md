# Systems & Embedded Engineering Glossary

Canonical definitions for low-level computer architecture, operating systems, and Android platform engineering.

## Terms

### Hardware & Memory Architecture

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

---

### Operating Systems & Concurrency

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

---

### Linux Kernel & Android Platform

**Character Device**:
A Linux kernel driver interface exposing hardware as a sequential stream of bytes accessible via standard file operations (`read`, `write`, `ioctl`).
_Avoid_: Byte driver, raw device

**Flattened Device Tree (FDT)**:
A hardware description data structure passed from the bootloader to the Linux kernel detailing memory base addresses, clocks, IRQs, and pin multiplexing.
_Avoid_: Hardware config, board file

**Binder IPC**:
Android's primary inter-process communication mechanism, using a Linux kernel driver (`/dev/binder`) and single-copy `mmap` transactions between processes.
_Avoid_: Android RPC, intents engine

**AIDL HAL (Hardware Abstraction Layer)**:
A standardized Android interface defined in Android Interface Definition Language (AIDL) that decouples system framework services from vendor-specific kernel drivers.
_Avoid_: HIDL, native driver

**Vehicle HAL (VHAL)**:
The Android Automotive OS (AAOS) interface mediating communication between Android's `CarService` and the vehicle's physical powertrain/body networks.
_Avoid_: Car HAL, auto driver

---

### Modern Systems Rust & Automotive Engineering

**Embassy**:
A modern async embedded framework for Rust that provides cooperative multitasking on microcontrollers with zero dynamic heap allocation.
_Avoid_: Embedded Tokio, micro-async

**CAN-FD (Flexible Data-Rate)**:
An enhanced in-vehicle serial bus protocol supporting variable bitrates up to 8 Mbps in the data phase and payload sizes up to 64 bytes per frame.
_Avoid_: Fast CAN, high-speed bus

**CAN Database (.DBC)**:
A standardized text file defining the bit-level structure, endianness, scaling factor, and physical units of telemetry signals packed inside raw CAN frames.
_Avoid_: CAN schema, protocol spec

**CarPropertyManager**:
The client-side Android Automotive OS framework manager that applications use to read, set, and subscribe to vehicle telemetry properties.
_Avoid_: VehicleManager, car SDK

**ISO 26262**:
The international functional safety standard for road vehicle electrical and electronic systems, defining risk classification and fault mitigation from QM through ASIL-D.
_Avoid_: Automotive safety rule, ISO code

**Automotive Safety Integrity Level (ASIL)**:
A risk classification scheme (QM, ASIL-A, B, C, D) defined by ISO 26262 based on Severity, Exposure, and Controllability to specify requisite safety requirements.
_Avoid_: Risk level, safety tier

**Freedom From Interference (FFI)**:
The architectural guarantee that a software component of lower safety criticality (e.g. QM Android) cannot cause failures in a higher safety criticality component (e.g. ASIL-D braking).
_Avoid_: Software isolation, process sandbox

**VirtIO**:
A standardized virtualization standard enabling guest operating systems (such as Android) to access shared host hardware devices via zero-copy ring buffers.
_Avoid_: Virtual driver, VM bridge
