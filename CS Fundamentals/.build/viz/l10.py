import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402

EX = ("ex", "NumberFormatException", [("message", 'For input string: "80x"')])
EX_GONE = ("ex", "NumberFormatException (unreachable)", [("message", 'For input string: "80x"')])

VIZ = {
    "l10_unwind": {
        "kotlin": "l10/Unwind.kt", "java": "l10/Unwind.java",
        "steps": [
            S(19, 20, "<code>main</code> calls <code>startServer</code> from inside a <code>try</code> that can catch <code>NumberFormatException</code>.",
              [("main", [])]),
            S(10, 11, "<code>startServer</code> gets a frame and calls <code>parsePort</code>. Its <code>try</code> has only a <code>finally</code>, no <code>catch</code>.",
              [("main", []), ("startServer", [("config", '"80x"'), ("port", D)])]),
            S(3, 4, "Parsing fails. An <b>exception object</b> is created on the heap and <b>thrown</b>. The JVM looks for a matching <code>catch</code> in the current frame, and <code>parsePort</code> has none.",
              [("main", []), ("startServer", [("config", '"80x"'), ("port", D)]), ("parsePort", [("text", '"80x"'), ("port", D)])],
              [EX]),
            S(13, 14, "<code>parsePort</code>'s frame is <b>popped</b> without returning: the rest of its code never runs. In <code>startServer</code> there's still no <code>catch</code>, but its <code>finally</code> <b>runs while the exception passes through</b>.",
              [("main", []), ("startServer", [("config", '"80x"'), ("port", D)])],
              [EX]),
            S(21, 22, "<code>startServer</code> is popped too, and its <code>return</code> never happens. <code>main</code>'s <code>catch</code> matches, so <b>unwinding stops here</b> and <code>e</code> points at the exception object.",
              [("main", [("e", "@ex")])],
              [EX]),
            S(23, 24, "The <code>catch</code> block is done, so <code>e</code> is out of scope and the exception is garbage. The program carries on normally. If no frame had caught it, the thread would have died and printed a stack trace.",
              [("main", [])],
              [EX_GONE]),
        ],
    },
}
