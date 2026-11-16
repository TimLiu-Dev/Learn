# Curriculum Audit, Bug Fixes & Phase 6 Expansion

Status: active

## Context
Before starting Lesson 01, the learner requested a full audit and expansion of the 16-lesson curriculum to ensure comprehensive end-to-end Operating Systems mastery from Junior Android Developer to Senior OS/Systems Engineer in 2027.

## Findings & Fixes Applied
1. **Progress Persistence & Simulator Fixes**:
   - Migrated `lesson-01.html` onto the shared `assets/os-course.js` engine, fixing the revisit bug that overwrote `lessonScores["01"]` with `"0/6"`.
   - Enhanced `assets/os-course.js` to persist per-lesson `passedLabs` and `quizSelections` in `localStorage` (`os2027_progress_v1`).
   - Corrected `lesson-01.html` simulator `ELR_EL1` display after `SVC #0` to `0x0000401004` (`PC + 4`), matching ARM64 hardware semantics.
   - Clarified in `capstone.html` which `aegis-os/` subsystems are provided as host-tested C scaffold vs. which QEMU hardware integrations (GICv3 timer IRQ, MMU `TTBR0/1`, EL0 `init`, ELF64 loader, VirtIO-Blk) are built across Milestones 1–5.
2. **Phase 6 Curriculum Expansion (Lessons 17–20)**:
   - Added `lesson-17.html`: Hardware Interrupts, GICv3, `softirq`/threaded IRQs/`workqueue`, `hrtimer`, and `NO_HZ`.
   - Added `lesson-18.html`: Kernel Signals (`rt_sigframe`, `SA_RESTART`, `pidfd`), `execve()` ELF64 `auxv`, ASLR, PLT/GOT Full RELRO, and Android `debuggerd` tombstones.
   - Added `lesson-19.html`: Power Management (`DVFS`/`schedutil`, `cpuidle`, `s2idle`, `wakeup_source`/`EPOLLWAKEUP`, Thermal IPA).
   - Added `lesson-20.html`: Rust in the Linux Kernel (`Pin<KBox<T>>`, `Mutex<T>`, `UserSlice`, fallible `GFP_KERNEL`) and Android Rust Binder.

## Implications for Future Teaching
- Total interactive scope is now **20 lessons, 60 runnable coding labs, and 120 diagnostic MCQs**.
- Note: `ld.lld` and `qemu-system-aarch64` are not yet installed on this macOS host (`brew install lld qemu` will unlock `make test-qemu` in `aegis-os/`).
