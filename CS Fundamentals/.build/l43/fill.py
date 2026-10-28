"""Turns lesson.src.html into ../tpl/0043.html (L43).
<!--OUT:file--> becomes the escaped capture out/file (minus its "===== file" header line);
<!--OUT:file:a-b--> keeps only lines a..b (1-based, after the header);
<!--RESILSPEC--> becomes the widget spec built from the same captures.
<!--CODE:...--> markers are left for build.py."""
import html, re, pathlib, sys
here = pathlib.Path(__file__).parent
sys.path.insert(0, str(here))
import resilviz_spec

def out(m):
    name, rng = m.group(1), m.group(2)
    ls = here.joinpath("out", name).read_text().splitlines()
    if ls and ls[0].startswith("====="): ls = ls[1:]
    if rng:
        a, b = map(int, rng.split("-")); ls = ls[a - 1:b]
    return html.escape("\n".join(ls), quote=False)

src = here.joinpath("lesson.src.html").read_text()
src = re.sub(r"<!--OUT:([\w.]+)(?::(\d+-\d+))?-->", out, src)
src = src.replace("<!--RESILSPEC-->", resilviz_spec.spec().replace("</", "<\\/"))
assert "<!--OUT:" not in src and "<!--RESILSPEC-->" not in src
here.joinpath("..", "tpl", "0043.html").write_text(src)
print("wrote ../tpl/0043.html")
