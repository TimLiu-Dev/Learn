# Algorithmic Thinking & Technical Interview Foundations Resources

## Knowledge

- [Harvard CS50x — Lecture 3 (Algorithms), Lecture 4 (Memory) & Lecture 5 (Data Structures)](https://cs50.harvard.edu/x/)
  Physical demonstrations of binary search halving, hexadecimal RAM addresses, pointers, and separate-chaining hash tables. Use for: Lessons 01–04 hardware grounding.
- [MIT 6.006: Introduction to Algorithms (OpenCourseWare)](https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-spring-2020/)
  Formal asymptotic bounds (`O`, `Ω`, `Θ`), recursion trees, binary heaps, `Ω(n log n)` comparison lower bounds, and Counting Sort. Use for: Lessons 04, 12, 13, and 14.
- [Competitive Programmer's Handbook (Antti Laaksonen) — Chapters 1, 2, 10 & 21](https://cses.fi/book/book.pdf)
  Operational time complexity, the `10^7`–`10^8` ops/sec rule, bit manipulation masks, modular arithmetic (`10^9 + 7`), and `O(√n)` primality. Use for: Lessons 01, 05, and 10.
- [Open Data Structures (Pat Morin)](https://opendatastructures.org/)
  Rigorous proofs of dynamic array geometric doubling (`O(1)` amortized), `ArrayDeque` ring buffers, hash tables, binary heaps, and `O(V + E)` adjacency lists. Use for: Lessons 03, 11, and 12.
- [Kotlin 2.x Standard Library: Collections, Numbers & Ordering Documentation](https://kotlinlang.org/docs/collections-overview.html)
  Official reference for `IntArray`, `ArrayList`, `ArrayDeque`, `HashMap`, `HashSet`, `mod()`, `..<` open-ended ranges, and `compareBy`. Use for: Lessons 02, 08, 11, and 14.
- [Google Research: Nearly All Binary Searches and Mergesorts are Broken (Joshua Bloch)](https://research.google/blog/extra-extra-read-all-about-it-nearly-all-binary-searches-and-mergesorts-are-broken/)
  Seminal breakdown of 32-bit signed integer overflow in `(low + high) / 2` and `low + (high - low) / 2`. Use for: Lessons 05 and 07.
- [Stanford Bit Twiddling Hacks (Sean Eron Anderson)](https://graphics.stanford.edu/~seander/bithacks.html)
  Canonical catalog of Two's Complement properties, parity checks, and `n & (n - 1)` power-of-two masks. Use for: Lesson 10.
- [Edsger W. Dijkstra — Notes on Structured Programming (EWD249)](https://www.cs.utexas.edu/users/EWD/ewd02xx/EWD249.PDF)
  Foundational computer science monograph on proving loop correctness via invariants. Use for: Lesson 07.
- [Big-O Cheat Sheet & Complexity Reference](https://www.bigocheatsheet.com/)
  Quick visual growth curves and worst/average complexity tables across standard data structures and sorting algorithms. Use for: rapid complexity cross-checks.
- [Tech Interview Handbook & Grind 75 Study Strategy](https://www.techinterviewhandbook.org/coding-interview-prep/)
  Pragmatic rubric on interview communication, time-boxed practice, and spaced repetition. Use for: Lessons 06 and 08.

## Wisdom (Communities, Mock Platforms & Visualizers)

- [PythonTutor / Java & Kotlin Execution Visualizer](https://pythontutor.com/)
  Interactive line-by-line Call Stack frame and Heap reference visualizer. Use for: verifying Lesson 11 pointer nodes and Lesson 13 recursion unwinding.
- [Interviewing.io Hiring Process & Recorded Staff Mock Interviews](https://interviewing.io/guides/hiring-process)
  Real technical interview recordings, post-AI interview rubrics, and live mock feedback. Use for: Lesson 06 protocol calibration.
- [NeetCode 150 Roadmap & Video Walkthroughs](https://neetcode.io/roadmap)
  Structured Phase 2 pattern progression directly following Lesson 14 graduation. Use for: post-foundation problem solving.
- [LeetCode Discuss — Interview Question & Experience Reports](https://leetcode.com/discuss/)
  Up-to-date 2026–2027 candidate loop reports across Big Tech and AI-native engineering teams. Use for: calibrating real-world follow-up twists.
