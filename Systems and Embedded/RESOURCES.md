# Systems & Embedded Engineering Resources

## Knowledge

- [Book: _Computer Systems: A Programmer's Perspective (3rd Edition)_ — R. Bryant & D. O'Hallaron](https://csapp.cs.cmu.edu/)
  The gold-standard Carnegie Mellon systems textbook. Use for: memory hierarchy, processor architecture, linker mechanics, and virtual memory.
- [Book: _Embedded Systems: Introduction to ARM Cortex-M Microcontrollers_ — J. Valvano](https://users.ece.utexas.edu/~valvano/)
  UT Austin EE foundational text. Use for: bare-metal registers, NVIC interrupts, SysTick, and serial bus protocols.
- [Book: _Mastering the FreeRTOS Real Time Kernel_ — R. Barry](https://www.freertos.org/Documentation/RTOS_book.html)
  Primary reference by FreeRTOS author. Use for: task scheduling, queue management, mutex priority inheritance, and tickless idle.
- [Documentation: _Linux Device Drivers & The Linux Kernel Documentation_ — kernel.org](https://docs.kernel.org/)
  Primary source for kernel internals. Use for: character device drivers, memory barriers, wait queues, and DMA mapping.
- [Book: _The Linux Programming Interface_ — M. Kerrisk](https://man7.org/tlpi/)
  Authoritative POSIX/Linux systems reference. Use for: `epoll`, Unix domain sockets, `shm_open`/`mmap`, signals, and daemon lifecycle.
- [Documentation: _The Yocto Project Mega-Manual & Mastering Embedded Linux Programming_](https://docs.yoctoproject.org/)
  Standard specification for custom Embedded Linux builds. Use for: BitBake recipes, meta-layers, Device Tree integration, and RAUC OTA.
- [Documentation: _Android NDK, AOSP Architecture & AIDL HALs_ — Google](https://source.android.com/docs/core/architecture/aidl-hals)
  Official specification for modern Android native & HAL layers. Use for: JNI/NDK bridges, AIDL interfaces, backend C++/Rust services, and VINTF manifests.
- [Book: _Embedded Android_ — K. Yaghmour (O'Reilly)](https://www.oreilly.com/library/view/embedded-android/9781449327958/)
  Foundational text on AOSP plumbing. Use for: Android init, Zygote, Binder driver, and filesystem layouts.
- [Documentation: _Android Automotive OS (AAOS) Architecture & VHAL_ — Google](https://source.android.com/docs/automotive/vhal)
  Official specification for connected car systems. Use for: CarPropertyManager, Vehicle HAL properties, and cluster display integration.
- [Documentation: _The Embedded Rust Book & Embassy Async Framework_ — rust-lang.org & embassy.dev](https://docs.rust-embedded.org/book/)
  Authoritative guide to bare-metal Rust. Use for: `no_std`, peripheral access crates (PAC), and async embedded runtimes.
- [Documentation: _Eclipse Mosquitto / MQTT v5 Spec, Protocol Buffers & MCUboot_](https://docs.mcuboot.com/)
  Core references for embedded cloud and OTA engineering. Use for: mTLS device identity, `nanopb`/`prost`, and signed A/B firmware rollback.

## Wisdom (Communities)

- [r/embedded](https://reddit.com/r/embedded)
  High-signal community of professional firmware and systems engineers. Use for: real-world hardware triage, schematic reviews, and career guidance.
- [Embedded.fm Podcast & Community](https://embedded.fm/)
  Long-running practitioner discussions. Use for: real-world embedded trade-offs, engineering ethics, and systems culture.
- [AOSP Building Google Group](https://groups.google.com/g/android-building)
  The direct venue for engineers compiling and customizing Android OS. Use for: build issues, device trees, and Treble HAL troubleshooting.
- [Rust Embedded Matrix Channel (#rust-embedded:matrix.org)](https://matrix.to/#/#rust-embedded:matrix.org)
  Active community of engineers running Rust on bare-metal silicon. Use for: driver crates, PAC debugging, and async runtime questions.
- [Yocto Project Mailing Lists & Matrix](https://www.yoctoproject.org/community/mailing-lists/)
  Practitioner forum for custom Embedded Linux BSPs, BitBake recipe debugging, and OTA integration.

## Gaps
- Interactive CAN-FD automotive bus capture datasets: generated simulated DBC/frame test vectors inside `./labs/` for local macOS verification without physical hardware.
