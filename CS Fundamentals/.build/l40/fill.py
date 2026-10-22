"""Fill L40's template: replace <!--OUT:name--> markers with exact, HTML-escaped captures.

Usage: python3 fill.py   (reads tpl_src.html + captures/, writes ../tpl/0040.html)
Captures are real program/git output; this script only selects and escapes lines.
Terminal progress lines that use carriage returns keep only the final text, as a terminal shows it.
"""
import html, os, re

HERE = os.path.dirname(os.path.abspath(__file__))
CAP = os.path.join(HERE, "captures")


def load(name):
    text = open(os.path.join(CAP, name), encoding="utf-8", newline="").read()
    return [line.split("\r")[-1] for line in text.split("\n")]


def sec(name, start, end=None):
    lines = load(name)
    i = lines.index(start) + 1
    j = lines.index(end, i) if end else len(lines)
    out = lines[i:j]
    while out and not out[-1].strip():
        out.pop()
    return out


def gradle(name):
    """Test result lines, the exception line after each failure, the summary and the BUILD line."""
    lines, out = load(name), []
    for k, line in enumerate(lines):
        if re.search(r" > .* (PASSED|FAILED)$", line):
            out.append(line)
            if line.endswith("FAILED") and k + 1 < len(lines) and lines[k + 1].startswith("    "):
                out.append(lines[k + 1])
        elif re.match(r"\d+ tests completed|BUILD |exit code", line):
            out.append(line)
    return out


def code_lines(path, start_pat, end_pat):
    lines = open(os.path.join(HERE, path), encoding="utf-8").read().split("\n")
    i = next(k for k, l in enumerate(lines) if re.search(start_pat, l))
    j = next(k for k in range(i + 1, len(lines)) if re.search(end_pat, lines[k]))
    return lines[i:j + 1]


def term(lines):
    out = []
    for line in lines:
        if line.startswith("$ "):
            out.append('<span class="p">$ </span>' + html.escape(line[2:], quote=False))
        else:
            out.append(html.escape(line, quote=False))
    return "\n".join(out)


def raw(lines):
    return "\n".join(html.escape(l, quote=False) for l in lines)


def drop_cmd(lines):
    assert lines[0].startswith("$ ")
    return lines[1:]


S = {}
# ---- Part 1: diffs ----
S["diff_stat"] = term(sec("diff_out.txt", "=== STAT", "=== KT"))
S["diff_kt"] = raw(drop_cmd(sec("diff_out.txt", "=== KT", "=== KTTEST")))
S["diff_kttest"] = raw(drop_cmd(sec("diff_out.txt", "=== KTTEST", "=== JAVA")))
S["diff_jv"] = raw(drop_cmd(sec("diff_out.txt", "=== JAVA", "=== JAVATEST")))
S["diff_jvtest"] = raw(drop_cmd(sec("diff_out.txt", "=== JAVATEST", "=== WORKING")))
w = sec("diff_out.txt", "=== WORKING")
i_w = w.index("$ git diff -w")
S["diff_working_a"] = term(w[:i_w])
S["diff_working_b"] = term(w[i_w:])
S["kt_ai"] = term(gradle("kt_ai.txt"))
S["jv_ai"] = term(gradle("jv_ai.txt"))
S["kt_review"] = term(gradle("kt_review.txt"))
S["jv_review"] = term(gradle("jv_review.txt"))
S["kt_fixed"] = term(gradle("kt_fixed.txt"))
S["jv_fixed"] = term(gradle("jv_fixed.txt"))
S["kt_run"] = raw(load("kt_run.txt")[:2])
S["jv_run"] = raw(load("jv_run.txt")[:2])
S["kt_run_fixed"] = raw(load("kt_run_fixed.txt")[:2])
S["jv_run_fixed"] = raw(load("jv_run_fixed.txt")[:2])
S["race_kt"] = raw(sec("race_out.txt", "===== CacheRace.kt", "===== CacheRace.java"))
S["race_jv"] = raw(sec("race_out.txt", "===== CacheRace.java"))
hk = load("hall_kt.txt")
S["hall_kt"] = raw(hk[:3])
S["hall_jv"] = raw([l for l in load("hall_jv.txt") if l.strip()])
# ---- Part 2: history ----
for key, a, b in [("hist_before", "=== BEFORE", "=== MERGE"), ("hist_merge", "=== MERGE", "=== REBASE"),
                  ("hist_rebase", "=== REBASE", "=== FFONLY"), ("hist_ffonly", "=== FFONLY", "=== NOFF"),
                  ("hist_noff", "=== NOFF", "=== CONFLICT")]:
    S[key] = term(sec("history_out.txt", a, b))
