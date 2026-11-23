# v7 rules: the senior-by-2027 pass (shared by every v7 builder)

The learner approved this on 2026-11-21, as the last pass before they start studying:

- **Fill gaps.** Fill the gaps found by a roadmap.sh check and a senior-engineer-in-2027 review of every lesson.
- **Two new core lessons.** L58 Software Architecture and L59 Shipping Safely.
- **Seven mini projects** between phases.
- **An Electives track** (E01–E28) for nice-to-have and niche topics.
- **Trims and outdated fixes.**

**The plan is `.build/v7/plan.md`; read it first.** Raw reviewer notes are in `.build/v7/reviews.md`. Your prompt names exactly which items are yours.

Course root, W: `/Users/tl/Documents/Learns/CS Fundamentals`. Always use absolute paths, because the folder name has a space.

**Everything in `.build/rules/v6-rules.md` and `.build/rules/v5-rules.md` still applies; read both.** That covers:

- the tools (`pg_check.py`, `build.py`, `qa.py --strict`, `cdp.mjs`, `run.sh`, kotlinc, `.build/libs`);
- the practice and code-quiz formats;
- `whyopts`;
- "everything verified";
- quiz rules;
- the lesson structure;
- don't edit shared files.

**Gold standard for style:** `.build/tpl/0008.html` with `lessons/0008-inheritance-and-polymorphism.html`, plus `lessons/0057-time-order-and-agreement.html` for a v5-era lesson.

## Names that now exist (link to them freely, even if not built yet)

- **L58 Software Architecture:** `0058-software-architecture.html`. Study order: after L40, before L41.
- **L59 Shipping Safely:** `0059-shipping-safely.html`. Study order: after L43, before L57.
- **Mini projects:** `../projects/mN-slug.html`.
  - M1 `m1-budget-cli`, after L15.
  - M2 `m2-search-engine`, after L51.
  - M3 `m3-performance-clinic`, after L25.
  - M4 `m4-job-runner`, after L29.
  - M5 `m5-chat-server`, after L55.
  - M6 `m6-ledger`, after L56.
  - M7 `m7-legacy-rescue`, after L58.
- **Electives:** `eNN-slug.html` in `lessons/`. The catalogue is in `.build/v7/plan.md` §9. Point to one where your lesson touches a topic it deepens ("Going deeper: E02 String Algorithms"), but only if it's relevant. One line, in the part's prose or the end matter, never a new section.

## A. Editing an existing lesson (lesson-edit builders)

1. **Back up, then find the recipe.**
   - Back up the built lesson and its template/source into your scratch folder: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad/v7/<your-id>/`.
   - Find the rebuild recipe (NOTES.md, or `.build/lNN/*.py` docstrings). Edit the true source: the template, `tpl_src.html`, `lesson.src.html`, a generator script, or practice/cq files. L1–L3 are hand-written with in-place regions.
2. **Additions.** Each plan item says roughly how many minutes it takes.
   - **A new teaching part (≥ ~20 min)** gets everything a v6 part has:
     - the explanation with citations;
     - Kotlin/Java `.pair` code that you actually ran, and real output;
     - an `.auto` box (or an honest note when the languages are the same);
     - an `.exercise` with `details.solution`;
     - a runnable practice (`.build/lNN/practice/<partid>/`, v6 §3);
     - a Check with 2+ quizzes, all with `whyopts`, and at least one of them a code quiz;
     - a `label.done` checkbox and a toc row.

     Use a new part id that keeps the existing ids unchanged (e.g. `p2b`), so learners' saved progress survives.
   - **A section inside an existing part (5–15 min).** Prose with citations, plus code if the idea is code: a run `.pair`, or one language with an honest note. Add 1–2 quizzes with `whyopts` to that part's Check; a code quiz is welcome. Add the minutes to that part's `<span class="t">`.
   - **A one-line bridge or a sentence:** just write it, with a citation if it's a fact.
   - **Optional deep dive:** label it with `<span class="deep-tag">Optional deep dive</span>`. No Check question, exit quiz or later lesson may depend on it. Its minutes are not counted in the part time; write "(+N min optional)" next to the time instead.
3. **Trims.**
   - **"Make optional":** add the deep-tag label, remove any core quiz or recall item that depends on it (or move it into the optional section), and take its minutes out of the core time.
   - **"Cut":** remove the section and everything that depends on it: practices (delete the folder), quizzes, recall-card lines, sheet references (report those; don't edit sheets), and toc rows.
   - Never leave a dangling "as you saw in …" reference.
4. **Outdated fixes.** Verify the current fact with a fetched official source (release notes, RFC, JEP, vendor docs) and cite it. Prefer wording that won't go stale ("JUnit 6.1 or newer", "current Spring Boot 4.x") over pinning a patch version, unless a build file needs the exact version.
5. **Times.**
   - Recompute each touched part's time, the toc, and `lesson-meta`.
   - `lesson-meta` is `~S–S+15 min`, where S is the toc sum (core only) rounded to the nearest 5.
   - Aim to keep core lessons ≤ ~185 min. If your additions push a lesson past that, make something else in it optional and say so in your report.
6. **Finish.** Run these in order:
   1. `pg_check.py .build/lNN` → `0 problem(s)`.
   2. Rebuild.
   3. Diff against your backup: only intended changes.
   4. `qa.py --strict`: your lesson is OK.
   5. Browser check of one new practice end to end, plus no errors and no horizontal scroll at 420 px.

## B. Hard limits for everyone

- **Don't edit shared files.** That means `assets/*`, `.build/bin/*`, `reference/*`, NOTES.md, RESOURCES.md, MISSION.md, other lessons' files, and nav links in other lessons. Report what they need: sheet lines, glossary terms, nav. I integrate the roadmap, nav, reference sheets and notes.
- **Don't spawn subagents or forks.** The session has an agent cap.
- **The Playground server is shared and free.** Never loop `pg_check` quickly; its cache makes re-runs cheap.
- **Servers bind to 127.0.0.1 and stop themselves.** Before finishing, check `lsof -iTCP -sTCP:LISTEN -n -P` and kill anything of yours. A course server already runs at `http://127.0.0.1:8790/`; leave it alone.
- **Don't install anything.** Use what's in `.build/tools` and `.build/libs` and the system JDK 26 / Gradle 9.7.1. If you truly need a new library jar, report it instead.
- **If you hit a usage limit and are resumed,** re-read your scratch notes and continue. Write progress notes to `.build/v7/notes/<your-id>.md` as you go, because the scratchpad can be wiped.

## Report (≤ 300 words)

- **Items:** each plan item → done / changed / skipped, with the reason.
- **Lessons and parts:** new part ids and practices, quizzes added (count, with `whyopts`), code quizzes.
- **Trims:** what was trimmed or made optional, and the minutes saved.
- **Times:** old vs new `lesson-meta`.
- **For me:** findings you didn't fix, unverified claims, and anything I must change in shared files (sheet lines, glossary, nav).
- **Check output:** the `pg_check` tail and the `qa` line.
