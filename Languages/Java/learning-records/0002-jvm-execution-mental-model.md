# JVM Execution Mental Model Established

> **Status:** Superseded by [0009-audit-and-readiness-reset.md](0009-audit-and-readiness-reset.md). This early seed log recorded Lesson 0002 generation rather than demonstrated learner mastery; learner is starting fresh from Lesson 0002.

Established the two-tier compilation paradigm (source `.java` to architecture-neutral `.class` bytecode via `javac`, executed by the HotSpot JVM with dynamic JIT compilation). Dissected the precise semantics of `public static void main(String[] args)` and introduced bytecode inspection via `javap -c`. Sets the baseline for understanding stack and heap memory in Lesson 0003.
