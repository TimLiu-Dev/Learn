# Linux Engineering Resources

## Knowledge

- [Roadmap: roadmap.sh/linux](https://roadmap.sh/linux)
  Baseline checklist for core Linux navigation, permissions, processes, storage, networking, and bash automation. Use for: verifying foundational coverage.
- [Reference: Linux man-pages (man7.org) — Michael Kerrisk](https://man7.org/linux/man-pages/)
  Authoritative online Linux manual pages covering commands (Section 1), syscalls (Section 2), and `/proc` / file formats (Section 5). Use for: exact flag semantics and kernel syscall behavior.
- [Specification: Filesystem Hierarchy Standard (FHS 3.0) — Linux Foundation](https://refspecs.linuxfoundation.org/FHS_3.0/fhs-3.0.html)
  Official standard defining `/etc`, `/var`, `/usr`, `/opt`, `/srv`, `/tmp`, and `/run`. Use for: authoritative directory placement rules.
- [Manual: GNU Bash Reference Manual](https://www.gnu.org/software/bash/manual/bash.html)
  Canonical specification for Bash builtins, quoting, globbing, brace expansion, redirections, and shell startup behavior. Use for: shell parsing and scripting edge cases.
- [Documentation: systemd Official Docs (systemd.io)](https://systemd.io/)
  Primary reference for unit files, `systemctl`, `journalctl`, timers, resource limits (`cgroups v2`), and service sandboxing. Use for: Phase 2 and Capstone service architecture.
- [Documentation: The Linux Kernel Documentation](https://docs.kernel.org/)
  Upstream kernel docs for `/proc`, `/sys`, `cgroups v2`, namespaces, OOM killer, and virtual memory tunables. Use for: Phase 6 kernel internals.
- [Guide: ArchWiki (wiki.archlinux.org)](https://wiki.archlinux.org/)
  Highest-signal community technical wiki for practical Linux networking, LVM, bootloaders, `nftables`, SSH, and systemd configuration. Use for: configuration syntax and troubleshooting recipes.
- [Performance: Linux Performance & eBPF — Brendan Gregg](https://www.brendangregg.com/linuxperf.html)
  Industry-standard methodology (USE method, flamegraphs, `perf`, `bpftrace`) for Senior Linux performance engineering. Use for: Lesson 22+ observability and profiling.

## Wisdom (Communities)

- [r/linuxadmin](https://www.reddit.com/r/linuxadmin/)
  Professional enterprise Linux system administrators. Use for: production architecture critique, career advice, and real-world ops patterns.
- [Unix & Linux Stack Exchange](https://unix.stackexchange.com/)
  High-signal technical Q&A for POSIX shell edge cases, permissions, systemd, and kernel behavior. Use for: debugging subtle CLI and OS quirks.
- [LWN.net](https://lwn.net/)
  Deep Linux kernel and systems journalism with expert practitioner discussions. Use for: staying current on 2026–2027 kernel, eBPF, and security developments.

## Gaps

- Add curated CIS Benchmark PDF link and `nftables` wiki reference when authoring Phase 4 and Phase 6 lessons.
