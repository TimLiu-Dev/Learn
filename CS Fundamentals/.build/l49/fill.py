# Builds tpl/0049.html from l49/tpl_src.html:
#   <!--OUT:name-->   -> the escaped contents of l49/out/name (real captured output)
#   <!--PVSPEC:k-->   -> the pathviz spec with only mode k, from viz/PathTrace.java
# Run from .build/: python3 l49/fill.py && cd bin && python3 build.py ../tpl/0049.html ../../lessons/0049-greedy-shortest-paths-and-union-find.html
import html, json, re, subprocess
from pathlib import Path

L = Path(__file__).resolve().parent
spec = json.loads(subprocess.run(["java", str(L / "viz/PathTrace.java")], capture_output=True, text=True, check=True).stdout)
(L / "viz/spec.json").write_text(json.dumps(spec))

def pv(k):
    one = {"nodes": spec["nodes"], "edges": spec["edges"], "modes": [spec["modes"][int(k)]]}
    out = json.dumps(one, ensure_ascii=False, separators=(",", ":"))
    assert "</script" not in out
    return out

s = (L / "tpl_src.html").read_text()
s = re.sub(r"<!--OUT:([\w.]+)-->", lambda m: html.escape((L / "out" / m.group(1)).read_text().rstrip("\n"), quote=False), s)
s = re.sub(r"<!--PVSPEC:(\d)-->", lambda m: pv(m.group(1)), s)
assert "<!--OUT:" not in s and "<!--PVSPEC:" not in s
(L.parent / "tpl/0049.html").write_text(s)
print("tpl/0049.html written")
