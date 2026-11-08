"""Turns lesson.src.html into ../tpl/0057.html (L57).
<!--OUT:file--> becomes the escaped capture out/file (minus its "===== file" header line);
<!--OUT:file:a-b--> keeps only lines a..b (1-based, after the header);
<!--RAFTSPEC--> becomes out/raft_frames.json (from `./gradlew run --args=--json` in gr_java).
<!--CODE:...--> markers are left for build.py."""
import html, json, re, pathlib
here = pathlib.Path(__file__).parent

def out(m):
    name, rng = m.group(1), m.group(2)
    ls = here.joinpath("out", name).read_text().splitlines()
    if ls and ls[0].startswith("====="): ls = ls[1:]
    if rng:
        a, b = map(int, rng.split("-")); ls = ls[a - 1:b]
    return html.escape("\n".join(ls), quote=False)

src = here.joinpath("lesson.src.html").read_text()
src = re.sub(r"<!--OUT:([\w.]+)(?::(\d+-\d+))?-->", out, src)
spec = json.dumps(json.loads(here.joinpath("out", "raft_frames.json").read_text()), separators=(",", ":"))
src = src.replace("<!--RAFTSPEC-->", spec.replace("</", "<\\/"))
assert "<!--OUT:" not in src and "<!--RAFTSPEC-->" not in src
here.joinpath("..", "tpl", "0057.html").write_text(src)
print("wrote ../tpl/0057.html")
