"""Check #3: does every code quiz mark the option that matches its snippet's real output?

For each quiz whose question holds a listing, find the cq/ snippet with that exact text, read its .out file, and
compare against the options: the marked option must be the one whose text best matches the output.
usage: python3 cq_answers.py   (prints problems; exit 1 if any)
"""
import html, re, sys
from pathlib import Path

W = Path(__file__).resolve().parents[2]
norm = lambda s: re.sub(r"\s+", " ", html.unescape(re.sub(r"<[^>]+>", "", s))).strip()   # HTML: drop tags, then unescape
plain = lambda s: re.sub(r"\s+", " ", s).strip()                                        # source files are raw text
snippets = {}
for f in W.glob(".build/l*/cq/*.*"):
    if f.suffix in (".kt", ".java", ".sql") and f.with_suffix(".out").exists():
        snippets[plain(f.read_text())] = (f, f.with_suffix(".out").read_text())

def score(opt, out):
    """How well an option describes the output: exact match beats containment beats token overlap."""
    o, t = norm(opt).replace(" / ", " ").replace(" ⏎ ", " "), plain(out)   # options may write line breaks as " / "
    o2 = re.sub(r"^(Prints|Gives|Returns)\s+", "", o)
    if o2 == t or o == t: return 3
    if t and (t in o2 or o2 in t) and len(o2) > 0: return 2
    a, b = set(o2.split()), set(t.split())
    return len(a & b) / max(1, len(a | b))

problems, checked = [], 0
for page in sorted((W / "lessons").glob("0*.html")):
    s = page.read_text()
    for m in re.finditer(r'<div class="quiz" data-id="([^"]+)"[^>]*data-answer="(\d+)"[^>]*>(.*?)<div class="opts">(.*?)</div>', s, re.S):
        qid, ans, q, opts = m.group(1), int(m.group(2)), m.group(3), m.group(4)
        code = re.search(r"<pre[^>]*><code[^>]*>(.*?)</code></pre>", q, re.S)
        if not code: continue
        hit = snippets.get(norm(code.group(1)))
        if not hit: continue
        checked += 1
        f, out = hit
        buttons = re.findall(r"<button>(.*?)</button>", opts, re.S)
        scores = [score(b, out) for b in buttons]
        best = max(range(len(scores)), key=lambda i: scores[i])
        if scores[ans] < 2 and best != ans or scores[best] > scores[ans]:
            problems.append(f"{page.name} {qid}: marked [{ans}] {norm(buttons[ans])!r}, but {f.name} prints {plain(out)!r}; best match [{best}] {norm(buttons[best])!r}")
print(f"{checked} code quizzes checked against their .out files ({len(snippets)} snippets with .out on disk)")
for p in problems: print(" -", p)
sys.exit(1 if problems else 0)
