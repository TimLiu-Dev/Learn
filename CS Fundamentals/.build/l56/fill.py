# Pastes captured outputs and widget data into the built L56 lesson.
#   <!--OUT:Name[@a-b]--> -> escaped out/Name.txt (optional 1-based inclusive line range)
#   <!--JSON:log--> / <!--JSON:lsm--> -> one mode each from viz/logviz.json
# Rebuild order (from .build/): l56/capture.sh (optional, re-runs everything),
#   python3 bin/build.py tpl/0056.html ../lessons/0056-storage-engines-and-other-data-models.html, python3 l56/fill.py
import html, json, re
from pathlib import Path

here = Path(__file__).resolve().parent
lesson = here.parent.parent / "lessons" / "0056-storage-engines-and-other-data-models.html"
s = lesson.read_text()

def out(m):
    name = m.group(1)
    lines = (here / "out" / (name + ".txt")).read_text().rstrip("\n").split("\n")
    if m.group(2):
        a, b = map(int, m.group(2).split("-"))
        lines = lines[a - 1:b]
    if name == "shelldb":                            # a terminal transcript: style the prompts
        return "\n".join('<span class="p">$ </span>' + html.escape(l[2:], quote=False) if l.startswith("$ ")
                         else html.escape(l, quote=False) for l in lines)
    return html.escape("\n".join(lines), quote=False)

modes = {m["kind"]: m for m in json.loads((here / "viz" / "logviz.json").read_text())["modes"]}

def spec(m):
    return json.dumps({"modes": [modes[m.group(1)]]}, ensure_ascii=False).replace("</", "<\\/")

s, n1 = re.subn(r"<!--OUT:([\w.]+)(?:@(\d+-\d+))?-->", out, s)
s, n2 = re.subn(r"<!--JSON:(\w+)-->", spec, s)
lesson.write_text(s)
print(f"{n1} outputs, {n2} widget specs pasted")
