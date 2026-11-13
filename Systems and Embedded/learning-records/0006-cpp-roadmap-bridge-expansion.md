# Track 03 C++ Language Bridge Expansion (`roadmap.sh/cpp` Audit)

The learner audited Track 03 (`0013`–`0016`) and downstream AOSP C++ AIDL HAL material (`0023`, Milestone Project 03) against [roadmap.sh/cpp](https://roadmap.sh/cpp) to bridge a junior Android developer (Kotlin/Java background) into senior-level Modern Embedded & Systems C++20 for 2027.

## Audit Fixes Applied to Existing Track 03 (`0013`–`0016`) & `index.html`
- **`0013-cpp-type-safe-registers.html`:** Added C++20 `concept RegisterWord` constraint to `Register` (aligning with `index.html`'s C++20 concepts claim) and documented the `1 <= length < 32` precondition on `make_bitmask()` to prevent `1U << 32` shift Undefined Behavior.
- **`0014-cpp-heapless-raii-and-spans.html`:** Corrected the inaccurate claim that C++20 `std::span::operator[]` performs automatic runtime bounds checking (noting STL hardening `-D_GLIBCXX_ASSERTIONS`, `.size()` checks, and C++26 `.at()`), added a compiling `parse_can_payload(std::span<const uint8_t>, ...)` snippet, and explained placement `new` with static `alignas(T) std::array` buffers.
- **`0015-cpp-atomics-and-lock-free-queues.html`:** Added `alignas(64)` cache-line separation on `m_head` and `m_tail` in `SPSCQueue` to avoid SMP false sharing, added power-of-two masking notes for Cortex-M0/M0+, and fixed the workbench tab filename (`spsc_checker.cpp`).
- **`0016-cpp-embedded-patterns-crtp.html` & `index.html`:** Clarified `vptr` overhead (`4` bytes on 32-bit MCUs, `8` bytes on 64-bit ARM64), fixed the tab filename (`power_state_machine.cpp`), and aligned Track 03 dashboard copy.

## What Was Added
1. **4 Interactive Track 03 C++ Bridge Lessons (`0012a`–`0012d`):** Inserted between Lesson `0012` and Lesson `0013` so existing `0013`–`0036` numbers and `localStorage` keys remain intact (total curriculum expanded from 40 to 44 lessons):
   - `0012a-cpp-references-value-semantics-and-casts.html`: Kotlin heap references vs C++ value semantics, `T&` / `const T&` / `T*`, nested `namespace`s, `enum class`, `auto`/structured bindings, and named C++ casts (`static_cast`, `const_cast`, `reinterpret_cast`, `dynamic_cast` under `-fno-rtti`).
   - `0012b-cpp-lifetimes-move-semantics-and-smart-ptrs.html`: Deterministic destructors, Rule of Zero vs Rule of Five (`= delete`/`= default`), move semantics (`&&`, `std::move`), `std::unique_ptr` custom deleters for Linux descriptors, `std::shared_ptr`/`std::weak_ptr` vs MCU no-heap rules.
   - `0012c-cpp-errors-lambdas-and-concepts.html`: Exception-free error handling (`-fno-exceptions`), `[[nodiscard]]`, `std::optional`, C++23 `std::expected` / `ScopedAStatus`, `noexcept`, stateless lambdas vs `std::function` heap-allocation traps, `if constexpr`, and C++20 `concepts`.
   - `0012d-cpp-containers-extern-c-cmake-and-tests.html`: Bare-metal MCU (`std::array`, `std::span`, `std::string_view`) vs Linux/AOSP (`std::vector::reserve`) containers, name mangling & `extern "C"` linkage boundaries, dual-target `CMakeLists.txt`, and host GoogleTest + ASan/UBSan testing.
2. **Desk Reference Sheet #4 (`reference/0004-modern-cpp-systems-cheat-sheet.html`):** Print-ready C++17/20/23 summary covering parameter passing, Rule of Five, exception-free errors, container boundaries, `extern "C"`, and MCU vs host compiler flags.

## Intentional Exclusions (`roadmap.sh/cpp` Senior Filter)
Deliberately kept out of the core systems path: `iostream` formatting (~100+ KB MCU bloat / locale locking), throwing C++ exceptions & RTTI (`-fno-exceptions -fno-rtti`), deep multiple/diamond OOP inheritance hierarchies, legacy `SFINAE` (`std::enable_if`) authoring, and Windows/desktop tooling (`WinDbg`, `vcpkg`, `Conan`).

## Verification
- Executed `verify_curriculum.py` across all **44 lessons**, **4 reference sheets**, and `index.html` using Python + macOS JavaScriptCore (`jsc`): `HTML_ISSUES_COUNT: 0`, `QUIZ_BALANCE_WARNINGS: 0`, and `JS_FAILURES_COUNT: 0`.
- Verified C++20 syntax compilation (`c++ -std=c++20 -Wall -Wextra -Werror -fsyntax-only`) returned exit code `0` for `0012a`–`0012d` workbench solutions and the new `0013`/`0014` snippets.
