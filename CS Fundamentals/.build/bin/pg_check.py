"""Verify practice folders (see build.py practice_block) and code-quiz snippets.

usage: python3 pg_check.py .build/l8            # every practice/* and cq/* under that lesson folder
       python3 pg_check.py .build/l8/practice/p1
Practice, Kotlin (on the real Playground server, via pgapi.py):
  Solution.kt + Tests.kt compiles and passes every test (at least 3 tests);
  each optional Mistake*.kt (a wrong answer the hints/answer warn about) fails at least one test;
  Starter.kt + Tests.kt fails at least one test, or fails to compile with errors only in Starter.kt (a "make it compile" task).
Practice, Java (local JDK): Solution.java prints only PASS lines (at least 3) and exits 0;
  Starter.java prints a FAIL line or exits non-zero; both share the same // CHECKS … // END CHECKS block.
SQL practice (folder has setup.sql; checked with Python's sqlite3 the way playground.js checks in the browser):
  Solution.sql returns rows; Starter.sql and each Mistake*.sql give a different result or an error.
Code quiz: cq/Name.kt runs on the Playground, cq/Name.java on the local JDK, cq/Name.sql in sqlite3 (rows as a|b); stdout must equal Name.out exactly.
Exit status 1 if anything fails.
"""
import re
import sqlite3
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import pgapi  # noqa: E402

problems = []


def bad(where, msg):
    problems.append(f"{where}: {msg}")
    print(f"  ✘ {msg}")


def java(path):
    with tempfile.TemporaryDirectory() as tmp:   # run from an empty folder so nothing local is picked up
        p = subprocess.run(["java", str(path.resolve())], capture_output=True, text=True, timeout=60, cwd=tmp)
    return p.returncode, p.stdout, p.stderr


def checks_block(src):
    m = re.search(r"// CHECKS\n(.*?)// END CHECKS", src, re.S)
    return m.group(1) if m else None


def run_sql(setup, statements, check=""):
    """Like playground.js runSql: the result of the last statement that returned rows (or of the check query)."""
    db = sqlite3.connect(":memory:")
    try:
        db.executescript(setup)
        last = ([], [])
        for script in (statements, check):
            buf = ""
            for line in script.splitlines(keepends=True):
                buf += line
                if sqlite3.complete_statement(buf):
                    cur = db.execute(buf)
                    if cur.description:
                        last = ([c[0] for c in cur.description], cur.fetchall())
                    buf = ""
        return last
    finally:
        db.close()


def same_sql(a, b, ordered):
    if len(a[0]) != len(b[0]):
        return False
    ra, rb = [list(r) for r in a[1]], [list(r) for r in b[1]]
    return ra == rb if ordered else sorted(map(repr, ra)) == sorted(map(repr, rb))


def sql_practice(d):
    need = ["task.html", "setup.sql", "Starter.sql", "Solution.sql", "hints.html", "answer.html"]
    missing = [f for f in need if not (d / f).exists()]
    if missing:
        return bad(d, f"missing {missing}")
    setup = (d / "setup.sql").read_text()
    check = (d / "check.sql").read_text() if (d / "check.sql").exists() else ""
    ordered = bool(re.search(r"\border\s+by\b", check or (d / "Solution.sql").read_text(), re.I))
    try:
        want = run_sql(setup, (d / "Solution.sql").read_text(), check)
    except sqlite3.Error as e:
        return bad(d, f"Solution.sql fails: {e}")
    if not want[1]:
        bad(d, "Solution.sql returns no rows: nothing to compare against")
    else:
        print(f"  ✔ Solution.sql returns {len(want[1])} row(s){' (ordered)' if ordered else ''}")
    for f in ["Starter.sql"] + sorted(p.name for p in d.glob("Mistake*.sql")):
        try:
            got = run_sql(setup, (d / f).read_text(), check)
        except sqlite3.Error as e:
            print(f"  ✔ {f} errors ({e}), so it fails")
            continue
        if same_sql(got, want, ordered):
            bad(d, f"{f} already gives the expected result")
        else:
            print(f"  ✔ {f} gives a different result, so it fails")


