# v7 rules for NEW lessons: L58, L59 and the Electives E01–E28

Read `.build/rules/v7-rules.md` first, then `v5-rules.md` (how a new lesson is built and verified) and `v6-rules.md` (practices, code quizzes and `whyopts`). Everything in them applies unless this file says otherwise.

## Files

| | Core L58/L59 | Elective EKK |
|---|---|---|
| Built page | `lessons/00NN-slug.html` | `lessons/eKK-slug.html` |
| body | `data-lesson="lNN"` | `data-lesson="eKK"` |
| Template | `.build/tpl/00NN.html` | `.build/tpl/eKK.html` |
| Sources, practice, cq | `.build/lNN/` | `.build/eKK/` |
| Practice marker | `<!--PRACTICE:lNN/practice/p1-->` | `<!--PRACTICE:eKK/practice/p1-->` |

- **Build:** `cd "W/.build/bin" && python3 build.py ../tpl/<name>.html ../../lessons/<file>.html`. If you paste captured output with a script, keep it as `.build/<id>/fill.py` and give the rebuild order in your report.
- **Slugs** are given in `.build/v7/plan.md` (§2 for the core lessons, §9 for the electives).
- **Paths** must match `[\w/.]+`: no hyphens in folder or file names under `.build/` (use `gr_kotlin`).

## Shape

**Core L58/L59:** a full lesson in the v5 structure, ~150–175 min core. That means:

- the top matter;
- a warm-up interleaving earlier lessons;
- 3–4 parts, each with a practice, an exercise and a Check;
- a final exercise with `.rating` rows;
- an exit quiz, the results summary, a recall card, the primary source box, a reference-sheet line (say which sheet in your report), `.ask` and the nav.

  Study-order nav:

  - L58: prev L40, next M7 (`../projects/m7-legacy-rescue.html`).
  - L59: prev L43, next L57.
- Kicker:
  - L58: "Lesson 58 · Phase 11 Design & Clean Code".
  - L59: "Lesson 59 · Phase 12 System Design & Caching".
  - Both link to `../reference/roadmap.html#p11` / `#p12`.

**Electives:** shorter, **~60–90 min core**.

- **Top matter:**
  - **Kicker:** "Elective EKK · <track> · take after L<NN> · <a href="../reference/roadmap.html#electives">electives</a>".
  - **Below the kicker:** a `.note` saying it's optional, what it assumes, and that no core lesson depends on it.
- **Warm-up:** short, ~5 min, 2–3 quizzes recalling the prerequisite lessons.
- **2–3 parts.** Each has:
  - the explanation with citations;
  - verified Kotlin + Java code with real output;
  - an `.auto` box or an honest note;
  - one runnable practice;
  - a Check with 2–3 quizzes, all with `whyopts`. At least 2 code quizzes per elective in total, with at least one in Kotlin and one in Java.
- **Final exercise:** ~20 min, with `.rating` rows for KOTLIN and JAVA.
- **End matter:**
  - an exit quiz of 3–4 questions;
  - "Build my results";
  - a recall card;
  - a `.primary` source box;
  - `.ask`;
  - a `.lesson-nav` with prev = the "take after" lesson and next = `../reference/roadmap.html#electives` (label "All electives").
- **What it's for:** depth over breadth. Pick the 2–3 ideas a senior engineer would actually use or be asked about. Build at least one thing by hand (the course's signature) rather than survey a list. "Optional deep dive" labels are allowed inside electives too.
- **Practices must run on the Playground:** single file, Java 17 APIs, no network, no files, 10 s.
  - Where the real thing needs more (an LLM API, a container, FFM, the Vector API, tcpdump), the practice uses an honest in-memory model of it.
  - The IntelliJ exercise does the real thing on JDK 26, with real captured output.
- **Assets:** you may create ONE new asset, named `assets/<id>viz.js` (e.g. `e01viz.js`), following v5-rules. Prefer none.

## Facts and currency

Electives touch fast-moving topics: LLMs, KMP, Wasm, the Vector API, OpenTelemetry, post-quantum TLS.

- Verify every version or status claim against a fetched official source dated 2026, and cite it.
- Say "as of late 2026" where something is moving.
- Never invent API shapes. For LLM APIs, describe the common shape (HTTP POST of JSON messages; SSE streaming) without depending on one vendor. Any vendor example must be checked against its fetched docs.
- No API keys or paid services in any exercise. Use a local fake server for anything remote.

## Finish

As in v7-rules §A.6, but there's no backup or diff (the files are new):

1. `pg_check.py .build/<id>` → `0 problem(s)`.
2. Build.
3. `qa.py --strict` → your page is OK.
4. Browser check: one practice end to end, plus no errors and no horizontal scroll at 420 px.

Then report as in v7-rules, adding the sheet text you propose: 3–8 lines for the reference sheet named in your prompt (I'll merge it).
