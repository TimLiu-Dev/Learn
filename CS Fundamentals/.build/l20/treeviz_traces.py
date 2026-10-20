"""Generate the treeviz specs for L20 and write them into ../tpl/0020.html.

The algorithms below mirror the lesson's Kotlin/Java code step for step, and every
snapshot that the real programs print is asserted against their captured output
(Contains, Delete, Traversals, IntMinHeap). Run: python3 treeviz_traces.py
"""
import json
import re
from pathlib import Path

TPL = Path(__file__).resolve().parent.parent / "tpl" / "0020.html"


class N:
    def __init__(self, key):
        self.key, self.left, self.right = key, None, None


def insert(n, key):
    if n is None:
        return N(key)
    if key < n.key:
        n.left = insert(n.left, key)
    elif key > n.key:
        n.right = insert(n.right, key)
    return n


def build(keys):
    root = None
    for k in keys:
        root = insert(root, k)
    return root


def nest(n):
    return None if n is None else [n.key, nest(n.left), nest(n.right)]


def shape(n):  # same picture as Delete.kt's shape()
    if n is None:
        return "-"
    if n.left is None and n.right is None:
        return str(n.key)
    return f"{n.key}({shape(n.left)} {shape(n.right)})"


def S(tree, note, cur=(), seen=(), ok=(), dele=(), out=None):
    st = {"t": nest(tree), "note": note, "cur": list(cur), "seen": list(seen), "ok": list(ok), "del": list(dele)}
    if out is not None:
        st["out"] = out
    return st


# ── Part 1: search and insert ────────────────────────────────────────────────
def search_mode(key):
    root = build([50, 30, 70, 20, 40, 60, 80])
    steps = [S(root, f"Search for <b>{key}</b>. Start at the root. Every comparison throws away one whole subtree.")]
    seen, cur, path = [], root, []
    while cur is not None:
        path.append(cur.key)
        if key < cur.key:
            steps.append(S(root, f"{key} &lt; {cur.key}: anything smaller lives on the <b>left</b>. The right subtree is never looked at.", [cur.key], seen))
            seen.append(cur.key)
            cur = cur.left
        elif key > cur.key:
            steps.append(S(root, f"{key} &gt; {cur.key}: go <b>right</b>. The left subtree is skipped entirely.", [cur.key], seen))
            seen.append(cur.key)
            cur = cur.right
        else:
            steps.append(S(root, f"{key} == {cur.key}: <b>found</b> after {len(path)} comparisons, one per level.", [cur.key], seen, [cur.key]))
            return steps, path, True
    steps.append(S(root, f"The next link is <code>null</code>: an empty subtree. {key} is <b>not in the tree</b>, and we only touched {len(path)} of 7 nodes.", (), seen))
    return steps, path, False


def insert_mode(key):
    root = build([50, 30, 70, 20, 40, 60, 80])
    steps = [S(root, f"Insert <b>{key}</b>: walk down exactly as if searching for it.")]
    seen, cur = [], root
    while True:
        go_left = key < cur.key
        nxt = cur.left if go_left else cur.right
        side = "left" if go_left else "right"
        rel = "&lt;" if go_left else "&gt;"
        if nxt is None:
            steps.append(S(root, f"{key} {rel} {cur.key}, and {cur.key}'s {side} link is <code>null</code>: that's the spot.", [cur.key], seen))
            seen.append(cur.key)
            insert(root, key)
            steps.append(S(root, f"Hang a new leaf there. Nothing else moves: insert is O(h), the length of one path.", (), seen, [key]))
            return steps
        steps.append(S(root, f"{key} {rel} {cur.key}: go {side}.", [cur.key], seen))
        seen.append(cur.key)
        cur = nxt


s60, p60, f60 = search_mode(60)
s45, p45, f45 = search_mode(45)
assert (p60, f60) == ([50, 70, 60], True)      # Contains: "loop 60: 50 70 60 true"
assert (p45, f45) == ([50, 30, 40], False)     # Contains: "loop 45: 50 30 40 false"
tv_search = {"modes": [{"name": "contains 60", "steps": s60}, {"name": "contains 45", "steps": s45},
                       {"name": "insert 65", "steps": insert_mode(65)}]}


# ── Part 1: delete, three cases (continuing from each other, like Delete.kt) ──
root = build([50, 30, 70, 20, 40, 60, 80, 65])
assert shape(root) == "50(30(20 40) 70(60(- 65) 80))"

