# Systems & Embedded Engineering Resources

## Knowledge

### Core Architecture, C/C++/Rust & RTOS
- [Book: _Computer Systems: A Programmer's Perspective (3rd Edition)_ — R. Bryant & D. O'Hallaron](https://csapp.cs.cmu.edu/)
  Gold-standard systems textbook for memory hierarchy, linking, assembly, and virtual memory.
- [Reference: _ST RM0383 (STM32F411) & RM0008 (STM32F103) Reference Manuals_ — STMicroelectronics](https://www.st.com/resource/en/reference_manual/rm0383-stm32f411xce-advanced-armbased-32bit-mcus-stmicroelectronics.pdf)
  Primary register-level truth for RCC, GPIO, SPI, I2C, TIM1/quadrature, DMA, and bxCAN.
- [Reference: _ARM Cortex-M4 Generic User Guide (HardFault / CFSR / SCB)_ — Arm](https://developer.arm.com/documentation/dui0553/latest/)
  Authoritative specification for exception frame stacking, `CFSR`, `HFSR`, `MMFAR`, and `BFAR`.
- [Book: _Mastering the FreeRTOS Real Time Kernel_ & _ESP-IDF FreeRTOS SMP Guide_](https://docs.espressif.com/projects/esp-idf/en/stable/esp32s3/api-reference/system/freertos_idf.html)
  Primary references for single-core RTOS primitives, dual-core `xTaskCreatePinnedToCore`, and `TWAI`.
- [Documentation: _Zephyr Project, nRF Connect SDK (nRF54L15-DK) & MCUboot / TF-M_](https://docs.nordicsemi.com/bundle/ncs-latest/page/nrf/index.html)
  Official documentation for `west`, `sysbuild`, DeviceTree, BLE GATT, `native_sim`/Twister, `MCUboot`, and `PSA Crypto`.
- [Documentation: _The Embedded Rust Book, `embassy.dev`, `esp-hal` & `probe-rs`_](https://probe.rs/docs/)
  Authoritative guides for `no_std`, `embedded-hal` v1.0, async executors, RISC-V `esp-hal`, and `defmt` RTT debugging.

### Hardware Datasheets, Buses & PCB Design
- [Datasheets: _Bosch BME280, ST LSM6DSV16X, TI INA226, TI SN65HVD230 & Modbus RTU Over Serial Line_](https://www.modbus.org/docs/Modbus_over_serial_line_V1_02.pdf)
  Primary sensor/transceiver specs (`WHO_AM_I` registers, compensation formulas, 120Ω termination, 3.5-char silence timing).
- [Documentation: _KiCad Documentation & OpenOCD User's Guide_](https://docs.kicad.org/)
  Official references for 4-layer PCB schematic/layout, DFM exports, SWD flashing, and `CPUTAPID` overrides.

### Embedded Linux, AOSP 17 & Cuttlefish
- [Documentation: _The Linux Kernel Documentation (I2C, Char Devices, hwmon, DMA)_ — kernel.org](https://docs.kernel.org/)
  Primary kernel API reference alongside Raspberry Pi 4 bootloader/Device Tree docs.
- [Book: _The Linux Programming Interface_ — M. Kerrisk](https://man7.org/tlpi/)
  Authoritative POSIX reference for `AF_UNIX` sockets, `epoll`, `shm_open`/`mmap`, signals, and daemons.
- [Documentation: _The Buildroot User Manual & Yocto Project Mega-Manual_](https://buildroot.org/docs.html)
  Primary guides for reproducible custom Embedded Linux images (`meta-raspberrypi` for Yocto stretch).
- [Documentation: _AOSP Architecture, AIDL HALs, Cuttlefish & VTS_ — Google](https://source.android.com/docs/core/architecture/aidl-hals)
  Official specification for AOSP builds, Cuttlefish virtual devices, `@VintfStability` C++/Rust AIDL HALs, `sepolicy`, and VTS.

### Optional / Extended Tracks (Preserved)
- [Documentation: _Android Automotive OS (AAOS) & VHAL_ — Google](https://source.android.com/docs/automotive/vhal)
- [Documentation: _Eclipse Mosquitto / MQTT v5 & TimescaleDB_](https://mosquitto.org/documentation/)

## Wisdom (Communities)
- [r/embedded](https://reddit.com/r/embedded) — Hardware triage, schematic/PCB reviews, and firmware career advice.
- [Nordic DevZone](https://devzone.nordicsemi.com/) — Official Q&A for `nRF54L15-DK`, nRF Connect SDK, MCUboot, and TF-M.
- [ESP32 Forum & `esp-rs` Matrix](https://esp32.com/) — Practical help for `ESP32-S3`/`C3`, PSRAM constraints, and `esp-hal`.
- [Rust Embedded Matrix (`#rust-embedded:matrix.org`)](https://matrix.to/#/#rust-embedded:matrix.org) — `probe-rs`, `defmt`, and `embassy` troubleshooting.
- [AOSP Building Google Group](https://groups.google.com/g/android-building) — AOSP builds, Cuttlefish, SELinux, and AIDL HALs.
