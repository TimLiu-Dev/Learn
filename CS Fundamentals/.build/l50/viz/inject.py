# Regenerates the windowviz JSON specs inside tpl/0050.html from WindowTrace.java.
# Run from .build/: python3 l50/viz/inject.py
import json, re, subprocess

def gen(arg):
    out = subprocess.run(["java", "l50/viz/WindowTrace.java", arg], capture_output=True, text=True, check=True).stdout.strip()
    json.loads(out)
    assert "</script" not in out
    return out

f = "tpl/0050.html"
s = open(f).read()
for sid, arg in [("wv-pointers", "pointers"), ("wv-queens", "board"), ("wv-trie", "trie")]:
    s, n = re.subn(r'(<script type="application/json" id="%s">).*?(</script>)' % sid,
                   lambda m: m.group(1) + gen(arg) + m.group(2), s, flags=re.S)
    assert n == 1, sid
open(f, "w").write(s)
print("specs injected")