leaf = [S(root, "Case 1: delete <b>20</b>. Find it first.", [50]),
        S(root, "20 &lt; 30: left.", [30], [50]),
        S(root, "20 has <b>no children</b>. Its parent's link just becomes <code>null</code> (the recursive call returns <code>node.right</code>, which is null).", [20], [50, 30], dele=[20])]
root.left.left = None
leaf.append(S(root, "Gone. <code>50(30(- 40) 70(60(- 65) 80))</code>, exactly what <code>Delete.kt</code> printed.", ok=[30]))
assert shape(root) == "50(30(- 40) 70(60(- 65) 80))"

one = [S(root, "Case 2: delete <b>60</b>, which has exactly one child (65).", [50]),
       S(root, "60 &gt; 50: right.", [70], [50]),
       S(root, "60 &lt; 70: left. Found 60. Its only child is 65.", [60], [50, 70], dele=[60]),
       S(root, "Hand the child up: the recursive call returns 65, and 70's left link now points at it. The whole 65 subtree moves up one level.", [65], [70], dele=[60])]
root.right.left = root.right.left.right
one.append(S(root, "<code>50(30(- 40) 70(65 80))</code>. Still a valid BST, because everything under 60 was already between 50 and 70.", ok=[65]))
assert shape(root) == "50(30(- 40) 70(65 80))"

two = [S(root, "Case 3: delete <b>50</b>, the root. It has <b>two</b> children, so no single child can take its place.", [50], dele=[50]),
       S(root, "Find the <b>in-order successor</b>: the smallest key bigger than 50. Step right once…", [70], [50], dele=[50]),
       S(root, "…then left as far as possible. 65 has no left child, so 65 is the successor. (The predecessor, 40, would work too.)", [65], [50, 70], dele=[50])]
root.key = 65
two.append(S(root, "Copy 65 into the root. For a moment 65 appears twice: the BST order still holds, because 65 is the smallest key on the right.", [65], ok=[]))
two.append(S(root, "Now delete the <em>old</em> 65 from the right subtree. It has at most one child (a successor never has a left child), so this is case 1 or 2.", [70], dele=[]))
root.right.left = None
two.append(S(root, "<code>65(30(- 40) 70(- 80))</code>, matching <code>Delete.kt</code>. Every case was O(h).", ok=[65]))
assert shape(root) == "65(30(- 40) 70(- 80))"
tv_delete = {"modes": [{"name": "1 · leaf (20)", "steps": leaf}, {"name": "2 · one child (60)", "steps": one},
                       {"name": "3 · two children (50)", "steps": two}]}


# ── Part 2: traversals ───────────────────────────────────────────────────────
troot = build([50, 30, 70, 20, 40, 60, 80])
why = {"pre": "node, then left, then right", "in": "left, then node, then right", "post": "left, then right, then node"}


def dfs_order(n, kind, acc):
    if n is None:
        return
    if kind == "pre":
        acc.append(n.key)
    dfs_order(n.left, kind, acc)
    if kind == "in":
        acc.append(n.key)
    dfs_order(n.right, kind, acc)
    if kind == "post":
        acc.append(n.key)


def trav_mode(kind):
    order = []
    dfs_order(troot, kind, order)
    steps = [S(troot, f"<b>{kind}-order</b> visits {why[kind]}, recursively at every node.", out="")]
    for i, k in enumerate(order):
        steps.append(S(troot, f"Visit {k}.", [k], (), order[:i + 1], out=" ".join(map(str, order[:i + 1]))))
    return order, steps


def level_mode():
    steps = [S(troot, "<b>level-order</b>: a FIFO queue. Start with the root in it. Queue: [50]", out="")]
    queue, order = [troot], []
    while queue:
        n = queue.pop(0)
        order.append(n.key)
        for c in (n.left, n.right):
            if c is not None:
                queue.append(c)
        q = ", ".join(str(x.key) for x in queue)
        steps.append(S(troot, f"Take {n.key} from the front, enqueue its children at the back. Queue: [{q}]", [n.key], (), order[:], out=" ".join(map(str, order))))
    return order, steps


orders = {}
modes = []
for kind in ("pre", "in", "post"):
    orders[kind], st = trav_mode(kind)
    modes.append({"name": f"{kind}-order", "steps": st})
