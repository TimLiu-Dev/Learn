# Hardware Lab & 2027 Roadmap Realignment

Realigned the entire curriculum with the purchased physical hardware lab (29 cart items / 33 units + Raspberry Pi 4 8GB + MacBook Pro M5 Max + ROG Flow Z13 Linux) and the 2027 Part A (Mid-Level Gate, Weeks 1–32) + Part B (Senior Gate, Phases 6–8) roadmap.

## What Was Added
1. **Prerequisites & Refresher Section (`0000a`, `0000b`):**
   - `0000a-bsee-hardware-and-bench-refresher.html`: BSEE bench electronics refresher (1.8V/3.3V/5V traps, open-drain I2C pull-ups, decoupling, 60Ω CAN termination, DMM & 10x scope probing).
   - `0000b-android-platform-and-unix-ipc-refresher.html`: Fitness-console Android platform refresher (`AF_UNIX` framing, `SystemService` vs Vendor HAL boundaries, AOSP IME, `adb`/`dumpsys`).
2. **Part A Physical Hardware, Fieldbus, RISC-V, SMP & AOSP 17 Lessons:**
   - `0008b-bare-metal-drivers-displays-imu-storage.html` (`SSD1306`, `LSM6DSV16X`, `EC11`, `BME280`, `FatFs` shared-SPI trap).
   - `0016a-industrial-buses-can-rs485-modbus.html` (`bxCAN`, `SN65HVD230`, `SP3485` DE/RE timing, Modbus RTU CRC-16, clone `CPUTAPID 0`).
   - `0026a-riscv-esp32c3-probe-rs-defmt.html` (RISC-V `mtvec`/`mcause`, `ESP32-C3` USB-Serial-JTAG, `probe-rs`, `defmt`, `esp-hal`).
   - `0011a-freertos-smp-esp32s3-lvgl-twai.html` (`ESP32-S3` dual-core pinning, `GPIO35–37` PSRAM guard, `ST7789` LVGL, `TWAI`, `INA226`).
   - `0024e-aosp17-cuttlefish-vts-vendor-hal.html` (AOSP 17 Cuttlefish KVM, side-by-side C++ & Rust `vendor.dev.hardware.fitnesstelemetry`, VTS).
3. **Part B Senior Mastery Lessons (`0037`–`0039`) & Desk Cheat Sheet #8:**
   - `0037` (TrustZone-M `TF-M`, `PSA Crypto`, `MCUboot` BLE DFU, `west spdx`, `ESP32-S3` virtual eFuses).
   - `0038` (Closed-loop PID, `TIM1` complementary PWM dead-time, `INA226` overcurrent cutoff, watchdogs, CAN bus-off recovery).
   - `0039` (`KiCad` 4-layer carrier PCB, decoupling, differential pairs, BOM/CPL, Hardware Bring-Up Report).
   - `reference/0008-hardware-bringup-buses-cheat-sheet.html` (Chip IDs, voltage traps, OpenOCD/`probe-rs`/`west` commands, `CFSR` decoding).
4. **4 New Verified Local C11 Labs (`./labs/`):**
   - `08_hardfault_decoder`, `09_modbus_rtu`, `10_bme280_compensation`, `11_pid_controller` — all passing under `-Wall -Wextra -Werror -Wconversion -fsanitize=address,undefined`.

## Preserved & Re-Categorized (No Deletion)
- Kept Track 00 (`0001`–`0004d`), C++ Bridge (`0012a`–`0012d`), and Rust Bridge (`0024a`–`0024d`) intact.
- Moved Go Cloud/MQTT (`0028b`–`0028c`, `0035a`, `labs/04`) and AAOS/Safety (`0030`–`0032`) to Optional/Archived in `index.html`.
- Applied Week 5 buffer for `BME280`/`FatFs` in Phase 1 and Buildroot-first / Yocto-stretch pacing in Phase 5A.
