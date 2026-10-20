# Generates the two gcviz diagrams for L24 and writes them into ../tpl/0024.html.
# Run from .build/l24:  python3 gcviz_specs.py   (then rebuild the lesson with build.py)
import copy, json, re, pathlib

TPL = pathlib.Path(__file__).parent.parent / "tpl" / "0024.html"


def step(note, nodes, edges):
    return {"note": note, "nodes": copy.deepcopy(nodes), "edges": [list(e) for e in edges]}


# ── Mode 1: mark and sweep ──────────────────────────────────────────────
C0, C1, C2, C3 = 8, 150, 280, 410
R0, R1, R2, R3 = 20, 75, 130, 185
n = {
    "rOrder": {"x": C0, "y": R0, "label": "local order", "cls": "root"},
    "rLine":  {"x": C0, "y": R1, "label": "local line", "cls": "root"},
    "rCfg":   {"x": C0, "y": R3, "label": "CONFIG", "cls": "root"},
    "order":  {"x": C1, "y": R0, "label": "Order #2"},
    "cust":   {"x": C2, "y": R0, "label": "Customer"},
    "items":  {"x": C2, "y": R1, "label": "List<Item>"},
    "line":   {"x": C1, "y": R1, "label": "\"line 7\""},
    "old":    {"x": C3, "y": R1, "label": "Order #1"},
    "a":      {"x": C1, "y": R2, "label": "Node A"},
    "b":      {"x": C2, "y": R2, "label": "Node B"},
    "cfg":    {"x": C1, "y": R3, "label": "Config"},
    "set":    {"x": C2, "y": R3, "label": "Settings"},
}
e = [("rOrder", "order"), ("order", "cust"), ("order", "items"), ("rLine", "line"),
     ("old", "cust"), ("a", "b"), ("b", "a"), ("rCfg", "cfg"), ("cfg", "set")]

mark = []
mark.append(step("Three <b>GC roots</b> (orange): two local variables in <code>main</code>'s stack frame (L4) and a <code>static</code> field, <code>CONFIG</code>. Every arrow is a reference. Before a collection, the GC knows nothing about which objects are still needed.", n, e))
e1 = [x for x in e if x != ("rLine", "line")]
mark.append(step("The loop moves on and <code>line</code> now refers to something else (here: nothing). Nobody is pointing at <code>\"line 7\"</code> any more, but the object is still sitting in the heap.", n, e1))
n2 = copy.deepcopy(n)
for k in ("order", "cfg"):
    n2[k]["cls"] = "marked"
mark.append(step("<b>Mark, part 1.</b> The GC starts at every root and marks what each one refers to directly: <code>Order #2</code> and <code>Config</code>.", n2, e1))
for k in ("cust", "items", "set"):
    n2[k]["cls"] = "marked"
mark.append(step("<b>Mark, part 2.</b> It follows every arrow out of a marked object, and so on, until nothing new is found. <code>Customer</code>, <code>List&lt;Item&gt;</code> and <code>Settings</code> are reachable, so they are <b>live</b>.", n2, e1))
for k in ("line", "old", "a", "b"):
    n2[k]["cls"] = "dead"
mark.append(step("Everything left unmarked is <b>unreachable</b>, so it is garbage. Two surprises: <code>Order #1</code> points <em>at</em> a live object, but that doesn't help it, because only references <em>into</em> an object count. And <code>Node A</code> ↔ <code>Node B</code> refer to each other, yet nothing reachable refers to either, so the whole cycle goes.", n2, e1))
n3 = {k: v for k, v in n2.items() if v.get("cls") != "dead"}
e3 = [x for x in e1 if x[0] in n3 and x[1] in n3]
mark.append(step("<b>Sweep.</b> The unmarked objects' memory is reclaimed. A real collector then usually <b>compacts</b>: it slides the survivors together so the free space is one big block, which keeps allocation a cheap pointer bump.", n3, e3))

# ── Mode 2: generations ────────────────────────────────────────────────
LX = 112                       # lanes start right of the roots column
X0, X1, X2 = 128, 258, 388
EDEN, SURV, OLD = (0, 96), (102, 70), (178, 82)
lanes = [{"label": "young · eden", "x": LX, "y": EDEN[0], "h": EDEN[1]},
         {"label": "young · survivor", "x": LX, "y": SURV[0], "h": SURV[1]},
         {"label": "old", "x": LX, "y": OLD[0], "h": OLD[1]}]
