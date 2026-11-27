# Mission: End-to-End Embedded Systems & AOSP Platform Engineering (2027 Roadmap)

## Why
Stack deterministic bare-metal firmware, real-time operating systems, and Embedded Linux underneath an existing BSEE + commercial Android/Kotlin background (physical fitness console hardware, Unix domain sockets, system services, AOSP IME) to become competitive for Mid-Level Embedded / Android Platform / Robotics / Edge Systems roles by Weeks 20–21, then advance to Senior Systems mastery.

## Success looks like
- **Prerequisites Refreshed:** Solid fluency in BSEE bench electronics (1.8V/3.3V/5V logic levels, open-drain pull-ups, decoupling, differential pairs, DMM/oscilloscope probing) and Android/Linux IPC (`AF_UNIX` sockets, `SystemService` boundaries, `adb`/`logcat`, AOSP IME IPC).
- **Phase 1 — Bare-Metal C (`STM32F411CEU6`):** Write zero-HAL register drivers (SPI+DMA `SSD1306` OLED, `BME280`, I2C+EXTI `LSM6DSV16X` into SPSC ring buffer, `EC11` timer quadrature, `FatFs` MicroSD), linker scripts, startup code, and `HardFault` (`CFSR`) decoders verified with `24MHz` logic analyzer & `DSO2512G`.
- **Phase 2 — Modern C++20/23 & Fieldbuses (`2x STM32F103C8T6`):** Zero-overhead C++ abstractions (`constexpr`, `std::span`, CRTP/concepts, `-fno-exceptions -fno-rtti`), 1 Mbps `bxCAN` (`SN65HVD230`), and half-duplex `RS485` / Modbus RTU (`SP3485`).
- **Phase 3 — Embedded Rust (`STM32F411` + `ESP32-C3`):** Cross-architecture `no_std` + `embassy-rs` / `esp-hal` async firmware sharing `embedded-hal` v1.0 crates, RISC-V trap handling (`mtvec`/`mcause`), `probe-rs` + `defmt` RTT logging, and `embedded-hal-mock` tests.
- **Phase 4 — Production RTOS (`nRF54L15-DK` + `ESP32-S3`):** Zephyr RTOS (`west`, `sysbuild`, DeviceTree, BLE GATT, Shell, `native_sim`/Twister) + FreeRTOS SMP (`xTaskCreatePinnedToCore`, `TWAI` CAN, LVGL on `ST7789` TFT via PSRAM DMA) + `INA226`/DMM power profiling. *(Job Search Trigger: Weeks 20–21)*
- **Phase 5A — Embedded Linux (`Raspberry Pi 4 8GB`):** Pi 4 boot chain vs SPL/TF-A/U-Boot, `.dtbo` Device Tree overlays, out-of-tree Linux I2C char driver (`/dev/telemetry_sensor` vs mainline `ina2xx`), userspace socket daemon, and reproducible Buildroot (+ Yocto stretch) images.
- **Phase 5B — AOSP 17 Platform (`ROG Flow Z13 Linux` + Cuttlefish):** Build & boot AOSP 17 Cuttlefish, implement `@VintfStability` `vendor.dev.hardware.fitnesstelemetry` AIDL HAL side-by-side in **C++ and Rust**, `init.rc`, `sepolicy`, VINTF, VTS tests, and Kotlin Compose system dashboard.
- **Part B Senior Gate (Phases 6–8):** `MCUboot` + `TF-M` TrustZone-M + `PSA Crypto` + BLE DFU + ESP32-S3 virtual eFuses; `TIM1` dead-time complementary PWM + closed-loop PID motor control + `INA226` overcurrent cutoff + CAN bus-off recovery; `KiCad` 4-layer carrier PCB + full silicon bring-up report.

## Constraints
- **Pacing:** ~12–15 focused hours/week alongside full-time engineering work (~32 weeks for Part A; Part B afterward).
- **Physical Hardware Lab:** Finalized 33-item (37-unit) Taobao hardware kit — Nordic `nRF54L15-DK`, WeAct `STM32F411CEU6`, `ESP32-S3 N16R8`, `ESP32-C3-Zero-M`, 2x `STM32F103C8T6`, `Raspberry Pi 4 (8GB)`, `ZEEWEII DSO2512G`, `24MHz` Logic Analyzer, `Delixi DT9205APRO` DMM, `CP2102`, `CMSIS-DAP`, `ST-LINK V2`, `2x SP3485`, `2x SN65HVD230`, `BME280`, `LSM6DSV16X`, `SSD1306`, `ST7789`, `MicroSD`, `INA226`, `EC11`, `TB6612FNG`, `12GA-N20 6V 300RPM` Hall-encoder motor + wheel/bracket, `2x MB-102` breadboard kits, `HS-02A` soldering + rework kit (`63/37` solder, `KE-150` flux, brass wool, ESD tweezers, `2016` wick), `MacBook Pro M5 Max` (firmware toolchains), and `ROG Flow Z13` running Linux (Buildroot/Yocto/AOSP 17 Cuttlefish).
- **Engineering Discipline Every Phase:** Host unit tests (Unity/Ceedling, GoogleTest, `cargo test`, `native_sim`/Twister), GitHub Actions CI, GDB/logic-analyzer/scope evidence, and README wiring + pinned version docs.

## Out of scope (for Part A Core Gate)
- Legacy 8-bit microcontrollers (Arduino AVR, 8051, PIC) and deprecated Android HIDL.
- Heavy enterprise Java/Spring Boot backends.
- Optional / Post-Gate specializations (preserved in workspace): AAOS VHAL/Hypervisors (`0030`–`0032`), Cloud MQTT/Go Ingestor (`0028b`–`0028c`), and Kernel Rust (`0027`).
