import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402

N = "null"
A1 = lambda f0, f1: ("a1", "String[2]", [("[0]", f0), ("[1]", f1)])
A2 = lambda f0, f1, f2: ("a2", "String[4]", [("[0]", f0), ("[1]", f1), ("[2]", f2), ("[3]", N)])

VIZ = {
    "l16_resize": {
        "kotlin": "l16/Resize.kt", "java": "l16/Resize.java",
        "steps": [
            S(2, 5, "An array of capacity 2 on the heap, every slot <code>null</code>. An array's length is fixed forever once it's created (L4).",
              [("main", [("data", "@a1"), ("size", D)])], [A1(N, N)]),
            S(3, 6, "<code>size</code> counts the slots in use. <b>size</b> is how many elements you have; <b>capacity</b> (<code>data.size</code> / <code>data.length</code>) is how many fit.",
              [("main", [("data", "@a1"), ("size", "0")])], [A1(N, N)]),
            S(4, 7, "Adding at index <code>size</code> is one write: O(1).",
              [("main", [("data", "@a1"), ("size", "1")])], [A1('"a"', N)]),
            S(5, 8, "Another O(1) write. Now <code>size == capacity</code>: the array is full, and the next add needs room that doesn't exist.",
              [("main", [("data", "@a1"), ("size", "2")])], [A1('"a"', '"b"')]),
            S(6, 9, "So allocate a <b>new</b>, bigger array, here twice the size. Nothing is in it yet.",
              [("main", [("data", "@a1"), ("size", "2"), ("bigger", "@a2")])], [A1('"a"', '"b"'), A2(N, N, N)]),
            S(7, 10, "Copy every element across. This is the expensive step: <code>size</code> copies, so O(n) for this one add.",
              [("main", [("data", "@a1"), ("size", "2"), ("bigger", "@a2")])], [A1('"a"', '"b"'), A2('"a"', '"b"', N)]),
            S(8, 11, "Re-point <code>data</code> at the new array. Nothing refers to the old array any more, so the garbage collector can reclaim it.",
              [("main", [("data", "@a2"), ("size", "2"), ("bigger", "@a2")])], [A1('"a"', '"b"'), A2('"a"', '"b"', N)]),
            S(9, 12, "Now the add fits: one write. The next add will also be O(1), and so on until capacity 4 is full again.",
              [("main", [("data", "@a2"), ("size", "3"), ("bigger", "@a2")])], [A1('"a"', '"b"'), A2('"a"', '"b"', '"c"')]),
            S(10, 13, "Prints <code>size=3 capacity=4 [a, b, c, null]</code>. Doubling means resizes get <em>rarer</em> as the list grows: after 4, the next one is at 8, then 16, 32…",
              [("main", [("data", "@a2"), ("size", "3"), ("bigger", "@a2")])], [A1('"a"', '"b"'), A2('"a"', '"b"', '"c"')]),
        ],
    },
}
