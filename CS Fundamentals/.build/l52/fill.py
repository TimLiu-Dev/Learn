# Run after build.py: pastes files into the built lesson.
#   <!--RAW:l52/path-->   the file, HTML-escaped (code listings, captured test output)
#   <!--JSON:l52/path-->  the file as-is (widget specs inside <script type="application/json">)
# Rebuild: cd .build/bin && python3 build.py ../tpl/0052.html ../../lessons/0052-from-gates-to-instructions.html
#          && python3 ../l52/fill.py
import html, pathlib, re
B = pathlib.Path(__file__).resolve().parent.parent          # .build/
lesson = B.parent / "lessons" / "0052-from-gates-to-instructions.html"
s = lesson.read_text()
s = re.sub(r"<!--RAW:([\w/.]+)-->", lambda m: html.escape((B / m.group(1)).read_text().rstrip("\n"), quote=False), s)
s = re.sub(r"<!--JSON:([\w/.]+)-->", lambda m: (B / m.group(1)).read_text().strip(), s)
assert "<!--RAW:" not in s and "<!--JSON:" not in s and "<!--CODE:" not in s
lesson.write_text(s)
print("filled", lesson.name)