orders["level"], st = level_mode()
modes.append({"name": "level-order", "steps": st})
assert orders == {"pre": [50, 30, 20, 40, 70, 60, 80], "in": [20, 30, 40, 50, 60, 70, 80],      # Traversals.kt
                  "post": [20, 40, 30, 60, 80, 70, 50], "level": [50, 30, 70, 20, 40, 60, 80]}
modes[1]["steps"][-1]["note"] += " In-order on a BST is <b>sorted</b>."
modes[2]["steps"][-1]["note"] += " The root comes <b>last</b>: children are finished before their parent."
tv_trav = {"modes": modes}


# ── Part 3: heap add / poll ──────────────────────────────────────────────────
def H(h, note, cur=(), seen=(), ok=(), dele=()):
    return {"h": h[:], "note": note, "cur": list(cur), "seen": list(seen), "ok": list(ok), "del": list(dele)}


heap, add_steps, after_add = [], [H([], "An empty min-heap. Rule: every parent ≤ its children. Shape: a complete tree, filled level by level, left to right, so it fits in an array with no gaps.")], []
for x in [5, 1, 8, 3, 2, 9]:
    heap.append(x)
    i = len(heap) - 1
    add_steps.append(H(heap, f"add({x}): put it at the <b>end</b> of the array (index {i}). The shape stays complete.", [i]))
    while i > 0 and heap[i] < heap[(i - 1) // 2]:
        p = (i - 1) // 2
        add_steps.append(H(heap, f"{heap[i]} &lt; its parent {heap[p]} at index ({i}-1)/2 = {p}: swap (sift up).", [i, p]))
        heap[i], heap[p] = heap[p], heap[i]
        i = p
    add_steps.append(H(heap, f"{x} is in place. Array: {heap}", (), (), [i]))
    after_add.append(heap[:])
assert after_add == [[5], [1, 5], [1, 5, 8], [1, 3, 8, 5], [1, 2, 8, 5, 3], [1, 2, 8, 5, 3, 9]]   # IntMinHeap.kt

poll_steps, after_poll = [H(heap, "The heap after the six adds: <code>[1, 2, 8, 5, 3, 9]</code>, the same array <code>println(pq)</code> showed. Now poll twice.")], []
for _ in range(2):
    top = heap[0]
    poll_steps.append(H(heap, f"poll(): the answer is the root, {top}. But removing the root leaves a hole.", [0], dele=[0]))
    last = heap.pop()
    heap[0] = last
    poll_steps.append(H(heap, f"Move the <b>last</b> element ({last}) into the root. The shape stays complete, but the order is broken.", [0]))
    i = 0
    while True:
        l = 2 * i + 1
        if l >= len(heap):
            poll_steps.append(H(heap, f"{heap[i]} has no children: done.", (), (), [i]))
            break
        c = l + 1 if l + 1 < len(heap) and heap[l + 1] < heap[l] else l
        if heap[i] <= heap[c]:
            poll_steps.append(H(heap, f"{heap[i]} ≤ its smaller child {heap[c]}: done. Array: {heap}", (), (), [i]))
            break
        poll_steps.append(H(heap, f"Children at {l}" + (f" and {l + 1}" if l + 1 < len(heap) else "") + f": the smaller is {heap[c]}. {heap[i]} &gt; {heap[c]}, so swap (sift down).", [i, c]))
        heap[i], heap[c] = heap[c], heap[i]
        i = c
    after_poll.append((top, heap[:]))
assert after_poll == [(1, [2, 3, 8, 5, 9]), (2, [3, 5, 8, 9])]   # IntMinHeap.kt: "poll 1 -> …", "poll 2 -> …"
tv_heap = {"modes": [{"name": "add 5, 1, 8, 3, 2, 9", "steps": add_steps}, {"name": "poll × 2", "steps": poll_steps}]}


# ── write into the template ──────────────────────────────────────────────────
tpl = TPL.read_text()
for name, spec in {"tv-search": tv_search, "tv-delete": tv_delete, "tv-trav": tv_trav, "tv-heap": tv_heap}.items():
    pat = re.compile(rf'(<script type="application/json" id="{name}">).*?(</script>)', re.S)
    assert pat.search(tpl), name
    tpl = pat.sub(lambda m: m.group(1) + json.dumps(spec, ensure_ascii=False) + m.group(2), tpl)
TPL.write_text(tpl)
print("treeviz specs written; all snapshots match the captured program output")
