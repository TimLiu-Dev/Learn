"""Generate the btreeviz specs for L35 and write them into the template.

Usage: python3 btreeviz_spec.py ../tpl/0035.html
A toy B+-tree with 3 entries per leaf and up to 4 children per interior node,
bulk-loaded from sorted keys. Every step is derived from the data, so the
diagram can't disagree with itself. It has 3 levels, like the real
idx_loans_member on 200,000 rows (dbstat in Part 2).
"""
import json
import re
import sys

LEAF, FAN, PAGE_ROWS = 3, 4, 4


def build(entries):
    """entries: sorted list of (key_tuple, label, rowid). Returns nodes, root, leaf ids."""
    nodes, leaves = {}, []
    for i in range(0, len(entries), LEAF):
        lid = f"L{len(leaves)}"
        chunk = entries[i:i + LEAF]
        nodes[lid] = {"entries": [{"k": lab, "r": r} for _, lab, r in chunk], "min": chunk[0][0],
                      "max": chunk[-1][0], "maxlab": chunk[-1][1], "keys": [e[0] for e in chunk]}
        leaves.append(lid)
    for a, b in zip(leaves, leaves[1:]):
        nodes[a]["next"] = b
    level, depth = leaves, 0
    while len(level) > 1:
        depth += 1
        up = []
        for i in range(0, len(level), FAN):
            kids = level[i:i + FAN]
            nid = f"N{depth}_{len(up)}"
            nodes[nid] = {"kids": kids, "min": nodes[kids[0]]["min"], "max": nodes[kids[-1]]["max"],
                          "maxlab": nodes[kids[-1]]["maxlab"],
                          "seps": [nodes[k]["max"] for k in kids[:-1]],
                          "lab": [nodes[k]["maxlab"] for k in kids[:-1]]}
            up.append(nid)
        level = up
    return nodes, level[0], leaves


def label(t):
    return str(t[0]) if len(t) == 1 else "(" + ",".join(map(str, t)) + ")"


def descend(nodes, root, target):
    """Path from root to the first leaf that can hold keys starting with >= target.
    Each separator is the largest key in the child to its left, so we take the
    first child whose separator is >= target (prefix compare), else the last child."""
    path, n = [root], root
    while "kids" in nodes[n]:
        kids, seps = nodes[n]["kids"], nodes[n]["seps"]
        i = 0
        while i < len(seps) and seps[i][:len(target)] < target:
            i += 1
        n = kids[i]
        path.append(n)
    return path


def public(nodes):
    out = {}
    for k, v in nodes.items():
        if "kids" in v:
            out[k] = {"keys": ["≤" + v["lab"][i] for i in range(len(v["seps"]))], "kids": v["kids"]}
        else:
            out[k] = {"entries": v["entries"], **({"next": v["next"]} if "next" in v else {})}
    return out


