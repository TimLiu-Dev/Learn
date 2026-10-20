# Regenerates the graphviz JSON specs inside tpl/0021.html from GraphTrace.java.
# Run from .build/: python3 l21/viz/inject.py
import json, re, subprocess

def gen(arg):
    out = subprocess.run(["java", "l21/viz/GraphTrace.java", arg], capture_output=True, text=True, check=True).stdout.strip()
    json.loads(out)
    assert "</script" not in out
    return out

f = "tpl/0021.html"
s = open(f).read()
for sid, arg in [("gv-search", "bfsdfs"), ("gv-dp", "dp")]:
    s, n = re.subn(r'(<script type="application/json" id="%s">).*?(</script>)' % sid,
                   lambda m: m.group(1) + gen(arg) + m.group(2), s, flags=re.S)
    assert n == 1, sid
open(f, "w").write(s)
print("specs injected")
