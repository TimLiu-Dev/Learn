# Notes & User Preferences

## Learner Profile
- **Background:** BSEE degree + commercial Android/Kotlin software engineering on physical fitness console hardware (Kotlin/Compose, legacy Java, Unix domain socket IPC, Android system services, AOSP IME integration).
- **Refresher Need:** Forgotten or only partially learned parts of BSEE electronics (pull-ups, RC rise time, logic-level safety, differential termination, scope/DMM probing) and low-level Android/Linux IPC (`AF_UNIX` framing, `SystemService` vs HAL boundaries, IME plumbing). Covered explicitly in the **Prerequisites & Refresher** section (`0000a`–`0000b`).
- **Initial Low-Level Gap:** Needs structured ramp-up in C11, C++20/23, Rust (`no_std` / `embassy` / `esp-hal`), bare-metal registers, RTOS internals, Linux kernel drivers, and AOSP 17 platform builds.

## Curriculum Decisions & Preferences
- **Language & Hardware Progression:**
  - *Prep / Language Bridges (Keep Intact):* Track 00 (`0001`–`0004d`), C++ Bridge (`0012a`–`0012d`), Rust Bridge (`0024a`–`0024d`).
  - *Part A (Weeks 1–32, Mid-Level Gate; Job Search at Weeks 20–21):* Phase 1 Bare-Metal C (`STM32F411`) → Phase 2 C++20 + CAN/RS485 (`2x Blue Pill`) → Phase 3 Embedded Rust (`STM32F411` + `ESP32-C3`) → Phase 4 Zephyr (`nRF54L15-DK`) + FreeRTOS SMP (`ESP32-S3`) → Phase 5A Linux (`Pi 4`) → Phase 5B AOSP 17 (`ROG Flow Z13` + Cuttlefish).
  - *Part B (Senior Gate):* Phase 6 (`MCUboot` + `TF-M` TrustZone + ESP32 virtual eFuses) → Phase 7 (`STM32F411` + `TB6612FNG` + `12GA-N20 6V 300RPM` Hall-encoder motor closed-loop PID + fault injection) → Phase 8 (`KiCad` 4-layer PCB + bring-up).
- **Hardware Cart Sync:** Finalized at **33 line items / 37 units** (¥1,425.91) including `12GA-N20` motor + `TB6612FNG` + full rework gear (`KE-150` flux, `2016` wick, ESD tweezers, brass wool, `63/37` solder).
- **Navigation & Capstone Alignment:** Sequential lesson header/footer navigation follows the 2027 Part A → Part B roadmap order; Capstone (`0033`–`0035`) and Playbook (`0036`) align directly to `Pi 4 + AOSP 17 Cuttlefish + vendor.dev.hardware.fitnesstelemetry`.
- **Pacing Adjustments Applied:**
  - **Phase 1 Buffer:** Stage Core drivers (`SSD1306`, `LSM6DSV16X`, `EC11`) in Weeks 1–4 and `BME280` + `FatFs` MicroSD in Week 5 so a shared-SPI MicroSD hardware issue doesn't block Phase 2.
  - **Phase 5A Buildroot-First:** Use **Buildroot** for rapid Phase 5A iteration on the Pi 4; treat full **Yocto** (`meta-raspberrypi`) customization as Week 25 stretch / post-Phase 5B polish.
- **Phase 5B AIDL HAL Standard:** Side-by-side **C++ and Rust** implementations for `@VintfStability` `vendor.dev.hardware.fitnesstelemetry` on AOSP 17 Cuttlefish.
- **Preserved Optional / Deferred Tracks (Do Not Delete):**
  - Go Cloud & MQTT (`0028b`, `0028c`, `labs/04_go_cloud_ingestor`, `reference/0007`)
  - AAOS VHAL, ISO 26262 & Hypervisors (`0030`–`0032`)
  - Rust in Linux Kernel (`0027`), `bpftrace` (`0020c`), `PREEMPT_RT` (`0020i`), Android NDK/JNI (`0020e`)
- **Host Split:** macOS (`MacBook Pro M5 Max`) runs firmware toolchains (`arm-none-eabi-gcc`, `probe-rs`, `west`, `esp-idf`, `clang`, `cargo`) and `./labs/` (`make test`); Linux (`ROG Flow Z13`) runs Buildroot/Yocto and AOSP 17 + Cuttlefish KVM.

## Instructional Design Guidelines
- **Zone of Proximal Development (ZPD):** Explain electrical and memory concepts cleanly before hardware wiring; warn explicitly about irreversible hardware traps (e.g., `nRF54L15-DK` 1.8V default I/O, `CP2102` 5V level vs Pi 4, `ESP32-S3` `GPIO35–37` octal PSRAM, real ESP32 eFuses).
- **Quiz Rule:** All multiple-choice options must maintain identical word counts and balanced character lengths so formatting gives no unintended cues.