c = sec("history_out.txt", "=== CONFLICT")
k_abort = c.index("$ git rebase --abort")
k_add = c.index("$ git add Greeting.kt")
S["hist_conflict"] = term(c[:k_abort])
S["hist_abort"] = term(c[k_abort:k_add])
S["hist_continue"] = term(c[k_add:])
for key, a, b in [("clean_amend", "=== AMEND", "=== FIXUP"), ("clean_fixup", "=== FIXUP", "=== SQUASH"),
                  ("clean_squash", "=== SQUASH", "=== REWORD"), ("clean_reword", "=== REWORD", "=== REFLOG"),
                  ("clean_reflog", "=== REFLOG", "=== STASH"), ("clean_stash", "=== STASH", "=== CHERRY"),
                  ("clean_cherry", "=== CHERRY", None)]:
    S[key] = term(sec("clean_out.txt", a, b))
S["bisect"] = term([l for l in load("bisect_out.txt") if l.strip()])
# ---- Part 3: CI ----
S["ci_out"] = term([l for l in load("ci_out.txt") if l.strip()])
red = load("kt_build_red.txt")
keep = [l for l in red if re.search(r" > .* FAILED$|tests completed|^\* What went wrong|^Execution failed|^> There were failing|BUILD |exit code", l)]
keep = [re.sub(r"file:///\S*/app/build/", "file:///…/app/build/", l) for l in keep]
S["ci_red"] = term(keep)
# ---- Final ----
for key, a, b in [("fin_start", "=== START", "=== BRANCH"), ("fin_branch", "=== BRANCH", "=== REVIEW"),
                  ("fin_review", "=== REVIEW", "=== FIXUP"), ("fin_fixup", "=== FIXUP", "=== TEAMMATE"),
                  ("fin_teammate", "=== TEAMMATE", "=== REBASE"), ("fin_rebase", "=== REBASE", "=== NAIVE"),
                  ("fin_naive", "=== NAIVE", "=== RESOLVE"), ("fin_resolve", "=== RESOLVE", "=== MERGE"),
                  ("fin_merge", "=== MERGE", "=== CI"), ("fin_ci", "=== CI", None)]:
    lines = [l for l in sec("final_out.txt", a, b) if not re.fullmatch(r"=== C\d", l)]
    S[key] = term(lines)
S["start_inline_kt"] = raw(code_lines("final/start/books_kt/app/src/main/kotlin/books/BookApi.kt",
                                      r"private fun create", r"^    }$"))
S["start_inline_jv"] = raw(code_lines("final/start/books_java/app/src/main/java/books/BookApi.java",
                                      r"private void create", r"^    }$"))
S["ci_yml"] = raw(open(os.path.join(HERE, "ci/ci.yml")).read().rstrip("\n").split("\n"))
S["ci_matrix_yml"] = raw(open(os.path.join(HERE, "ci/ci_matrix.yml")).read().rstrip("\n").split("\n"))
S["final_ci_yml"] = raw(open(os.path.join(HERE, "final/end/.github/workflows/ci.yml")).read().rstrip("\n").split("\n"))

src = open(os.path.join(HERE, "tpl_src.html"), encoding="utf-8").read()
used = set(re.findall(r"<!--OUT:(\w+)-->", src))
missing = used - S.keys()
assert not missing, f"unknown OUT markers: {missing}"
out = re.sub(r"<!--OUT:(\w+)-->", lambda m: S[m.group(1)], src)
dst = os.path.join(HERE, "..", "tpl", "0040.html")
open(dst, "w", encoding="utf-8").write(out)
print(f"filled {len(used)} markers -> {os.path.normpath(dst)}; unused: {sorted(S.keys() - used)}")
