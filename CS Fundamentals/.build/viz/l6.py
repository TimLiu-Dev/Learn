import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S, D  # noqa: E402

DUNE = [("title", '"Dune"'), ("author", '"Herbert"'), ("pages", "412")]
EMMA = [("title", '"Emma"'), ("author", '"Austen"'), ("pages", "100")]

VIZ = {
    "l6_construct": {
        "kotlin": "l6/Book.kt", "java": "l6/Book.java",
        "steps": [
            S(12, 27, "<code>main</code> is about to construct the first <code>Book</code>.",
              [("main", [("a", D)])]),
            S(1, 7, "The constructor is called like a function, so it gets a <b>frame</b>. The JVM has already allocated the new object on the heap with every field set to its <b>default value</b> (<code>null</code> for references, <code>0</code> for numbers). <code>this</code> is a reference to that new object.",
              [("main", [("a", D)]), ("Book constructor", [("this", "@o1"), ("title", '"Dune"'), ("author", '"Herbert"'), ("pages", "412")])],
              [("o1", "Book", [("title", "null"), ("author", "null"), ("pages", "0")])]),
            S(1, 10, "The parameters are copied into the fields. In Java that's your <code>this.title = title</code> lines. In Kotlin, <code>val</code>/<code>var</code> in the class header writes those assignments for you.",
              [("main", [("a", D)]), ("Book constructor", [("this", "@o1"), ("title", '"Dune"'), ("author", '"Herbert"'), ("pages", "412")])],
              [("o1", "Book", DUNE)]),
            S(4, 11, "The rest of the constructor body runs: Kotlin's <code>init</code> block, or the end of Java's constructor. It prints <code>created 'Dune'</code>.",
              [("main", [("a", D)]), ("Book constructor", [("this", "@o1"), ("title", '"Dune"'), ("author", '"Herbert"'), ("pages", "412")])],
              [("o1", "Book", DUNE)]),
            S(13, 28, "The constructor frame is popped and <code>a</code> holds the reference. The second book gets <code>pages = 100</code>, from the default parameter in Kotlin or the <code>this(title, author, 100)</code> chain in Java.",
              [("main", [("a", "@o1"), ("b", "@o2")])],
              [("o1", "Book", DUNE), ("o2", "Book", EMMA)]),
            S(14, 29, "The copy constructor builds a <b>separate</b> object with the same values. Compare L5's defensive copy.",
              [("main", [("a", "@o1"), ("b", "@o2"), ("c", "@o3")])],
              [("o1", "Book", DUNE), ("o2", "Book", EMMA), ("o3", "Book", DUNE)]),
            S(16, 31, "Changing the copy leaves the original alone: <code>a</code> still has 412 pages and <code>c</code> has 500. (<code>d</code> was built on the previous line, using named arguments.)",
              [("main", [("a", "@o1"), ("b", "@o2"), ("c", "@o3"), ("d", "@o4")])],
              [("o1", "Book", DUNE), ("o2", "Book", EMMA), ("o3", "Book", [("title", '"Dune"'), ("author", '"Herbert"'), ("pages", "500")]),
               ("o4", "Book", [("title", '"Ulysses"'), ("author", '"Unknown"'), ("pages", "730")])]),
        ],
    },
}
