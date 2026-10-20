"""Fill lesson templates with verified source files and memviz specs.

Placeholders in a template:
  <!--CODE:l4/Frames.kt-->   -> <pre><code class="language-kotlin">…escaped file…</code></pre>
  <!--VIZ:frames-->          -> memviz div + JSON spec (code listings read from the same files)
usage: python3 build.py template.html output.html
"""
import html
import importlib.util
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent  # scratchpad


def src(rel):
    return (ROOT / rel).read_text().rstrip("\n")


def code_block(rel):
    lang = "kotlin" if rel.endswith(".kt") else "java"
    return f'<pre><code class="language-{lang}">{html.escape(src(rel), quote=False)}</code></pre>'


from vizlib import S, D  # noqa: E402


VIZ = {
    "frames": {
        "kotlin": "l4/Frames.kt", "java": "l4/Frames.java",
        "steps": [
            S(13, 14, "<b>main</b> starts and gets a frame. <code>total</code> has no value yet.",
              [("main", [("total", D)])]),
            S(7, 8, "Calling <code>sumOfSquares(3, 4)</code> <b>pushes</b> a new frame. The arguments are <em>copied</em> into its parameters <code>a</code> and <code>b</code>.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", D), ("sb", D)])]),
            S(2, 3, "<code>square(a)</code> pushes a third frame. <code>x</code> gets a copy of <code>a</code>'s value.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", D), ("sb", D)]),
               ("square", [("x", "3"), ("result", D)])]),
            S(3, 4, "<code>result</code> is 9, and <code>square</code> is about to return it.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", D), ("sb", D)]),
               ("square", [("x", "3"), ("result", "9")])]),
            S(8, 9, "<code>square</code> returned: its frame was <b>popped</b> and is gone, and 9 landed in <code>sa</code>. Now <code>square(b)</code> pushes a <em>fresh</em> frame. Same function, new frame.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", "9"), ("sb", D)]),
               ("square", [("x", "4"), ("result", D)])]),
            S(3, 4, "<code>result</code> is 16.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", "9"), ("sb", D)]),
               ("square", [("x", "4"), ("result", "16")])]),
            S(9, 10, "Popped again: <code>sb</code> is 16, and <code>sumOfSquares</code> returns 25.",
              [("main", [("total", D)]), ("sumOfSquares", [("a", "3"), ("b", "4"), ("sa", "9"), ("sb", "16")])]),
            S(14, 15, "Back in <code>main</code>: <code>total</code> is 25. Every other frame is gone. The stack grows and shrinks with your calls.",
              [("main", [("total", "25")])]),
        ],
    },
    "alias": {
        "kotlin": "l4/Alias.kt", "java": "l4/Alias.java",
        "steps": [
            S(4, 8, "A primitive value sits <b>directly in the frame</b>.", [("main", [("x", "1")])]),
            S(5, 9, "<code>y = x</code> copies the <em>number</em>.", [("main", [("x", "1"), ("y", "1")])]),
            S(6, 10, "Changing <code>y</code> can't affect <code>x</code>. They're separate boxes.", [("main", [("x", "1"), ("y", "99")])]),
            S(9, 13, "<code>Counter(1)</code> creates an <b>object on the heap</b>. <code>a</code> doesn't hold the object; it holds a <b>reference</b>, drawn as an arrow.",
              [("main", [("x", "1"), ("y", "99"), ("a", "@o1")])], [("o1", "Counter", [("count", "1")])]),
            S(10, 14, "<code>b = a</code> copies the <em>arrow</em>, not the object. Two references, <b>one</b> object. This is called <b>aliasing</b>.",
              [("main", [("x", "1"), ("y", "99"), ("a", "@o1"), ("b", "@o1")])], [("o1", "Counter", [("count", "1")])]),
            S(11, 15, "Change the object through <code>b</code>… and <code>a</code> sees it, because there's only one object.",
              [("main", [("x", "1"), ("y", "99"), ("a", "@o1"), ("b", "@o1")])], [("o1", "Counter", [("count", "99")])]),
            S(14, 18, "<code>c</code> starts out pointing at the same object…",
              [("main", [("x", "1"), ("y", "99"), ("a", "@o1"), ("b", "@o1"), ("c", "@o1")])], [("o1", "Counter", [("count", "99")])]),
            S(15, 19, "…then gets <b>re-pointed</b> at a brand-new object. Re-pointing a variable never changes the object it used to point at, and never changes other variables.",
              [("main", [("x", "1"), ("y", "99"), ("a", "@o1"), ("b", "@o1"), ("c", "@o2")])],
              [("o1", "Counter", [("count", "99")]), ("o2", "Counter", [("count", "5")])]),
        ],
    },
    "pass": {
        "java": "l4/Pass.java",
        "steps": [
            S(None, 26, "<code>n</code> is 5 in <code>main</code>'s frame.", [("main", [("n", "5")])]),
            S(None, 8, "<code>bumpInt(n)</code> pushed a frame whose parameter <code>n</code> is a <b>copy</b> of 5, and incremented <em>its own copy</em>.",
              [("main", [("n", "5")]), ("bumpInt", [("n", "6")])]),
            S(None, 28, "Frame popped. <code>main</code>'s <code>n</code> is still 5.", [("main", [("n", "5")])]),
            S(None, 30, "A <code>Counter</code> on the heap, referenced by <code>counter</code>.",
              [("main", [("n", "5"), ("counter", "@o1")])], [("o1", "Counter", [("count", "5")])]),
            S(None, 12, "<code>bump(counter)</code>: the parameter <code>c</code> is a copy of the <b>arrow</b>. It points at the <em>same</em> object, so <code>c.count++</code> changes what the caller sees.",
              [("main", [("n", "5"), ("counter", "@o1")]), ("bump", [("c", "@o1")])], [("o1", "Counter", [("count", "6")])]),
            S(None, 32, "Popped. The change stuck, because it was made to the shared object.",
              [("main", [("n", "5"), ("counter", "@o1")])], [("o1", "Counter", [("count", "6")])]),
            S(None, 16, "<code>replace(counter)</code>: re-pointing the parameter <code>c</code> moves only <em>the copy</em> of the arrow. <code>counter</code> in <code>main</code> still points at the old object.",
              [("main", [("n", "5"), ("counter", "@o1")]), ("replace", [("c", "@o2")])],
              [("o1", "Counter", [("count", "6")]), ("o2", "Counter", [("count", "100")])]),
            S(None, 34, "Popped. Nothing references <code>Counter(100)</code> any more: it's <b>unreachable</b>, and the garbage collector will reclaim it (L24).",
              [("main", [("n", "5"), ("counter", "@o1")])],
              [("o1", "Counter", [("count", "6")]), ("o2", "Counter (unreachable)", [("count", "100")])]),
        ],
    },
    "boxes": {
        "kotlin": "l4/Boxes.kt", "java": "l4/Boxes.java",
        "steps": [
            S(11, 17, "<code>a</code> references a new <code>Box(1)</code>.", [("main", [("a", "@o1")])], [("o1", "Box", [("value", "1")])]),
            S(12, 18, "<code>b</code> is an alias: the same object.", [("main", [("a", "@o1"), ("b", "@o1")])], [("o1", "Box", [("value", "1")])]),
            S(13, 19, "<code>c</code> is a <em>different</em> <code>Box</code> that happens to hold the same value.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2")])], [("o1", "Box", [("value", "1")]), ("o2", "Box", [("value", "1")])]),
            S(14, 20, "<code>n</code> is a plain 5.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5")])], [("o1", "Box", [("value", "1")]), ("o2", "Box", [("value", "1")])]),
            S(4, 10, "<code>grow(b, n)</code>: the frame gets a copy of the arrow and a copy of 5. <code>b.value += n</code> changes the shared object to 6.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", D)]), ("grow", [("b", "@o1"), ("n", "5")])],
              [("o1", "Box", [("value", "6")]), ("o2", "Box", [("value", "1")])]),
            S(6, 12, "<code>m</code> is local to <code>grow</code>: 5 × 10 = 50.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", D)]), ("grow", [("b", "@o1"), ("n", "5"), ("m", "50")])],
              [("o1", "Box", [("value", "6")]), ("o2", "Box", [("value", "1")])]),
            S(16, 22, "Popped. <code>result</code> is 50, <code>main</code>'s <code>n</code> is still 5, and both <code>a</code> and <code>b</code> read 6. Output: <code>6 6 1 5 50</code>",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", "50")])],
              [("o1", "Box", [("value", "6")]), ("o2", "Box", [("value", "1")])]),
            S(18, 24, "A list on the heap holding <b>three references</b>, two of them to the same <code>Box</code>.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", "50"), ("boxes", "@l1")])],
              [("l1", "List", [("[0]", "@o1"), ("[1]", "@o2"), ("[2]", "@o1")]), ("o1", "Box", [("value", "6")]), ("o2", "Box", [("value", "1")])]),
            S(19, 25, "<code>boxes[2]</code> <em>is</em> <code>a</code>'s object, so setting it to 7 changes what <code>a</code>, <code>b</code> and <code>boxes[0]</code> all see.",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", "50"), ("boxes", "@l1")])],
              [("l1", "List", [("[0]", "@o1"), ("[1]", "@o2"), ("[2]", "@o1")]), ("o1", "Box", [("value", "7")]), ("o2", "Box", [("value", "1")])]),
            S(20, 26, "Output: <code>7 7 1</code>",
              [("main", [("a", "@o1"), ("b", "@o1"), ("c", "@o2"), ("n", "5"), ("result", "50"), ("boxes", "@l1")])],
              [("l1", "List", [("[0]", "@o1"), ("[1]", "@o2"), ("[2]", "@o1")]), ("o1", "Box", [("value", "7")]), ("o2", "Box", [("value", "1")])]),
        ],
    },
    "equality": {
        "kotlin": "l5/Equality.kt", "java": "l5/Equality.java",
        "steps": [
            S(4, 11, "<code>b1</code> references a new <code>Box(1)</code>.", [("main", [("b1", "@o1")])], [("o1", "Box", [("value", "1")])]),
            S(5, 12, "<code>b2</code> references <em>another</em> <code>Box(1)</code>. Same contents, different object.",
              [("main", [("b1", "@o1"), ("b2", "@o2")])], [("o1", "Box", [("value", "1")]), ("o2", "Box", [("value", "1")])]),
            S(6, 13, "<code>b3</code> is an alias of <code>b1</code>.",
              [("main", [("b1", "@o1"), ("b2", "@o2"), ("b3", "@o1")])], [("o1", "Box", [("value", "1")]), ("o2", "Box", [("value", "1")])]),
            S(7, 14, "Identity: <code>b1</code> and <code>b3</code> share an arrow target, so they're identical. <code>b1</code> and <code>b2</code> are not. Equality: <code>Box</code> never defined <code>equals</code>, so it falls back to identity, and <code>b1</code> isn't equal to <code>b2</code> either. Output: <code>false false true</code>",
              [("main", [("b1", "@o1"), ("b2", "@o2"), ("b3", "@o1")])], [("o1", "Box", [("value", "1")]), ("o2", "Box", [("value", "1")])]),
        ],
    },
    "grid": {
        "kotlin": "l5/Grid.kt", "java": "l5/Grid.java",
        "steps": [
            S(2, 6, "One empty list, referenced by <code>row</code>.", [("main", [("row", "@r")])], [("r", "MutableList / ArrayList", [("size", "0")])]),
            S(3, 8, "<code>grid</code> holds <b>three references to that same list</b>. Look at the arrows.",
              [("main", [("row", "@r"), ("grid", "@g")])],
              [("g", "List", [("[0]", "@r"), ("[1]", "@r"), ("[2]", "@r")]), ("r", "MutableList / ArrayList", [("size", "0")])]),
            S(4, 9, "Adding through <code>grid[0]</code> changes the one shared list, so every \"row\" shows <code>[1]</code>.",
              [("main", [("row", "@r"), ("grid", "@g")])],
              [("g", "List", [("[0]", "@r"), ("[1]", "@r"), ("[2]", "@r")]), ("r", "MutableList / ArrayList", [("[0]", "1")])]),
            S(7, 13, "The fix: create a <b>new</b> list for each slot. Three arrows, three objects.",
              [("main", [("row", "@r"), ("grid", "@g"), ("fixed", "@f")])],
              [("f", "List", [("[0]", "@f0"), ("[1]", "@f1"), ("[2]", "@f2")]),
               ("f0", "MutableList / ArrayList", [("size", "0")]), ("f1", "MutableList / ArrayList", [("size", "0")]), ("f2", "MutableList / ArrayList", [("size", "0")])]),
            S(8, 14, "Now only the first row changes: <code>[[1], [], []]</code>.",
              [("main", [("row", "@r"), ("grid", "@g"), ("fixed", "@f")])],
              [("f", "List", [("[0]", "@f0"), ("[1]", "@f1"), ("[2]", "@f2")]),
               ("f0", "MutableList / ArrayList", [("[0]", "1")]), ("f1", "MutableList / ArrayList", [("size", "0")]), ("f2", "MutableList / ArrayList", [("size", "0")])]),
        ],
    },
}