def member_spec():
    rows = [(r, (r * 7) % 12 + 1) for r in range(1, 25)]           # (rowid, member_id): each member twice
    entries = sorted(((m, r), f"{m}·r{r}", f"r{r}") for r, m in rows)   # SQLite keys: (member_id, rowid)
    nodes, root, leaves = build(entries)
    pages = [[{"r": f"r{r}", "t": f"r{r} m={m}"} for r, m in rows[i:i + PAGE_ROWS]]
             for i in range(0, len(rows), PAGE_ROWS)]
    page_of = {f"r{r}": i // PAGE_ROWS for i, (r, _) in enumerate(rows)}
    modes = []

    # 1. lookup member_id = 7
    target = 7
    path = descend(nodes, root, (target,))
    steps, hl, reads = [], [], 0
    for n in path:
        hl.append(n); reads += 1
        what = "the root" if n == root else ("an interior page" if "kids" in nodes[n] else "a leaf page")
        steps.append({"nodes": list(hl), "now": [n], "reads": reads,
                      "note": f"Read {what}. " + ("Compare 7 with its separator keys and follow one pointer down." if "kids" in nodes[n]
                              else "Leaf entries are sorted: find the entries for 7.")})
    leaf, ents, rowhits = path[-1], [], []
    while True:
        for i, e in enumerate(nodes[leaf]["entries"]):
            if nodes[leaf]["keys"][i][0] == target:
                ents.append(f"{leaf}:{i}"); rowhits.append(e["r"])
        nxt = nodes[leaf].get("next")
        if nxt and nodes[nxt]["keys"][0][0] == target:
            leaf = nxt; hl.append(leaf); reads += 1
        else:
            break
    steps.append({"nodes": list(hl), "now": [], "entries": list(ents), "reads": reads,
                  "note": f"Found {len(ents)} entries for member 7: rowids {', '.join(rowhits)}. That took {reads} page reads."})
    pg = sorted({page_of[r] for r in rowhits})
    steps.append({"nodes": list(hl), "entries": list(ents), "rows": rowhits, "pages": pg, "reads": reads + len(pg),
                  "note": f"Fetch those rows from the table by rowid: {len(pg)} more page reads. Total {reads + len(pg)}, whether the table has 24 rows or 200,000 (only the tree's height grows, logarithmically)."})
    modes.append({"name": "Lookup m = 7", "steps": steps})

    # 2. full scan
    steps, hits = [], []
    for p, page in enumerate(pages):
        hits += [c["r"] for c in page if c["t"].endswith(" m=7")]
        steps.append({"pages": list(range(p + 1)), "nowPage": p, "rows": list(hits), "reads": p + 1,
                      "note": f"No index: read table page {p + 1} of {len(pages)} and check every row." +
                              (f" Found {len(hits)} so far." if hits else "")})
    steps[-1]["note"] += f" Every page read: cost grows with the table (O(n))."
    modes.append({"name": "Full scan", "steps": steps})

    # 3. range 5 <= m <= 8
    lo, hi = 5, 8
    path = descend(nodes, root, (lo,))
    steps, hl, reads = [], [], 0
    for n in path:
        hl.append(n); reads += 1
        steps.append({"nodes": list(hl), "now": [n], "reads": reads,
                      "note": "Descend once, to the first leaf that can hold 5." if n == path[-1] else "Descend: compare 5 with the separators."})
    leaf, ents = path[-1], []
    while leaf:
        for i, e in enumerate(nodes[leaf]["entries"]):
            if lo <= nodes[leaf]["keys"][i][0] <= hi:
                ents.append(f"{leaf}:{i}")
        done = nodes[leaf]["keys"][-1][0] >= hi
        steps.append({"nodes": list(hl), "now": [leaf], "entries": list(ents), "reads": reads,
                      "note": ("Past 8: stop. " if done else "Walk right along the linked leaves. ") + f"Matches so far: {len(ents)}. Page reads: {reads}."})
        nxt = nodes[leaf].get("next")
        if done or not nxt:
            break
        leaf = nxt; hl.append(leaf); reads += 1
    modes.append({"name": "Range 5–8", "steps": steps})
    return {"nodes": public(nodes), "root": root, "table": {"label": f"loans table · {PAGE_ROWS} rows per page", "pages": pages}, "modes": modes}


def composite_spec():
    books = [(b, (b * 5) % 6 + 1, 1980 + (b * 7) % 40) for b in range(1, 25)]     # (id, author_id, year)
    entries = sorted(((a, y), label((a, y)), f"b{b}") for b, a, y in books)
    nodes, root, leaves = build(entries)
    modes = []

    # good: author_id = 3 AND year > 2000
    path = descend(nodes, root, (3,))
    steps, hl, reads = [], [], 0
    for n in path:
        hl.append(n); reads += 1
        steps.append({"nodes": list(hl), "now": [n], "reads": reads,
                      "note": "Sorted by author first: descend straight to author 3." if n != path[-1] else "Entries for author 3 sit together, sorted by year."})
    leaf, ents = path[-1], []
    while leaf:
        for i, _ in enumerate(nodes[leaf]["entries"]):
            a, y = nodes[leaf]["keys"][i]
            if a == 3 and y > 2000:
                ents.append(f"{leaf}:{i}")
        done = nodes[leaf]["keys"][-1][0] > 3
        steps.append({"nodes": list(hl), "now": [leaf], "entries": list(ents), "reads": reads,
                      "note": ("Reached author 4: stop. " if done else "Keep walking while author = 3. ") + f"Matches so far: {len(ents)}. Page reads: {reads}."})
        nxt = nodes[leaf].get("next")
        if done or not nxt:
            break
        leaf = nxt; hl.append(leaf); reads += 1
    modes.append({"name": "author = 3 AND year > 2000", "steps": steps})

    # bad: year > 2000 only
    steps, ents, hl = [], [], []
    for k, leaf in enumerate(leaves):
        hl.append(leaf)
        for i, _ in enumerate(nodes[leaf]["entries"]):
            if nodes[leaf]["keys"][i][1] > 2000:
                ents.append(f"{leaf}:{i}")
        steps.append({"nodes": list(hl), "now": [leaf], "entries": list(ents), "reads": k + 1,
                      "note": f"Years aren't sorted on their own: matches are scattered across every leaf. Read leaf {k + 1} of {len(leaves)}." +
                              (" The tree can't help: this is a scan." if k == len(leaves) - 1 else "")})
    modes.append({"name": "year > 2000 only", "steps": steps})
    return {"nodes": public(nodes), "root": root, "modes": modes}


def main(tpl):
    text = open(tpl).read()
    for sid, spec in (("bt-member", member_spec()), ("bt-composite", composite_spec())):
        block = f'<script type="application/json" id="{sid}">{json.dumps(spec, separators=(",", ":"))}</script>'
        pat = re.compile(rf'<script type="application/json" id="{sid}">.*?</script>', re.S)
        assert pat.search(text), f"placeholder for {sid} missing"
        text = pat.sub(lambda m: block, text)
    open(tpl, "w").write(text)
    print("wrote specs into", tpl)


if __name__ == "__main__":
    main(sys.argv[1])
