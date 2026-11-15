# v6 check #2: independent review

Each lesson was upgraded by a builder following `.build/rules/v6-rules.md`; read that first, because its tools, file layouts, rebuild rules and quiz rules all apply here. You did NOT build these lessons, and that's the point. Read each lesson the way the learner will, before they start studying, and make it right.

Course root: `/Users/tl/Documents/Learns/CS Fundamentals` (always use absolute paths). Builders' unfixed findings are in `.build/v6/findings.md`. Fix the ones for your lessons that are definite problems.

## Per lesson

1. **Machine checks first.**
   - `python3 .build/bin/pg_check.py .build/lNN` must end with `0 problem(s)`.
   - `qa.py --strict` must say OK.
   - If either fails, fix the cause.
2. **Read the whole built lesson top to bottom.** Read the template/source alongside it for line references. Check:
   - **Facts and code.** Is any claim wrong, outdated or uncited? Does each example's printed output match its source? Re-run any listing you doubt: `java File.java`, or kotlinc in `.build/tools/kotlinc/bin`.
   - **Every quiz.**
     - Is the marked answer actually right?
     - Is any distractor also defensible? If so, reword it.
     - Does the question give the answer away?
     - Does every `whyopts` item say something true and specific about its option?
     - Do new code-quiz explanations match the snippet's real output (the `.out` file)?
   - **Every practice.**
     - Is the task text unambiguous?
     - Do the starter and the tests agree on names and signatures?
     - Do the hints lead to the answer without giving it away until hint 3?
     - Does `answer.html` explain the solution and the common mistake truthfully?
     - Does the Java version do the same task?
     - Is it the right size, 5–10 minutes?
   - **Flow.**
     - Do new sections fit where they sit?
     - Do cross-references (Lnn) point to the right lessons, in study order (see `reference/roadmap.html`)?
     - Are there broken anchors, or a recall card or primary-source box that misses a new section the builder added?
   - **Times.** The `lesson-meta` total must match the toc. Set it to `~S–S+15 min`, where S is the toc sum rounded to the nearest 5. Many lessons had this off before v6.
3. **Fix definite problems** in the true source (template, `tpl_src.html`, a generator, or practice/cq files), then rebuild with the lesson's recipe. Diff the new build against a backup you took first: only your changes may appear. Keep the voice, and change only what's wrong.
4. **Browser:** one practice end to end per lesson, using `node .build/bin/cdp.mjs` against `http://127.0.0.1:8790/…`. Use a different practice from the one the builder tested (its report is in findings or NOTES), with no page errors.

Don't restructure lessons (no splitting parts, no renumbering) unless your brief says to. Don't edit shared files (`assets/*`, `.build/bin/*`, the roadmap, other lessons); report what they need.

## Report (under 250 words per lesson)

- What you fixed and why (source file and line).
- What you judged fine, despite a finding in `findings.md`.
- Anything still open.
- The `pg_check` and `qa` lines.