def load_extra_viz():
    """Merge VIZ dicts from ../viz/*.py, so each lesson keeps its specs in its own file."""
    for path in sorted((ROOT / "viz").glob("*.py")):
        spec = importlib.util.spec_from_file_location(path.stem, path)
        mod = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(mod)
        for name, cfg in mod.VIZ.items():
            assert name not in VIZ, f"duplicate viz name {name} in {path.name}"
            VIZ[name] = cfg


def viz_block(name):
    cfg = VIZ[name]
    spec = {"steps": cfg["steps"]}
    if "kotlin" in cfg:
        spec["kotlin"] = src(cfg["kotlin"]).split("\n")
    if "java" in cfg:
        spec["java"] = src(cfg["java"]).split("\n")
    # sanity: highlighted lines must exist
    for st in cfg["steps"]:
        for key, lang in (("kt", "kotlin"), ("jv", "java")):
            if st[key] is not None and lang in spec:
                assert 1 <= st[key] <= len(spec[lang]), (name, key, st[key])
    data = json.dumps(spec, ensure_ascii=False).replace("</", "<\\/")
    return f'<div class="memviz" data-src="viz-{name}"></div>\n<script type="application/json" id="viz-{name}">{data}</script>'


def build(template, out):
    load_extra_viz()
    text = Path(template).read_text()
    text = re.sub(r"<!--CODE:([\w/.]+)-->", lambda m: code_block(m.group(1)), text)
    text = re.sub(r"<!--VIZ:(\w+)-->", lambda m: viz_block(m.group(1)), text)
    assert "<!--CODE:" not in text and "<!--VIZ:" not in text
    Path(out).write_text(text)
    print("built", out)


if __name__ == "__main__":
    build(sys.argv[1], sys.argv[2])
