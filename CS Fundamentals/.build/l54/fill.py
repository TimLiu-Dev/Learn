# Builds tpl/0054.html from l54/tpl_src.html:
#   <!--OUT:name-->  -> the escaped contents of l54/out/name.txt (real captured output)
#   <!--PVSPEC-->    -> the parseviz spec, produced by running the real Java pipeline (VizDump.java)
# Run from .build/: python3 l54/fill.py && cd bin && python3 build.py ../tpl/0054.html ../../lessons/0054-how-languages-work.html
import html, re, subprocess
from pathlib import Path

L = Path(__file__).resolve().parent
PROGRAMS = ["let x = 3; x * (x + 1)", "1 + 2 * 3", "8 - 3 - 2", "2 ^ 3 ^ 2", "-2 ^ 2"]
spec = subprocess.run(["java", "VizDump.java", *PROGRAMS], cwd=L, capture_output=True, text=True, check=True).stdout.strip()
(L / "viz.json").write_text(spec + "\n")
assert "</script" not in spec

s = (L / "tpl_src.html").read_text()
s = re.sub(r"<!--OUT:([\w.]+)-->", lambda m: html.escape((L / "out" / f"{m.group(1)}.txt").read_text().rstrip("\n"), quote=False), s)
s = s.replace("<!--PVSPEC-->", spec)
assert "<!--OUT:" not in s and "<!--PVSPEC" not in s
(L.parent / "tpl/0054.html").write_text(s)
print("tpl/0054.html written")
