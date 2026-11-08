# Pastes captured outputs into the built lesson: <!--OUT:Name.kt--> -> escaped outs/Name.kt.txt
# Rebuild order (from .build/): l50/capture.sh (optional, re-runs everything), python3 l50/viz/inject.py,
#   python3 bin/build.py tpl/0050.html ../lessons/0050-pointers-backtracking-and-tries.html, python3 l50/fill.py
import html, re
from pathlib import Path

here = Path(__file__).resolve().parent
lesson = here.parent.parent / "lessons" / "0050-pointers-backtracking-and-tries.html"
s = lesson.read_text()

def out(m):
    name = m.group(1)
    text = (here / "outs" / (name + ".txt")).read_text().rstrip("\n")
    lines = text.split("\n")
    if m.group(2):                                   # optional @a-b line range (1-based, inclusive)
        a, b = map(int, m.group(2).split("-"))
        lines = lines[a - 1:b]
    return html.escape("\n".join(lines), quote=False)

s, n = re.subn(r"<!--OUT:([\w.]+)(?:@(\d+-\d+))?-->", out, s)
lesson.write_text(s)
print(f"{n} outputs pasted")
