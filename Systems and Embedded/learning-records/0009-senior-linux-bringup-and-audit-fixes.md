# Audit Fixes, Senior Linux & Hardware Bring-Up Expansion, and Labs Upgrade

Audited all 60 existing lessons, 7 reference sheets, `assets/style.css`, `index.html`, and `./labs/` prior to starting Study Session 1, keeping **Non-AOSP Systems Linux before AOSP** and marking **Track 08 (AAOS & Safety)** as an **Optional Automotive Specialization**.

## Bugs & Inconsistencies Fixed
- **`assets/style.css` & `index.html`:** Defined missing CSS variables (`--bg-surface`, `--border-accent`, `--font-mono`, `--font-sans`, `--text-secondary`) so lessons `0026`–`0036` and `0004a`–`0004d` render borders, surfaces, muted text, and monospace code cleanly.
- **`lessons/0023-modern-aidl-hal-development.html` & `index.html`:** Fixed Binder domain accuracy (`@VintfStability` AIDL HALs register with `servicemanager` on `/dev/binder`, while `vndservicemanager` on `/dev/vndbinder` is vendor-to-vendor only) and replaced the trivial `status_code == STATUS_OK` workbench with an NDK `VehicleSensor::getSpeedKmh` null-guard & `ScopedAStatus` exercise.
- **`lessons/0004`, `0017`, `0024`:** Replaced unsafe `sprintf` with bounds-checked `snprintf(..., size_t)` to match Lesson `0004b`.
- **`lessons/0012`, `0018`, `0036`:** Upgraded trivial workbenches (`0012` Zephyr `GPIO_DT_SPEC_GET` driver, `0018` full `.dts` SPI sensor overlay node, `0036` strict CAS tail-update validator).
- **Track Labels & Primary Sources (`0021`–`0036`):** Synchronized `.track-badge` and `.nav-breadcrumb` labels across Tracks 05, 07, 08, and 09, and standardized `.callout-primary-source` boxes across `0026`–`0036`.

## What Was Added
1. **5 New Senior Systems & Hardware Bring-Up Lessons (`60` &rarr; `65` total):**
   - `0008a-hardware-bringup-schematics-swd-logic-analyzer.html` (Track 01: power/brownout, `RCC->CSR`, SWD/OpenOCD, I2C pull-ups, SPI logic analyzer captures)
   - `0020f-linux-virtual-memory-storage-powerloss.html` (Track 04: MMU page tables, no-swap `cgroups v2`, SquashFS/OverlayFS, atomic `fsync` &rarr; `rename` &rarr; `fsync`)
   - `0020g-linux-networking-sockets-socketcan.html` (Track 04: `SO_REUSEADDR`, non-blocking `EAGAIN`/EOF, `SocketCAN`, `ip`/`ss`/`tcpdump`/`candump`)
   - `0020h-linux-signals-capabilities-udev.html` (Track 04: `signalfd` in `epoll`, `CapabilityBoundingSet=`, `udev` device symlinks)
   - `0020i-linux-preempt-rt-userspace-buses.html` (Track 04: `PREEMPT_RT`, `mlockall`, `SCHED_FIFO`, `isolcpus`, `libgpiod` v2, `spidev`, `i2c-dev`)
2. **Track 05 Portfolio Milestone Project Card:** Added Resume Artifact #5 (*Async Embassy Sensor Node & Linux Kernel Rust Driver*) in `index.html`, renumbering Cloud (#6) and AOSP (#7).
3. **2 New Runnable Local Labs (`./labs/`):**
   - `labs/06_linux_posix_ipc`: C11 POSIX `shm_open`/`mmap`, non-blocking `AF_UNIX` `EAGAIN`/`poll()`, and `sigaction` under ASan/UBSan.
   - `labs/07_arm_cortex_m_boot`: ARM Cortex-M4 `cortex_m4.ld` linker script, vector table/`Reset_Handler` `startup.c`, and host-verified `.data`/`.bss` `crt0` layout test.
