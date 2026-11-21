"""List the not-covered (red) and partly-covered (yellow) lines of a few classes in a JaCoCo HTML report."""
import html
import re
import sys
from pathlib import Path

FILES = ["shelf.http/Router.java", "shelf.service/LendingService.java", "shelf.http/Dtos.java"]
for f in FILES:
    s = (Path(sys.argv[1]) / (f + ".html")).read_text()
    print(f.split("/")[1])
    for m in re.finditer(r'<span class="(nc|pc|fc)(?: (bpc|bnc|bfc))?" id="L(\d+)"(?: title="([^"]*)")?>(.*?)</span>', s):
        cls, br, line, title, code = m.groups()
        if cls == "fc" and br in (None, "bfc"):
            continue
        what = "not run" if cls == "nc" else (title or "partly run")
        print(f"  {line:>3}  {what:<26} {html.unescape(re.sub('<[^>]+>', '', code)).strip()}")
