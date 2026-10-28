#!/usr/bin/env python3
"""Builds ../tpl/0044.html from tpl_src.html.

<!--OUT:captures/x.txt--> is replaced by the HTML-escaped contents of that real capture,
so every terminal output on the page is byte-for-byte what a run printed.
Then build.py injects <!--CODE:...--> sources as usual:

    python3 fill.py && cd ../bin && python3 build.py ../tpl/0044.html ../../lessons/0044-a-worked-design.html
"""
import html, re
from pathlib import Path

here = Path(__file__).parent
src = (here / "tpl_src.html").read_text()

def out(m):
    return html.escape((here / m.group(1)).read_text().rstrip("\n"), quote=False)

text = re.sub(r"<!--OUT:([\w/.]+)-->", out, src)
assert "<!--OUT:" not in text
(here.parent / "tpl" / "0044.html").write_text(text)
print("wrote ../tpl/0044.html")