def practice(d):
    print(f"practice {d}")
    if (d / "setup.sql").exists():
        return sql_practice(d)
    need = ["task.html", "Starter.kt", "Tests.kt", "Solution.kt", "Starter.java", "Solution.java", "hints.html", "answer.html"]
    missing = [f for f in need if not (d / f).exists()]
    if missing:
        return bad(d, f"missing {missing}")
    if not (d / "task.html").read_text().strip().split("\n")[0].strip():
        bad(d, "task.html needs a title on its first line")
    hints = [h for h in re.split(r"(?m)^---\s*$", (d / "hints.html").read_text()) if h.strip()]
    if len(hints) < 2:
        bad(d, f"{len(hints)} hints; want 2–3")
    tests = (d / "Tests.kt").read_text()
    sol = pgapi.test((d / "Solution.kt").read_text(), tests)
    if not sol["ok"]:
        bad(d, f"Solution.kt doesn't compile: {sol['errors'][:3]}")
    else:
        failing = [t for t in sol["tests"] if t[1] != "OK"]
        if failing:
            bad(d, f"Solution.kt fails tests: {failing}")
        if len(sol["tests"]) < 3:
            bad(d, f"only {len(sol['tests'])} tests")
        else:
            print(f"  ✔ Solution.kt passes {len(sol['tests'])} tests")
    st = pgapi.test((d / "Starter.kt").read_text(), tests)
    if not st["ok"]:
        # Allowed on purpose (e.g. "make this compile"), but only if every error is in the learner's file:
        # an error inside the hidden tests would be unreadable in the page.
        hidden = [e for e in st["errors"] if not e.startswith("File.kt:")]
        if hidden:
            bad(d, f"Starter.kt breaks the hidden tests: {hidden[:3]}")
        else:
            print(f"  ✔ Starter.kt fails to compile in the learner's file (intended?): {st['errors'][:2]}")
    elif all(t[1] == "OK" for t in st["tests"]):
        bad(d, "Starter.kt already passes every test")
    else:
        print(f"  ✔ Starter.kt compiles and fails {sum(t[1] != 'OK' for t in st['tests'])} test(s)")
    for m in sorted(d.glob("Mistake*.kt")):   # optional: the common mistake answer.html warns about
        r = pgapi.test(m.read_text(), tests)
        if r["ok"] and all(t[1] == "OK" for t in r["tests"]):
            bad(d, f"{m.name} passes every test: the checks don't catch that mistake")
        else:
            print(f"  ✔ {m.name} is caught by the checks")
    s_src, j_src = (d / "Starter.java").read_text(), (d / "Solution.java").read_text()
    if checks_block(s_src) is None or checks_block(s_src) != checks_block(j_src):
        bad(d, "Starter.java and Solution.java need the same // CHECKS … // END CHECKS block")
    if not re.search(r"\bclass Practice\b", s_src + j_src):
        bad(d, "Java class must be named Practice (the learner saves it as Practice.java)")
    code, out, err = java(d / "Solution.java")
    lines = [l for l in out.splitlines() if l.startswith(("PASS", "FAIL"))]
    if code != 0 or any(l.startswith("FAIL") for l in lines) or len(lines) < 3:
        bad(d, f"Solution.java: exit {code}, output {out[-400:]!r} {err[-400:]!r}")
    else:
        print(f"  ✔ Solution.java prints {len(lines)} PASS lines")
    code, out, err = java(d / "Starter.java")
    if code == 0 and "FAIL" not in out:
        bad(d, "Starter.java already passes")
    elif "error:" in err and "compilation failed" in err:
        bad(d, f"Starter.java doesn't compile: {err[:400]}")
    else:
        print("  ✔ Starter.java compiles and fails")


def code_quiz(f):
    print(f"code quiz {f}")
    exp = f.with_suffix(".out")
    if not exp.exists():
        return bad(f, "missing .out file with the exact expected output")
    want = exp.read_text()
    if f.suffix == ".kt":
        r = pgapi.run(f.read_text())
        if not r["ok"]:
            return bad(f, f"doesn't compile on the Playground: {r['errors'][:3]}")
        if r["exc"] or r["err"]:
            return bad(f, f"throws or writes stderr ({r['exc'] or r['err'][:200]}); catch it and print instead")
        got = r["out"]
    elif f.suffix == ".sql":   # rows of every row-returning statement, printed like the sqlite3 CLI: a|b, NULL empty
        db, got, buf = sqlite3.connect(":memory:"), "", ""
        try:
            for line in f.read_text().splitlines(keepends=True):
                buf += line
                if sqlite3.complete_statement(buf):
                    for row in db.execute(buf).fetchall():
                        got += "|".join("" if v is None else str(v) for v in row) + "\n"
                    buf = ""
        except sqlite3.Error as e:
            return bad(f, f"SQLite error: {e}")
    else:
        code, got, err = java(f)
        if code != 0 or err:
            return bad(f, f"exit {code}, stderr {err[:300]}")
    if got != want:
        return bad(f, f"output {got!r} != expected {want!r}")
    print("  ✔ output matches")


for arg in sys.argv[1:]:
    p = Path(arg)
    if (p / "Tests.kt").exists() or (p / "setup.sql").exists():
        practice(p)
        continue
    for d in sorted((p / "practice").glob("*/")):
        practice(d)
    for f in sorted(x for x in (p / "cq").glob("*.*") if x.suffix in (".kt", ".java", ".sql")):
        code_quiz(f)

print(f"\n{len(problems)} problem(s)")
for x in problems:
    print(" -", x)
sys.exit(1 if problems else 0)
