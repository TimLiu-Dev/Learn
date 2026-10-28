"""L42: lesson.src.html -> ../tpl/0042.html.
<!--OUT:name--> becomes the HTML-escaped contents of captures/name.txt (real program output);
<!--SVSPEC--> becomes the scaleviz JSON from scaleviz_spec.py. Then run build.py as usual:
    python3 fill.py && cd ../bin && python3 build.py ../tpl/0042.html ../../lessons/0042-scaling-out.html
"""
import html, json, re
from pathlib import Path
import scaleviz_spec

here = Path(__file__).parent
src = (here / "lesson.src.html").read_text()
src = re.sub(r"<!--OUT:(\w+)-->",
             lambda m: html.escape((here / "captures" / f"{m.group(1)}.txt").read_text().rstrip("\n"), quote=False), src)
src = src.replace("<!--SVSPEC-->", json.dumps(scaleviz_spec.spec()))
assert "<!--OUT:" not in src and "<!--SVSPEC-->" not in src
(here.parent / "tpl" / "0042.html").write_text(src)
print("wrote ../tpl/0042.html")
