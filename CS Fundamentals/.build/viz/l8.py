import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S  # noqa: E402

DOG1 = ("o1", "Dog", [("runtime class", "Dog")])
DOG2 = ("o2", "Dog", [("runtime class", "Dog")])

VIZ = {
    "l8_dispatch": {
        "kotlin": "l8/Puzzle.kt", "java": "l8/Puzzle.java",
        "steps": [
            S(13, 15, "<code>a</code> is <b>declared</b> as <code>Animal</code> (its <em>static</em> type), but the object it points to was built as a <code>Dog</code> (its <em>runtime</em> type).",
              [("main", [("a: Animal", "@o1")])], [DOG1]),
            S(14, 16, "<code>d</code> is declared as <code>Dog</code> and points to a <code>Dog</code>.",
              [("main", [("a: Animal", "@o1"), ("d: Dog", "@o2")])], [DOG1, DOG2]),
            S(6, 8, "<code>a.sound()</code> is an <b>override</b>, so the JVM follows the arrow and asks the <em>object</em> which <code>sound()</code> to run. It's a <code>Dog</code>, so you get <code>Woof</code>. This is <b>dynamic dispatch</b>.",
              [("main", [("a: Animal", "@o1"), ("d: Dog", "@o2")]), ("Dog.sound", [("this", "@o1")])], [DOG1, DOG2]),
            S(9, 11, "<code>describe(a)</code> is an <b>overload</b> choice, and the compiler made it <em>before the program ran</em>, from the declared type <code>Animal</code>. The object's real class is never consulted, so the result is <code>some animal</code>.",
              [("main", [("a: Animal", "@o1"), ("d: Dog", "@o2")]), ("describe(Animal)", [("a", "@o1")])], [DOG1, DOG2]),
            S(10, 12, "<code>describe(d)</code>: here the declared type is <code>Dog</code>, so the compiler picks the <code>Dog</code> overload, giving <code>a dog</code>.",
              [("main", [("a: Animal", "@o1"), ("d: Dog", "@o2")]), ("describe(Dog)", [("d", "@o2")])], [DOG1, DOG2]),
        ],
    },
}
