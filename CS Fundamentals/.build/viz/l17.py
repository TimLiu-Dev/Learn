import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402


def node(i, value, nxt):
    return (f"n{i}", "Node", [("value", value), ("next", nxt)])


def arr(i, slots):
    return (f"a{i}", f"Array · capacity {len(slots)}", [(f"[{k}]", v) for k, v in enumerate(slots)])


def queue(arr_id, head, tail, size):
    return ("q", "MyQueue", [("items", f"@{arr_id}"), ("head", str(head)), ("tail", str(tail)), ("size", str(size))])


N = "null"

VIZ = {
    "l17_nodes": {
        "kotlin": "l17/Nodes.kt", "java": "l17/Nodes.java",
        "steps": [
            S(4, 9, "An empty list is just a <code>head</code> reference holding <code>null</code>. There are no nodes yet.",
              [("main", [("head", N)])]),
            S(5, 10, "<b>addFirst</b>: create a node whose <code>next</code> is the <em>old</em> head (null), then point <code>head</code> at it.",
              [("main", [("head", "@n1")])], [node(1, '"C"', N)]),
            S(6, 11, "Again: the new node <code>B</code> points at the old head <code>C</code>, and <code>head</code> moves to <code>B</code>. Nothing else moved: that's why it's <b>O(1)</b>, however long the list is.",
              [("main", [("head", "@n2")])], [node(2, '"B"', "@n1"), node(1, '"C"', N)]),
            S(7, 12, "Three nodes, scattered on the heap wherever there was room, held together only by their <code>next</code> references. To reach <code>C</code> you <em>must</em> walk from the head.",
              [("main", [("head", "@n3")])], [node(3, '"A"', "@n2"), node(2, '"B"', "@n1"), node(1, '"C"', N)]),
            S(8, 13, "<code>printAll</code> walks <code>cur = cur.next</code> until it hits <code>null</code>: <code>A -&gt; B -&gt; C -&gt; null</code>.",
              [("main", [("head", "@n3")])], [node(3, '"A"', "@n2"), node(2, '"B"', "@n1"), node(1, '"C"', N)]),
            S(9, 14, "<b>Unlink B</b>: copy B's <code>next</code> into A's <code>next</code>. A now skips straight to C. B still points at C, but <em>nothing points at B</em>.",
              [("main", [("head", "@n3")])], [node(3, '"A"', "@n1"), ("n2", "Node · unreachable", [("value", '"B"'), ("next", "@n1")]), node(1, '"C"', N)]),
            S(10, 15, "An unreachable object is garbage: no variable or field can ever get to it again, so the garbage collector reclaims it (L4). Output: <code>A -&gt; C -&gt; null</code>. Removing was O(1) <em>because we already held the node before it</em>.",
              [("main", [("head", "@n3")])], [node(3, '"A"', "@n1"), node(1, '"C"', N)]),
        ],
    },
    "l17_reverse": {
        "kotlin": "l17/Reverse.kt", "java": "l17/Reverse.java",
        "steps": [
            S(16, 17, "A three-node list: <code>1 → 2 → 3 → null</code>. <code>list</code> holds the head.",
              [("main", [("list", "@n1"), ("reversed", D)])],
              [node(1, "1", "@n2"), node(2, "2", "@n3"), node(3, "3", N)]),
            S(4, 5, "<code>reverse</code> gets a frame. <code>prev</code> starts at <code>null</code>: it will become the new list, built backwards.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", N), ("curr", D), ("next", D)])],
              [node(1, "1", "@n2"), node(2, "2", "@n3"), node(3, "3", N)]),
            S(5, 6, "<code>curr</code> starts at the head.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", N), ("curr", "@n1"), ("next", D)])],
              [node(1, "1", "@n2"), node(2, "2", "@n3"), node(3, "3", N)]),
            S(7, 8, "<b>Step 1: remember the rest.</b> <code>next</code> saves node 2 <em>before</em> we touch <code>curr.next</code>. Skip this and the rest of the list is lost.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", N), ("curr", "@n1"), ("next", "@n2")])],
              [node(1, "1", "@n2"), node(2, "2", "@n3"), node(3, "3", N)]),
            S(8, 9, "<b>Step 2: flip the arrow.</b> Node 1 now points backwards, at <code>prev</code> (null). Without <code>next</code>, nodes 2 and 3 would now be unreachable.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", N), ("curr", "@n1"), ("next", "@n2")])],
              [node(1, "1", N), node(2, "2", "@n3"), node(3, "3", N)]),
            S(9, 10, "<b>Step 3:</b> <code>prev</code> moves up to node 1. The reversed part is now <code>1 → null</code>.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", "@n1"), ("curr", "@n1"), ("next", "@n2")])],
              [node(1, "1", N), node(2, "2", "@n3"), node(3, "3", N)]),
            S(10, 11, "<b>Step 4:</b> <code>curr</code> moves to the saved <code>next</code>. One node done, two to go.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", "@n1"), ("curr", "@n2"), ("next", "@n2")])],
              [node(1, "1", N), node(2, "2", "@n3"), node(3, "3", N)]),
            S(10, 11, "Second time round the loop, the same four steps: save node 3, point node 2 back at node 1, advance both. Reversed part: <code>2 → 1 → null</code>.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", "@n2"), ("curr", "@n3"), ("next", "@n3")])],
              [node(1, "1", N), node(2, "2", "@n1"), node(3, "3", N)]),
            S(10, 11, "Third time: node 3 points back at node 2, and <code>curr</code> becomes <code>null</code>, so the loop ends.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", "@n3"), ("curr", N), ("next", N)])],
              [node(1, "1", N), node(2, "2", "@n1"), node(3, "3", "@n2")]),
            S(12, 13, "<code>prev</code> holds the old tail, which is the new head. Every arrow was flipped in place: O(n) time, O(1) extra memory, no new nodes.",
              [("main", [("list", "@n1"), ("reversed", D)]), ("reverse", [("head", "@n1"), ("prev", "@n3"), ("curr", N), ("next", N)])],
              [node(1, "1", N), node(2, "2", "@n1"), node(3, "3", "@n2")]),
            S(17, 18, "The frame is popped. <code>reversed</code> reads <code>3 → 2 → 1 → null</code>.",
              [("main", [("list", "@n1"), ("reversed", "@n3")])],
              [node(1, "1", N), node(2, "2", "@n1"), node(3, "3", "@n2")]),
            S(21, 21, "And <code>list</code> still points at node 1, which is now the <em>tail</em>: <code>list.next == null</code> prints <code>true</code>. Same objects, new arrows. In a list class with a tail pointer, <code>reverse</code> must also swap <code>head</code> and <code>tail</code>.",
              [("main", [("list", "@n1"), ("reversed", "@n3")])],
              [node(1, "1", N), node(2, "2", "@n1"), node(3, "3", "@n2")]),
        ],
    },
    "l17_ring": {
        "kotlin": "l17/ring/RingDemo.kt", "java": "l17/ring/RingDemo.java",
        "steps": [
            S(2, 3, "A queue backed by an array of 4 slots. <code>head</code> is where the next <code>poll</code> reads; <code>tail</code> is where the next <code>offer</code> writes.",
              [("main", [("q", "@q")])], [queue("a1", 0, 0, 0), arr(1, [N, N, N, N])]),
            S(3, 4, "<code>offer(\"A\")</code> writes at <code>tail</code> and moves it on.",
              [("main", [("q", "@q")])], [queue("a1", 0, 1, 1), arr(1, ['"A"', N, N, N])]),
            S(4, 5, "B goes into slot 1.",
              [("main", [("q", "@q")])], [queue("a1", 0, 2, 2), arr(1, ['"A"', '"B"', N, N])]),
            S(5, 6, "C goes into slot 2.",
              [("main", [("q", "@q")])], [queue("a1", 0, 3, 3), arr(1, ['"A"', '"B"', '"C"', N])]),
            S(6, 7, "<code>poll()</code> reads at <code>head</code>, clears the slot, and moves <code>head</code> on. <b>Nothing shifts.</b> An array list's <code>removeAt(0)</code> would move every element (O(n)); this is O(1).",
              [("main", [("q", "@q")])], [queue("a1", 1, 3, 2), arr(1, [N, '"B"', '"C"', N])]),
            S(7, 8, "Second poll: B. Slots 0 and 1 are free again, but they're <em>behind</em> <code>head</code>.",
              [("main", [("q", "@q")])], [queue("a1", 2, 3, 1), arr(1, [N, N, '"C"', N])]),
            S(8, 9, "D fills slot 3, the last one. <code>tail = (3 + 1) % 4</code> is <b>0</b>: it wraps around to the front.",
              [("main", [("q", "@q")])], [queue("a1", 2, 0, 2), arr(1, [N, N, '"C"', '"D"'])]),
            S(9, 10, "E reuses slot 0. The queue reads C, D, E, but in the array it's laid out D… E… C. That's the <em>circular</em> in circular buffer: the end is glued to the start.",
              [("main", [("q", "@q")])], [queue("a1", 2, 1, 3), arr(1, ['"E"', N, '"C"', '"D"'])]),
            S(10, 11, "F takes slot 1. Now <code>head == tail</code> and <code>size == 4</code>: full. (<code>head == tail</code> alone can't tell full from empty, which is why we keep <code>size</code>.)",
              [("main", [("q", "@q")])], [queue("a1", 2, 2, 4), arr(1, ['"E"', '"F"', '"C"', '"D"'])]),
            S(11, 12, "Full, so <code>offer(\"G\")</code> first grows: a new array of 8, copied <em>in queue order</em> starting from <code>head</code>, so the wrap disappears. The old array is now garbage. Then G goes in at slot 4. A grow is O(n), but it happens rarely enough that <code>offer</code> is amortised O(1), as in L16.",
              [("main", [("q", "@q")])], [queue("a2", 0, 5, 5), arr(2, ['"C"', '"D"', '"E"', '"F"', '"G"', N, N, N])]),
            S(12, 13, "<code>debug()</code> prints exactly this state, and the loop below drains it in arrival order: <code>C D E F G</code>.",
              [("main", [("q", "@q")])], [queue("a2", 0, 5, 5), arr(2, ['"C"', '"D"', '"E"', '"F"', '"G"', N, N, N])]),
        ],
    },
}
