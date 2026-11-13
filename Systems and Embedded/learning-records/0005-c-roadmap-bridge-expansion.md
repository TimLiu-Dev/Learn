# Track 00 C Language Bridge Expansion (roadmap.sh/c Audit)

The learner audited the initial C foundation against [roadmap.sh/c](https://roadmap.sh/c) to verify whether a junior Android developer (Kotlin/Java background) needed additional C language fundamentals before entering bare-metal firmware (Track 01) and Linux kernel/AAOS development.

## Audit Findings
- **Strong Hardware & Toolchain Start, Thin C Grammar Bridge:** Lessons `0001`–`0008` already covered silicon representation, ELF segments, GDB/QEMU, MMIO registers, linker scripts, NVIC interrupts, DMA, and state machines, but assumed fluency in intermediate C mechanics (`<stdint.h>`, qualifiers, array decay, pointer declarations, struct padding/alignment, function pointers, headers/linkage, and preprocessor hygiene).
- **2027 Senior Systems Filters Added:** Emphasized MISRA/CERT-aligned practices (`memcpy` instead of unaligned strict-aliasing casts, explicit buffer capacities instead of `strcpy`/`gets`, `do { ... } while(0)` macros, `_Static_assert`, `-Wall -Wextra -Werror -Wconversion`, and `-fsanitize=address,undefined`).

## What Was Added
1. **4 Interactive Track 00 Bridge Lessons (`0004a`–`0004d`):** Placed between Lesson `0004` and Lesson `0005` so existing `0005`–`0036` numbers and `localStorage` keys remain intact (total curriculum expanded from 36 to 40 lessons):
   - `0004a-c-types-qualifiers-bitwise-operators.html`: Fixed-width `<stdint.h>` types, `const`/`volatile`/`restrict`/`static`, and unsigned bitwise safety.
   - `0004b-c-pointers-arrays-and-strings.html`: Right-Left pointer reading, `const` pointer combinations, array decay, `void *` / `uintptr_t`, bounds-safe `snprintf`/`memcpy`, and overflow-checked heap sizing.
   - `0004c-c-structs-unions-function-pointers.html`: Struct alignment & padding minimization, `offsetof`/`_Static_assert`, unaligned wire parsing via `memcpy`, tagged `union`s, and `typedef` function-pointer callback tables.
   - `0004d-c-headers-macros-builds-and-ub.html`: `.h` include guards & `extern` declarations vs single `.c` definitions, parenthesis + `do { ... } while(0)` macro hygiene, GNU `Make` pattern rules (`-MMD -MP`), and ASan/UBSan flags.
2. **Desk Reference Sheet #3 (`reference/0003-c-for-android-devs-cheat-sheet.html`):** Print-ready summary of Kotlin-to-C types, pointer declarations, bounds-safe buffers, alignment/aliasing rules, linkage, macros, and `Makefile` compiler flags.

## Intentional Omissions (Low-ROI Legacy Desktop C)
Kept out of the core embedded/systems path: interactive console desktop I/O (`scanf`/`fscanf` formatting trivia), legacy `<time.h>`/`locale.h` formatting, `setjmp`/`longjmp` exception simulation, authoring arbitrary `<stdarg.h>` varargs APIs, and Windows/desktop package managers (`Conan`/`vcpkg`).

## Verification
- Verified all 40 lessons, 3 cheat sheets, and `index.html` after edits using automated HTML structure, navigation-chain, CSS class, quiz balance, and JavaScriptCore (`jsc`) runtime checks (`0` failures).
