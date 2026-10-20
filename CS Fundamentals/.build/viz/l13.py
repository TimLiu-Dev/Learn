import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402

ARR = ("a", "String[]", [("[0]", '"Ada"'), ("[1]", '"Linus"')])

VIZ = {
    "l13_arrays": {
        "java": "l13/ArrayStore.java",
        "steps": [
            S(None, 3, "One array object on the heap. Its runtime type is <b><code>String[]</code></b>, and the JVM remembers that: arrays are <em>reifiable</em>.",
              [("main", [("names", "@a")])], [ARR]),
            S(None, 4, "<code>things</code> is declared <code>Object[]</code>, but it's just a second arrow to the <em>same</em> <code>String[]</code>. Java allows this because arrays are covariant: a <code>String[]</code> counts as an <code>Object[]</code>.",
              [("main", [("names", "@a"), ("things", "@a")])], [ARR]),
            S(None, 5, "Reading through <code>things</code> is safe: whatever comes out of a <code>String[]</code> really is an <code>Object</code>. Prints <code>Ada</code>.",
              [("main", [("names", "@a"), ("things", "@a")])], [ARR]),
            S(None, 6, "Writing is the problem. The compiler sees <code>Object[]</code> and allows <code>42</code>. At runtime the JVM checks the <b>real</b> array type, finds <code>String[]</code>, and throws <code>ArrayStoreException</code>. Without that check, <code>names[1]</code> would hold an <code>Integer</code> and line 7 would break.",
              [("main", [("names", "@a"), ("things", "@a")])], [ARR, ("ex", "ArrayStoreException", [("message", '"java.lang.Integer"')])]),
        ],
    },
}
