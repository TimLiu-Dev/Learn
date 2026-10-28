"""Turn tpl_src.html into ../tpl/0046.html by pasting real captures and code excerpts in.

Markers (all paths relative to this folder):
  <!--OUT:captures/x.txt-->               whole file, HTML-escaped
  <!--OUT:captures/x.txt@21-26,58-->       only those 1-based lines; "…" between gaps
  <!--OUT:captures/x.txt~regex-->          only lines matching the regex
  <!--SRC:cap_kotlin/app/.../X.kt@10-40--> a code block with those lines of a real source file
  <!--VIZDATA-->                          loadviz spec built from captures/kt_load.txt
Whole files still use build.py's <!--CODE:l46/...--> marker.
Then run: cd ../bin && python3 build.py ../tpl/0046.html ../../lessons/0046-make-it-production-shaped.html
"""
import html
import json
import re
from pathlib import Path

HERE = Path(__file__).parent
LANG = {".kt": "kotlin", ".java": "java", ".sh": "bash", ".sql": "sql"}


def lines_of(path):
    return (HERE / path).read_text().replace("\r", "").rstrip("\n").split("\n")


def pick(spec):
    if "@" in spec:
        path, ranges = spec.split("@", 1)
        lines = lines_of(path)
        out, last = [], None
        for part in ranges.split(","):
            a, _, b = part.partition("-")
            a, b = int(a), int(b or a)
            if last is not None and a != last + 1:
                out.append("…")
            out.extend(lines[a - 1:b])
            last = b
        return "\n".join(out)
    if "~" in spec:
        path, rx = spec.split("~", 1)
        return "\n".join(l for l in lines_of(path) if re.search(rx, l))
    return "\n".join(lines_of(spec))


def src(spec):
    path = spec.split("@", 1)[0]
    text = pick(spec)
    # remove the common indentation, so an excerpt from inside a class starts at column 0
    lines = text.split("\n")
    indent = min((len(l) - len(l.lstrip()) for l in lines if l.strip() and l != "…"), default=0)
    text = "\n".join(l[indent:] if l != "…" else l for l in lines)
    lang = LANG[Path(path).suffix]
    return f'<pre><code class="language-{lang}">{html.escape(text, quote=False)}</code></pre>'


def viz():
    text = (HERE / "captures/kt_load.txt").read_text()
    rows = {}
    for line in text.splitlines():
        m = re.match(r"(before|after) \(.*?\)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+(\d+)\s+(\d+)\s+(\S+)\s+(\d+)\s+(\d+)", line)
        if m:
            rows[m.group(1)] = [float(m.group(i)) for i in (2, 3, 4, 5, 6)] + [int(m.group(8))]
    series = {m.group(1): json.loads(m.group(2)) for m in re.finditer(r"SERIES (\w+) (\[.*\])", text)}
    labels = ["p50 (ms)", "p95 (ms)", "p99 (ms)", "requests/s", "DB reads", "429s"]
    summary = [{"label": l, "before": rows["before"][i], "after": rows["after"][i]} for i, l in enumerate(labels)]
    for s in summary:
        for k in ("before", "after"):
            if float(s[k]).is_integer():
                s[k] = int(s[k])
    notes = {
        "n": "In the after run, the greedy client's 1,000 requests all finish in the first half-second: the limiter answers "
             "most of them at once with 429. In the before run, the same 1,000 requests compete for the database for about "
             "3 seconds, which is why its traffic runs higher early on. After that, the 16 paced clients set the pace in both.",
        "p95": "The before run went first, on a cold JVM, so its first half-second is slow while the JIT warms up. After that, "
               "the tail of both runs sits around 4 to 7 ms: misses and refetched list pages.",
        "r429": "All of the after run's 429s land in the first half-second: the greedy client spends its 100-token burst plus a "
                "few refills, and everything else it sends is refused. The paced clients, at about 40 requests a second, never hit "
                "their 50-a-second limit.",
    }
    return json.dumps({"summary": summary, "runs": [{"name": k, "series": series[k]} for k in ("before", "after")], "notes": notes})


s = (HERE / "tpl_src.html").read_text()
s = re.sub(r"<!--OUT:([^>]+?)-->", lambda m: html.escape(pick(m.group(1)), quote=False), s)
s = re.sub(r"<!--SRC:([^>]+?)-->", lambda m: src(m.group(1)), s)
s = s.replace("<!--VIZDATA-->", viz())
assert "<!--OUT:" not in s and "<!--SRC:" not in s and "<!--VIZDATA-->" not in s
(HERE.parent / "tpl/0046.html").write_text(s)
print("wrote ../tpl/0046.html")
