# v7 rules for MINI PROJECTS M1–M7

Read `.build/rules/v7-rules.md` (hard limits, names), `.build/v7/plan.md` §6 (the project list) and `v5-rules.md` ("everything verified", Gradle rules, tools).

## What a mini project is

- **Size:** 5–8 hours of the learner's own hand-written work, at a phase boundary. It integrates the lessons of that phase and earlier ones into one program bigger than any lesson's final exercise.
- **Language:** the learner picks Kotlin OR Java for the whole project. The page recommends alternating: M1 Kotlin, M2 Java, M3 Kotlin, M4 Java, M5 Kotlin, M6 Java, M7 in whichever language the legacy code is in.
- **Feedback:** the loop is a **language-agnostic black-box checker** that runs the learner's finished program from the outside.

## Files

- `W/projects/mN-slug.html`
  - The page. It uses `../assets/course.css` and `../assets/lesson.js`, with `<body data-lesson="mN">`, so checkboxes, ratings and the results summary work.
- `W/projects/mN/`
  - `README.md`: a short plain-text version of the setup and how to run the checker, for the terminal.
  - `starter_kotlin/` and `starter_java/`: Gradle 9.7.1 projects (Kotlin DSL, Application plugin, JUnit 6, toolchain 25).
    - Copy the wrapper from `.build/l11/tdd_kotlin`.
    - `./gradlew installDist` must work from a fresh copy.
    - Each starter contains:
      - the package layout;
      - a `main` that parses the CLI arguments (or starts the server) and calls `TODO`s;
      - 2–3 example unit tests that show the testing style, one of them failing;
      - any provided data files.

      No solution logic.
  - `check/Check.java`: one single-file Java 26 program, run with `java check/Check.java <path-to-launcher> [--stage N]`.
    - The launcher is the learner's `build/install/<app>/bin/<app>` (from `installDist`).
    - It drives the program through its CLI and stdin/stdout, through files in a temp directory, or over 127.0.0.1 sockets/HTTP when it's a server. It starts the server on a free port and always stops it.
    - It prints one line per check: `PASS stage 2 · imports a CSV with quoted commas`, or `FAIL stage 2 · … expected: … got: …`. Then a summary, then exit code 0 or 1.
    - `--stage N` runs stages 1..N, so each milestone can be checked as soon as it's done.
    - Messages must teach. Show the exact input, the expected output and the actual output, and give a hint for the likely cause when you can.
    - It must be deterministic. Timing checks need generous bounds and must never flake. Use no libraries beyond the JDK.
  - `solution_kotlin/` and `solution_java/`: complete reference solutions, both passing every stage.
    - Idiomatic, tested (unit tests included), commented only where it teaches.
    - They demonstrate the senior rubric.
  - `bugs/`: at least 4 seeded-bug variants. Each is a small patch description plus the modified file(s), applied to a copy of a solution. Each one must make at least one checker stage FAIL with a helpful message.
    - These prove the checker catches the classic mistakes.
    - Name them after the mistake: `off_by_one_month_boundary/`, `float_money/`, ….
  - Data files the project needs, kept small (≤ 2 MB total). Public-domain or generated with a seeded generator that you keep in `mN/tools/`.
- Gradle hygiene: delete `build/`, `.gradle/`, `.kotlin/` and `.idea/` from everything under `W/projects/` before you finish.

## The page (match the course's look; see L45 for a long build page)

1. **Kicker:** "Mini project N · after L<NN> · <phase name> · <a href="../reference/roadmap.html#projects">all projects</a>".
2. **Top:** h1, a subtitle, `.lesson-meta` (an honest total like "~6–8 h over 3–5 sittings"; "stop cleanly after any milestone"; `#resume`), a `.win` box and a `.toc` with `data-part` per milestone.
3. **What you'll use:** a table of the lessons it integrates, with links and the one skill each contributes.
4. **Setup:**
   - Copy a starter into `~/katas/mN/`, `git init` on a branch.
   - Build with `./gradlew installDist`.
   - Run the checker with the exact commands.
   - AI completion off.
5. **The spec:** precise behaviour, with input/output formats, examples, edge cases and error messages. Exact enough that the checker's expectations never surprise.
6. **Milestones:** 3–6, each a `part-head` with an id (`m1`, `m2`, …). Each has:
   - **Goal:** what to build, plus the design decision to think about first.
   - **Hints:** 2–3 progressive `<details>` blocks (nudge → approach → near-answer). No full code.
   - **Done when:** the checker command for that stage, plus the unit tests the learner should have written.
   - A `label.done` checkbox.
7. **Senior review:** a checklist the learner applies to their own code before calling it done. Make it specific to this project: invariants, naming, error handling, tests that would catch the seeded bugs, complexity, thread safety. Then the instruction to land it as a pull request on their own repo, as in L40, and to ask the teacher to review the diff.
8. **Stretch goals:** 3–5, each with one line on what it teaches.
9. **Reference solutions:** a short section linking `mN/solution_kotlin/` and `mN/solution_java/`, saying to open them only after the checker is green. Include 3–5 "things to compare" with your own solution.
10. **End matter:** `.rating` rows (Design, Tests, the chosen language), "Build my results", `.ask`, and `.lesson-nav` prev/next in study order (given in your prompt).

## Verify (all of it, and report the outputs)

- **Checker vs each solution:** run it against each solution's `installDist` launcher → all stages PASS.
- **Checker vs each seeded bug:** ≥ 1 FAIL each, with the message shown in your report.
- **Checker vs each starter:** stage 1 FAILs with a message that tells the learner what to do first. It must not crash with a stack trace.
- **Unit tests:** each solution's unit tests pass (`./gradlew test`).
- **Browser:** the page has no errors and no horizontal scroll at 420 px (`cdp.mjs` against `http://127.0.0.1:8790/projects/mN-slug.html`). `qa.py` only checks `lessons/`, so you check the quiz rules on any project quiz by eye.
- **Ports:** confirm no leftover listening ports.