ey0, ey1, sy, oy = 22, 60, SURV[0] + 22, OLD[0] + 26
g = {
    "rCart":  {"x": 4, "y": ey0, "label": "local cart", "cls": "root"},
    "rCache": {"x": 4, "y": oy, "label": "static cache", "cls": "root"},
    "cart":   {"x": X0, "y": ey0, "label": "Cart", "cls": "new"},
    "item":   {"x": X1, "y": ey0, "label": "Item", "cls": "new"},
    "s1":     {"x": X2, "y": ey0, "label": "String", "cls": "new"},
    "cat":    {"x": X0, "y": ey1, "label": "Catalog", "cls": "new"},
    "it1":    {"x": X1, "y": ey1, "label": "Iterator", "cls": "new"},
    "tmp":    {"x": X2, "y": ey1, "label": "StringBuilder", "cls": "new"},
}
ge = [("rCart", "cart"), ("cart", "item"), ("rCache", "cat")]
gen = []
gen.append(step("Every new object is allocated in <b>eden</b>, part of the young generation. Most of these are temporaries: a <code>String</code> built for a log line, an <code>Iterator</code> for one loop, a <code>StringBuilder</code>.", g, ge))
g1 = copy.deepcopy(g)
for k in ("cart", "item", "cat"):
    g1[k]["cls"] = "marked"
for k in ("s1", "it1", "tmp"):
    g1[k]["cls"] = "dead"
gen.append(step("Eden is full, so a <b>minor collection</b> runs. It marks from the roots but only cares about young objects, which is why it's fast: its cost is proportional to the <em>live</em> objects, and most young objects are already dead.", g1, ge))
g2 = {k: v for k, v in copy.deepcopy(g1).items() if v["cls"] != "dead"}
g2["cart"].update(y=sy, cls="live", tag="age 1")
g2["item"].update(y=sy, cls="live", tag="age 1")
g2["cat"].update(x=X2, y=sy, cls="live", tag="age 1")
gen.append(step("The survivors are <b>copied</b> into a survivor space and get age 1. Eden is now completely empty, with no sweeping needed: copying the few live objects out <em>is</em> the collection, and they end up packed together.", g2, ge))
g3 = copy.deepcopy(g2)
g3.update({
    "ord": {"x": X0, "y": ey0, "label": "Order", "cls": "new"},
    "s2":  {"x": X1, "y": ey0, "label": "String", "cls": "new"},
    "it2": {"x": X2, "y": ey0, "label": "Iterator", "cls": "new"},
})
ge3 = ge + [("cart", "ord")]
gen.append(step("The program keeps allocating into the empty eden. The cart now refers to a new <code>Order</code>.", g3, ge3))
g4 = copy.deepcopy(g3)
for k in ("cart", "item", "cat", "ord"):
    g4[k]["cls"] = "marked"
for k in ("s2", "it2"):
    g4[k]["cls"] = "dead"
gen.append(step("A second minor collection. <code>Cart</code>, <code>Item</code> and <code>Catalog</code> survive again, and so does the new <code>Order</code>.", g4, ge3))
g5 = {k: v for k, v in copy.deepcopy(g4).items() if v["cls"] != "dead"}
g5["cart"].update(y=oy, cls="live", tag="promoted")
g5["item"].update(y=oy, cls="live", tag="promoted")
g5["cat"].update(y=oy, cls="live", tag="promoted")
g5["ord"].update(y=sy, cls="live", tag="age 1")
gen.append(step("Objects that keep surviving are <b>promoted</b> to the old generation. In this toy the threshold is 2. HotSpot adapts it at run time, up to the <code>-XX:MaxTenuringThreshold</code> cap (15 by default). Long-lived things like caches end up here.", g5, ge3))
g6 = copy.deepcopy(g5)
g6["cart"].update(cls="dead", tag="waits for old GC")
g6["item"].update(cls="dead", tag="waits for old GC")
g6["ord"].update(cls="dead", tag="next minor GC")
ge6 = [x for x in ge3 if x != ("rCart", "cart")]
gen.append(step("The user empties the cart: <code>cart = null</code>. <code>Cart</code> and <code>Item</code> are garbage now, but they're in the old generation, which minor collections skip. They stay until an old-generation collection: in G1's log, that's the <code>Concurrent Mark Cycle</code> followed by <code>Mixed</code> pauses. (<code>Order</code> is unreachable too; the next minor collection reclaims it.)", g6, ge6))
specs = {
    "gc-mark": {"height": 230, "modes": [{"name": "Mark & sweep", "steps": mark}]},
    "gc-gen":  {"height": 262, "modes": [{"name": "Generations", "lanes": lanes, "steps": gen}]},
}

html = TPL.read_text()
for sid, spec in specs.items():
    body = json.dumps(spec, ensure_ascii=False, separators=(",", ":"))
    pat = re.compile(r'(<script type="application/json" id="%s">)(.*?)(</script>)' % sid, re.S)
    assert pat.search(html), sid
    html = pat.sub(lambda m: m.group(1) + body + m.group(3), html)
TPL.write_text(html)
print("wrote", ", ".join(specs), "into", TPL.name)
