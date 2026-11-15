# Track 06 Rust Language Bridge Expansion (`roadmap.sh/rust` Audit)

Audited Track 06 (`0025`–`0028`), Milestone Capstone `0035`, Reference Sheet #2, and `index.html` against [roadmap.sh/rust](https://roadmap.sh/rust) to bridge a junior Android developer (after Tracks 00–05 C11/C++20/Linux/Android foundations) into senior-level Systems & Embedded Rust for 2027.

## Audit Fixes Applied to Existing Track 06 & Capstone (`0025`–`0028`, `0035`)
- **`0025-bare-metal-rust-no-std.html`:** Scoped the borrow-checker safety claim to safe Rust (outside explicit `unsafe { ... }` blocks) and added a compiling `embedded-hal` v1.0 `signal_fault<P: OutputPin>` snippet.
- **`0026-async-embedded-rust-embassy.html`:** Clarified static `TaskStorage` arenas (`pool_size = 1` default) behind `#[embassy_executor::task]` rather than claiming arbitrary task count without pool configuration.
- **`0027-rust-in-the-linux-kernel.html`:** Replaced invalid `alloc`-dependent `String::from("CAN-Gateway-Active")` in `RustTelemetryModule` with `tag: &'static str`, added notes on fallible kernel allocation and `kernel::InPlaceModule`/`PinInit` (`try_pin_init!`), and fixed HTML entity escaping (`dev-&gt;lock`).
- **`0028-rust-aidl-hal-in-aosp.html`:** Fixed `TelemetryService::get_descriptor()` -> `BnTelemetry::get_descriptor()`.
- **`0035-capstone-rust-aidl-vhal-integration.html`:** Updated `start_telemetry_reader` to clone `callbacks` into `active_listeners` and drop the `MutexGuard` before invoking `cb.onPropertyEvent(...)` over Binder IPC.
- **Rust Workbenches Upgraded (`0025`–`0028`):** Replaced C `strstr(...)` string-check placeholders in `0025`–`0028` with authentic Rust (`.rs`) workbenches (`no_std_mmio.rs`, `embassy_blink.rs`, `kernel_mutex.rs`, `telemetry_hal.rs`) while preserving `lesson_0025_practice`–`lesson_0028_practice` `localStorage` keys and adding `Ref #5` header links.

## What Was Added
1. **4 Interactive Track 06 Rust Bridge Lessons (`0024a`–`0024d`):** Inserted between Lesson `0024` and Lesson `0025` with bidirectional navigation (`0024` &harr; `0024a` &harr; `0024b` &harr; `0024c` &harr; `0024d` &harr; `0025`) so existing `0025`–`0036` numbers and `localStorage` keys remain intact (expanding the curriculum from **44 to 48 lessons**):
   - `0024a-rust-cargo-ownership-and-borrowing.html`: Cargo workspaces & feature flags (`core` vs `alloc` vs `std`), move semantics vs `Copy`, shared `&T` / exclusive `&mut T` borrowing rules, and non-owning `&[u8]` / `&str` slice parsing.
   - `0024b-rust-enums-pattern-matching-and-errors.html`: Zero-heap tagged `enum`s, exhaustive `match`, `Option<T>`, `#[must_use]` `Result<T, E>`, `From` conversions with `?` propagation, and avoiding `.unwrap()` panics in production firmware/HALs.
   - `0024c-rust-traits-generics-lifetimes-and-pin.html`: Traits & associated types, static monomorphized generics vs `dyn Trait` dynamic dispatch, closures, lifetimes (`'a`, `'static`), and why self-referential `async` futures require `Pin`.
   - `0024d-rust-concurrency-unsafe-ffi-and-tests.html`: `Box`, `Rc` vs `Arc`, data-inside-the-lock `Mutex<T>`, compile-time `Send`/`Sync` guarantees, the 5 `unsafe` operations, `#[repr(C)]` FFI safety wrappers, and `#[cfg(test)]` / `#[test]` unit tests.
2. **Desk Reference Sheet #5 (`reference/0005-rust-for-systems-devs-cheat-sheet.html`):** Print-ready 6-card reference sheet covering Ownership & Runtime Layers, Enums/Errors, Traits/Lifetimes/`Pin`, `Send`/`Sync`/Atomics/Smart Pointers, `unsafe` & C FFI Checklist, and Cargo `no_std` + Host QA commands (`cargo test`, `cargo clippy --all-targets -- -D warnings`, `cargo build --target thumbv7em-none-eabihf --release`).
3. **`index.html` Dashboard Updates:** Updated total curriculum scope (`48 Deep-Dive Interactive Lessons`), expanded `WHAT WE ADDED` with Track 06 Rust Bridge coverage, added Quick Reference button for Sheet #5, updated Track 06 header to `8 LESSONS + 1 MILESTONE PROJECT`, and added cards for `0024a`–`0024d`.

## Verification
- Executed `verify_curriculum.py` across all **48 lessons**, **5 reference sheets**, and `index.html` using Python + macOS JavaScriptCore (`jsc`): `HTML_ISSUES_COUNT: 0`, `QUIZ_BALANCE_WARNINGS: 0`, and `JS_FAILURES_COUNT: 0` (verifying links, CSS classes, escaping, nav chain, balanced quiz options, `SystemsQuiz`, and starter-fail vs solution-pass execution).
- Compiled all standalone theory `.code-window` blocks in `0024a`–`0024d` as well as all unescaped starter stubs and combined solutions in `0024a`–`0024d` and `0025` (`#![no_std]`) using `/opt/homebrew/bin/rustc --edition 2021 --crate-type lib -D warnings` (all exited `0`).
- Compiled and executed the `#[cfg(test)]` FFI unit test in `0024d` via `/opt/homebrew/bin/rustc --edition 2021 --test -D warnings`: `test tests::null_pointer_returns_none ... ok` (`1 passed; 0 failed`). *(Workbenches in `0026`–`0028` depend on external `embassy_executor`, Linux `kernel`, and AOSP `binder` crates and were verified via the `jsc` harness.)*
