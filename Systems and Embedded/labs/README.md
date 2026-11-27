# Local Systems & Embedded Labs (Real Compilers & Hardware Algorithms)

The HTML lessons give you quick conceptual checks and retrieval quizzes. **This `./labs/` workspace is where you verify real C11, C++20, and Rust systems code** on your MacBook Pro (`clang`, `clang++`, `rustc`/`cargo`, `make`) with `-Wall -Wextra -Werror -Wconversion` and ASan/UBSan before flashing physical boards.

## Core Lab Suite

```bash
cd labs
make test
```

| Lab | Stack & Compiler Flags | What It Teaches |
| :--- | :--- | :--- |
| `01_c_bridge` | C11 (`-Wall -Wextra -Werror -Wconversion -fsanitize=address,undefined`) | CRC-16-CCITT, big-endian 16-byte wire framing, safe buffer bounds |
| `02_cpp20_telemetry` | Modern C++20 (`-std=c++20 -fno-exceptions -Wall -Wextra -Werror`) | `constexpr` register bitfields, `std::span`, cache-aligned SPSC atomic ring buffer |
| `03_rust_gateway` | Rust (`cargo test`, `#![no_std]` core parser + host tests) | Zero-copy slice parsing, `Option`/`Result`, CRC verification |
| `06_linux_posix_ipc` | C11 POSIX (`-fsanitize=address,undefined`) | `shm_open`/`mmap`, non-blocking `AF_UNIX` `EAGAIN`/`poll()`, `sigaction` |
| `07_arm_cortex_m_boot` | C11 + GNU `.ld` (`arm-none-eabi-gcc` optional) | ARM Cortex-M4 vector table, `cortex_m4.ld` linker script, `.data`/`.bss` `crt0` |
| `08_hardfault_decoder` | C11 (`-fsanitize=address,undefined`) | Cortex-M stacked exception frame (`PC`) & `CFSR` fault-bit decoding |
| `09_modbus_rtu` | C11 (`-fsanitize=address,undefined`) | Modbus RTU CRC-16 (`0xA001`) + little-endian frame verification |
| `10_bme280_compensation` | C11 (`-fsanitize=address,undefined`) | Bosch BME280 datasheet 32-bit fixed-point temperature compensation |
| `11_pid_controller` | C11 (`-fsanitize=address,undefined`) | Closed-loop anti-windup PID step + `INA226` overcurrent kill-switch |

## Optional / Archived Cloud Labs
If you also explore the optional Cloud/Go extension (`0028b`–`0028c`, `0035a`), run:
```bash
make test-go test-e2e
```

## Target Hardware Toolchains (macOS vs ROG Flow Z13 Linux)
- **macOS (`MacBook Pro M5 Max`):** `brew install arm-none-eabi-gcc openocd probe-rs-tools cmake qemu` + Nordic nRF Connect SDK (`west`), ESP-IDF, and `rustup target add thumbv7em-none-eabihf riscv32imc-unknown-none-elf`.
- **Linux (`ROG Flow Z13`):** Buildroot / Yocto (`meta-raspberrypi`) for Phase 5A and AOSP 17 + Cuttlefish KVM for Phase 5B.
