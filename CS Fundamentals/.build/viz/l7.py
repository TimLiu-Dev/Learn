import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
from vizlib import S  # noqa: E402

TEAM1 = ("t1", "Team", [("name", '"Core"'), ("members", "@l1")])

VIZ = {
    "l7_shallow": {
        "kotlin": "l7/ShallowCopy.kt", "java": "l7/ShallowCopy.java",
        "steps": [
            S(4, 8, "<code>original</code> references a <code>Team</code> object, whose <code>members</code> field is itself a <b>reference</b> to a list. (Strings are objects too; they're shown inline to keep the picture small.)",
              [("main", [("original", "@t1")])],
              [TEAM1, ("l1", "MutableList / ArrayList", [("[0]", '"Ada"')])]),
            S(5, 9, "<code>copy(name = …)</code> (or Java's <code>new Team(…, original.members())</code>) makes a <b>new</b> <code>Team</code>, but copies the <code>members</code> <em>arrow</em>, not the list. Both teams share one list: a <b>shallow copy</b>.",
              [("main", [("original", "@t1"), ("renamed", "@t2")])],
              [TEAM1, ("t2", "Team", [("name", '"Platform"'), ("members", "@l1")]),
               ("l1", "MutableList / ArrayList", [("[0]", '"Ada"')])]),
            S(6, 10, "Adding to <code>renamed</code>'s members changes the one shared list, so <code>original</code> \"gains\" Linus too.",
              [("main", [("original", "@t1"), ("renamed", "@t2")])],
              [TEAM1, ("t2", "Team", [("name", '"Platform"'), ("members", "@l1")]),
               ("l1", "MutableList / ArrayList", [("[0]", '"Ada"'), ("[1]", '"Linus"')])]),
            S(9, 13, "The two teams are <em>not</em> equal (their names differ), yet they share state. Shallow immutability: the fields can't be re-pointed, but what they point at can still change (L5).",
              [("main", [("original", "@t1"), ("renamed", "@t2")])],
              [TEAM1, ("t2", "Team", [("name", '"Platform"'), ("members", "@l1")]),
               ("l1", "MutableList / ArrayList", [("[0]", '"Ada"'), ("[1]", '"Linus"')])]),
        ],
    },
}
