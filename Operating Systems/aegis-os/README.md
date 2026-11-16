# AegisOS-64 — Bare-Metal ARM64 Kernel & Zero-Copy IPC Capstone

A portfolio-grade 64-bit OS kernel written in freestanding **C11 + AArch64 Assembly**, implementing the full stack from **Operating Systems Mastery (2027)**.

## Subsystems Implemented
- **ARM64 Boot & Trap Gate** (`arch/arm64/boot.S`, `vectors.S`, `switch.S`, `kernel/trap.c`): EL2 $\rightarrow$ EL1 drop, 2KB-aligned `VBAR_EL1` exception vectors, `pt_regs` trap frame, per-process 256-bit `seccomp` syscall allowlist, and integer-overflow-safe `copy_from_user_checked()`.
- **EEVDF-lite Scheduler** (`kernel/sched.c`): Weighted virtual runtime (`vruntime`), lag eligibility, and earliest virtual deadline selection.
- **Futex Synchronization** (`kernel/futex.c`): Atomic sequence-checked `futex_wait()` (lost-wakeup protection) and `futex_wake()`.
- **Memory Management** (`kernel/mm.c`): Binary Buddy page-frame allocator (split + XOR coalescing), SLUB-style fixed object cache, and Copy-on-Write (`PTE_COW`) `fork()` + write-fault handler.
- **Binder-Inspired Single-Copy IPC** (`kernel/ipc_binder.c`): 1MB mmap transaction ring, 50% async (`oneway`) quota cap, and cross-process FD (`memfd`/`DMA-BUF`) translation.
- **VFS & Async `io_uring` Ring** (`kernel/vfs.c`, `kernel/io_ring.c`): Inode/dentry RamFS + lock-free acquire/release SQ/CQ I/O rings.

## Quick Verification (Works Immediately on macOS / Linux with `clang`)
```bash
cd aegis-os
make test-host    # Compiles & runs all C subsystem & security tests natively
make check-arm64  # Cross-compiles all AArch64 .S and freestanding .c files
```

## Boot in QEMU (Optional Toolchain)
Install `lld` and `qemu`:
```bash
brew install lld qemu          # macOS
make test-qemu                 # Links build/aegis.elf and boots headless QEMU virt
```
