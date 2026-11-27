L40 · Code Review & Team Git: how this lesson is built

Rebuild the lesson (no programs need re-running):
  cd .build/l40 && python3 fill.py            # tpl_src.html + captures/ -> ../tpl/0040.html
  cd ../bin && python3 build.py ../tpl/0040.html ../../lessons/0040-code-review-and-team-git.html
fill.py replaces <!--OUT:name--> markers with exact, HTML-escaped slices of captures/*.txt.
Edit tpl_src.html, never ../tpl/0040.html (it is generated).

What was run (all real; captures/ holds the raw output):
  gr_kotlin/, gr_java/   the AI-written BookCatalog (6 planted bugs) + its test + the reviewer's tests
                         (./gradlew test --tests books.BookCatalogTest  -> green; ./gradlew test -> 4 red)
  fixed/                 the fixed BookCatalog, copied into a copy of gr_* for the green run
  CacheRace.kt/.java     bug 5 race demo (run with .build/bin/run.sh; has a 5 s watchdog)
  err/                   invented-API compile errors (kotlinc / javac)
  diff_base/             the pre-AI BookCatalog used to produce the git diff
  bisect/                Checkout.java + check.sh for the git bisect demo
  ci/                    the Part 3 workflows (YAML parsed with ruby -ryaml; GitHub-hosted run not run)
  final/start, steps/    the final's starting repo and per-commit file snapshots (gen_steps.py makes steps/)
  final/end/             git archive of the reference run's final main
  final/test_all.sh      byte-identical copy of final/start/test-all.sh (paths here can't contain '-')
  scripts/*.sh           the throwaway-repo transcript scripts. They expect a working folder L40W laid out as:
                         L40W/{diff_base,gr_kotlin,gr_java,fix_kotlin,ci,final/{start,steps}} and
                         L40W/src/{CacheRace.*,err/,bisect/}; lib.sh fixes author/dates (reproducible hashes)
                         and sets GIT_CONFIG_GLOBAL=/dev/null so no global git config is read or written.

v6 additions (2026-11):
  Part 3 "Branching and releases" (part id "rel"; CI stays id "p3" but is labelled Part 4, so saved progress still matches).
  scripts/release_demo.sh  tag / hotfix / push-tags transcript -> captures/release_out.txt
                           (bash scripts/release_demo.sh L40W > captures/release_out.txt 2>&1; L40W needs only scripts/lib.sh;
                           it sets HOME to L40W/home, so no personal git config is read)
  release/Toggle.kt/.java  feature-flag example, output in captures/toggle_out.txt (run with ../bin/run.sh)
  practice/{p1,p2,rel,p3}  in-page practices; cq/ code-quiz snippets. Verify: python3 ../bin/pg_check.py ../l40

v7 additions (2026-11):
  ai/before, ai/after      the "AI diff of a test file" for Part 1's "Working with AI as a senior" (rate change 25 -> 30;
                           the AI also dropped the cap, weakened one test, @Disabled another, added a non-existent dependency).
                           ai/RunTests.kt is a tiny reflective runner used to check which tests pass (kotlinc + junit-jupiter-api 6.0.1).
  scripts/ai_demo.sh       bash scripts/ai_demo.sh WORKDIR ai > captures/ai_out.txt
  Trims: SemVer details -> recap pointing at L25; changelog shortened; practice/rel (next version) cut;
         Part 2's autosquash practice (p2) labelled optional.
