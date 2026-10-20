import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402

BOX = "Ref.IntRef (Kotlin) / int[] (Java)"


def box(i, v):
    return (f"r{i}", BOX, [("element / [0]", v)])


def lam(i):
    return (f"f{i}", "lambda object", [("captured count", f"@r{i}")])


VIZ = {
    "l14_counter": {
        "kotlin": "l14/Counter.kt", "java": "l14/Counter.java",
        "steps": [
            S(2, 5, "<code>makeCounter()</code> gets a frame. <code>count</code> will be captured by a lambda that outlives this frame, so it can't just sit in the frame. Kotlin quietly puts it in a small <b>box on the heap</b> (<code>Ref.IntRef</code>). In Java you build that box yourself: a one-slot <code>int[]</code>.",
              [("main", [("a", D), ("b", D)]), ("makeCounter", [("count", "@r1")])],
              [box(1, "0")]),
            S(3, 6, "The lambda is created: an <b>object on the heap</b> that holds a reference to the box. That's the closure.",
              [("main", [("a", D), ("b", D)]), ("makeCounter", [("count", "@r1")])],
              [lam(1), box(1, "0")]),
            S(7, 10, "<code>makeCounter</code> returns, and its frame is popped. The box survives, because the lambda still points at it. <code>a</code> now references the lambda.",
              [("main", [("a", "@f1"), ("b", D)])],
              [lam(1), box(1, "0")]),
            S(8, 11, "A second call runs the same code again and creates a <em>second</em> box and a <em>second</em> lambda. <code>a</code> and <code>b</code> share nothing.",
              [("main", [("a", "@f1"), ("b", "@f2")])],
              [lam(1), box(1, "0"), lam(2), box(2, "0")]),
            S(9, 12, "Calling <code>a()</code> runs the lambda body, which bumps <b>its own</b> box to 1.",
              [("main", [("a", "@f1"), ("b", "@f2")])],
              [lam(1), box(1, "1"), lam(2), box(2, "0")]),
            S(10, 13, "Again: 2. The state lives between calls because it's on the heap, not in a frame.",
              [("main", [("a", "@f1"), ("b", "@f2")])],
              [lam(1), box(1, "2"), lam(2), box(2, "0")]),
            S(11, 14, "A third call gives 3, which is printed.",
              [("main", [("a", "@f1"), ("b", "@f2")])],
              [lam(1), box(1, "3"), lam(2), box(2, "0")]),
            S(12, 15, "<code>b()</code> bumps <code>b</code>'s box, which is still at 0, so it prints 1. Output: <code>3</code> then <code>1</code>.",
              [("main", [("a", "@f1"), ("b", "@f2")])],
              [lam(1), box(1, "3"), lam(2), box(2, "1")]),
        ],
    },
}
