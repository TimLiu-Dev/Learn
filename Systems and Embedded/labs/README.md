# Local Systems & Embedded Labs (Real Compilers & Simulators)

The HTML lessons give you quick conceptual checks and retrieval quizzes. **This `./labs/` workspace is where you build real systems muscle** using real compilers, sanitizers, unit test suites, and end-to-end pipelines on macOS.

## What Compiles & Runs Out-of-the-Box Today
Verified on your Mac with `clang`, `clang++`, `rustc`/`cargo`, `go`, and `make`:

```bash
cd labs
make test
```

| Lab | Stack & Compiler Flags | What It Teaches |
| :--- | :--- | :--- |
| `01_c_bridge` | C11 (`-Wall -Wextra -Werror -Wconversion -fsanitize=address,undefined`) | CRC-16-CCITT, big-endian 16-byte wire framing, safe buffer bounds |
| `02_cpp20_telemetry` | Modern C++20 (`-std=c++20 -fno-exceptions -Wall -Wextra -Werror`) | `constexpr` register bitfields, `std::span`, cache-aligned SPSC atomic ring buffer |
| `03_rust_gateway` | Rust (`cargo test`, `#![no_std]` core parser + host tests) | Zero-copy slice parsing, `Option`/`Result`, CRC verification |
| `04_go_cloud_ingestor` | Go (`go test -race ./...`) | Bounded channel batching, QoS-1 idempotent dedup, graceful `context` shutdown |
| `05_capstone_e2e` | C11 encoder → Rust validator → Go ingestor | Live top-to-bottom local pipe simulating the Silicon-to-Cloud Capstone |
| `06_linux_posix_ipc` | C11 POSIX (`-fsanitize=address,undefined`) | `shm_open`/`mmap`, non-blocking `AF_UNIX` `EAGAIN`/`poll()`, `sigaction` |
| `07_arm_cortex_m_boot` | C11 + GNU `.ld` (`arm-none-eabi-gcc` optional) | ARM Cortex-M4 vector table, `cortex_m4.ld` linker script, `.data`/`.bss` `crt0` |

## Optional Target Hardware & QEMU Emulation Tools (macOS Homebrew)
When you reach Track 01 (Lesson `0004`+) and Track 06 DevOps (`0028f`), install cross-compilers & QEMU:

```bash
brew install qemu arm-none-eabi-gcc cmake docker
```
