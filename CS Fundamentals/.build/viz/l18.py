import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402


def fr(n, ret=None):
    vs = [("n", str(n))]
    if ret is not None:
        vs.append(("returns", ret))
    return ("factorial", vs)


M = ("main", [("f", D)])

VIZ = {
    "l18_fact": {
        "kotlin": "l18/Factorial.kt",
        "java": "l18/Factorial.java",
        "steps": [
            S(7, 8, "<b>main</b> gets a frame. <code>f</code> has no value yet.", [M]),
            S(2, 3, "Calling <code>factorial(4)</code> pushes a frame with its own <code>n = 4</code>. Is <code>n &lt;= 1</code>? No.", [M, fr(4)]),
            S(3, 4, "The recursive case needs <code>factorial(3)</code> <em>before</em> it can multiply. This frame <b>pauses</b> mid-line and a new frame is pushed.", [M, fr(4)]),
            S(2, 3, "A second <code>factorial</code> frame, with a <em>different</em> <code>n = 3</code>. Each call has its own copy of its parameters (L4).", [M, fr(4), fr(3)]),
            S(2, 3, "<code>factorial(2)</code>: one more frame. Two frames below it are waiting.", [M, fr(4), fr(3), fr(2)]),
            S(2, 3, "<code>factorial(1)</code>: <code>n &lt;= 1</code> is true. This is the <b>base case</b>, so no more calls. The stack is at its deepest: 5 frames.", [M, fr(4), fr(3), fr(2), fr(1, "1")]),
            S(3, 4, "<code>factorial(1)</code> returned 1 and its frame was <b>popped</b>. <code>factorial(2)</code> resumes where it paused and computes <code>2 * 1</code>.", [M, fr(4), fr(3), fr(2, "2 * 1 = 2")]),
            S(3, 4, "Popped again. <code>factorial(3)</code> resumes: <code>3 * 2</code>.", [M, fr(4), fr(3, "3 * 2 = 6")]),
            S(3, 4, "<code>factorial(4)</code> resumes: <code>4 * 6</code>. The multiplications happen on the way <em>back</em>, in reverse order of the calls.", [M, fr(4, "4 * 6 = 24")]),
            S(8, 9, "Back in <code>main</code>: <code>f = 24</code>. Every <code>factorial</code> frame is gone. Calls go down, results come back up: the call stack really is a stack (L17).", [("main", [("f", "24")])]),
        ],
    },
}
