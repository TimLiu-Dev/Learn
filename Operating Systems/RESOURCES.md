# Operating Systems Engineering Resources

## Knowledge

- [Book: _Operating Systems: Three Easy Pieces (OSTEP)_ — Remzi & Andrea Arpaci-Dusseau](https://pages.cs.wisc.edu/~remzi/OSTEP/)
  Primary foundational textbook covering Virtualization, Concurrency, and Persistence. Use for: core mental models before mapping to Linux/ARM64.
- [Official Docs: Linux Kernel Documentation](https://docs.kernel.org/)
  Authoritative reference for EEVDF scheduling, `futex`, `mm/`, VFS, `io_uring`, cgroups v2, `seccomp`, and eBPF. Use for: exact kernel subsystem semantics.
- [Source Cross-Reference: Bootlin Elixir Linux Source](https://elixir.bootlin.com/linux/latest/source)
  Navigable Linux kernel C/ASM source (`arch/arm64`, `kernel/sched`, `drivers/android/binder.c`). Use for: inspecting real kernel implementations.
- [Official Docs: Android Open Source Project (AOSP) Architecture & Kernel](https://source.android.com/docs/core/architecture)
  Primary source for Android Binder, `bionic`, 16KB page sizes, Scudo, MTE, `lmkd`, Perfetto, and pKVM / Android Virtualization Framework.
- [Official Docs: ARM64 Architecture Reference Manual & Programmer's Guide](https://developer.arm.com/documentation)
  Reference for `EL0–EL3`, `SVC`/`ERET`, `VBAR_EL1`, `TTBR0/TTBR1`, `LDXR/STXR`, and Memory Tagging Extension (MTE).
- [Official Docs: eBPF & Perfetto Tracing](https://ebpf.io/what-is-ebpf/)
  Authoritative guides for eBPF verifier/maps/CO-RE and system-wide Linux/Android observability.
- [Official Docs: Rust for Linux Kernel Documentation](https://docs.kernel.org/rust/index.html)
  Primary reference for kernel Rust `Pin`, `Arc`, `Mutex<T>`, `UserSlice`, and fallible `GFP_KERNEL` abstractions.
- [Official Docs: Linux Power Management & Thermal Framework](https://docs.kernel.org/admin-guide/pm/sleep-states.html)
  Reference for `s2idle`, `cpuidle`, `wakeup_source`, `schedutil`, and Intelligent Power Allocation (IPA).
- [Reference: man7 signal-safety(7) & ELF / Bionic Linker Docs](https://man7.org/linux/man-pages/man7/signal-safety.7.html)
  Authoritative list of async-signal-safe primitives, `rt_sigreturn`, and `bionic` ELF/RELRO linker rules.

## Wisdom (Communities)

- [LWN.net (Linux Weekly News)](https://lwn.net/Kernel/)
  Deep, peer-reviewed journalism on Linux kernel patches (EEVDF, MGLRU, `io_uring`, `sched_ext`, Rust-in-Linux). Use for: understanding *why* kernel designs evolved.
- [r/osdev & OSDev Wiki](https://wiki.osdev.org/)
  Active systems/kernel-building practitioner community. Use for: QEMU bare-metal bring-up, ELF loading, linker scripts, and `AegisOS-64` debugging.
- [Systems & Kernel Engineering Discord / mailing lists (LKML)](https://lore.kernel.org/)
  Primary Linux kernel patch review archive for reading real senior maintainer code reviews.
