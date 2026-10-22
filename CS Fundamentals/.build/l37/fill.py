"""Builds .build/tpl/0037.html from .build/l37/lesson.src.html.

Run from anywhere:  python3 .build/l37/fill.py   then   cd .build/bin && python3 build.py ../tpl/0037.html ../../lessons/0037-clean-code-and-refactoring.html

Markers in lesson.src.html:
  <!--RV:pricing-->      refactorviz JSON for the 8 pricing steps (diffs computed from steps/kt|jv/StepN)
  <!--RV:ai-->           refactorviz JSON for the "AI cleanup" diff (Step5 -> AiCleanup), with the real red results
  <!--OUT:runs/x.txt-->  a real Gradle test log, trimmed to test result lines, assertion messages and the summary
Everything shown comes from files in this folder; nothing is typed by hand.
"""
import difflib
import html
import json
import re
from pathlib import Path

HERE = Path(__file__).resolve().parent
TPL = HERE.parent / "tpl" / "0037.html"
CAT = "https://refactoring.com/catalog/"

STEPS = [
    ("Rename Variable", "renameVariable",
     "<code>t</code>, <code>d</code> and <code>i</code> become <code>totalCents</code>, <code>discount</code> and <code>quantity</code>/<code>unitCents</code>. The comment that only repeated the function name goes too."),
    ("Replace Magic Literal", "replaceMagicLiteral",
     "Every unexplained number gets a name. <code>2000</code> could mean anything; <code>COUPON_CAP_CENTS</code> can't. (<code>10</code> and <code>20</code> stay: they <em>are</em> what SAVE10 and SAVE20 mean.)"),
    ("Extract Function", "extractFunction",
     "The loop that adds up the lines becomes <code>subtotalCents</code>. <code>price</code> now reads as steps instead of mechanics."),
    ("Parameterize Function", "parameterizeFunction",
     "SAVE10, SAVE20 and both member discounts were the same code with different numbers. One <code>percentOff(cents, percent, cap)</code> replaces all four copies."),
    ("Decompose Conditional", "decomposeConditional",
     "The nested <code>if</code>s become two named questions: <code>memberDiscountPercent</code> and <code>shippingCents</code>. The two nested shipping ifs merge into one condition along the way."),
    ("Replace Conditional with Polymorphism", "replaceConditionalWithPolymorphism",
     "The coupon string chain becomes a sealed <code>Coupon</code> type. Each kind of coupon knows how to <code>apply</code> itself, and parsing happens in one place."),
    ("Introduce Parameter Object", "introduceParameterObject",
     "Items, coupon and the member flag always travel together, so they become an <code>Order</code>. The old three-argument <code>price</code> stays as a one-line wrapper, so no caller (and no test) has to change."),
    ("Split Phase", "splitPhase",
     "Phase 1, <code>parseOrder</code>, turns raw input into a clean model: bad quantities dropped, coupon parsed. Phase 2, <code>price(order)</code>, is pure arithmetic with no strings and no validation."),
]

KEEP = re.compile(r"(Test > .*(PASSED|FAILED))|AssertionFailedError|tests completed|^BUILD ")


def trimmed(rel: str, only: str | None = None) -> str:
    lines = []
    for line in (HERE / rel).read_text().splitlines():
        line = line.rstrip()
        if not KEEP.search(line):
            continue
        if only and "Test >" in line and not line.startswith(only):
            continue
        lines.append(line.strip() if "AssertionFailedError" in line else line)
    return "\n".join(lines)


def tests_line(rel: str, only: str = "PricingTest") -> tuple[str, bool]:
    text = (HERE / rel).read_text()
    passed = len(re.findall(rf"{only} > .* PASSED", text))
    failed = len(re.findall(rf"{only} > .* FAILED", text))
    ok = failed == 0 and "BUILD SUCCESSFUL" in text
    return (f"{passed} of {passed + failed} characterisation tests green · real ./gradlew test" if ok
            else f"{failed} of {passed + failed} characterisation tests FAILED · real ./gradlew test"), ok


def udiff(a: Path, b: Path, a_name: str, b_name: str) -> str:
    return "\n".join(difflib.unified_diff(a.read_text().splitlines(), b.read_text().splitlines(),
                                          fromfile=a_name, tofile=b_name, n=2, lineterm=""))


def rv_pricing() -> str:
    steps = []
    for n, (name, slug, why) in enumerate(STEPS, start=1):
        kt = udiff(HERE / f"steps/kt/Step{n-1}.kt", HERE / f"steps/kt/Step{n}.kt", f"Step{n-1}.kt", f"Step{n}.kt")
        jv = udiff(HERE / f"steps/jv/Step{n-1}.java", HERE / f"steps/jv/Step{n}.java", f"Step{n-1}.java", f"Step{n}.java")
        tk, ok_k = tests_line(f"runs/kt_step{n}.txt")
        tj, ok_j = tests_line(f"runs/jv_step{n}.txt")
        steps.append({"name": f"{n} · {name}", "url": CAT + slug + ".html", "why": why,
                      "diffs": [kt, jv], "tests": [tk, tj], "ok": [ok_k, ok_j]})
    return json.dumps({"langs": ["Kotlin", "Java"], "steps": steps}, indent=1)


def rv_ai() -> str:
    kt = udiff(HERE / "steps/kt/Step5.kt", HERE / "steps/kt/AiCleanup.kt", "Step5.kt", "AiCleanup.kt")
    jv = udiff(HERE / "steps/jv/Step5.java", HERE / "steps/jv/AiCleanup.java", "Step5.java", "AiCleanup.java")
    tk, ok_k = tests_line("runs/kt_ai.txt")
    tj, ok_j = tests_line("runs/jv_ai.txt")
    step = {"name": "“Simplify: apply both discounts in one step”", "url": "#p2-ai",
            "why": "A plausible cleanup an assistant might suggest on the Step 5 code. It reads well. Run the tests.",
            "diffs": [kt, jv], "tests": [tk, tj], "ok": [ok_k, ok_j]}
    return json.dumps({"langs": ["Kotlin", "Java"], "steps": [step]}, indent=1)


def main() -> None:
    src = (HERE / "lesson.src.html").read_text()
    # JSON goes inside <script type="application/json">; "</" must not appear raw.
    src = src.replace("<!--RV:pricing-->", rv_pricing().replace("</", "<\\/"))
    src = src.replace("<!--RV:ai-->", rv_ai().replace("</", "<\\/"))

    def out(m: re.Match) -> str:
        rel, only = m.group(1), m.group(2)
        return html.escape(trimmed(rel, only), quote=False)

    src = re.sub(r"<!--OUT:([\w/.]+)(?:\|(\w+))?-->", out, src)
    assert "<!--RV:" not in src and "<!--OUT:" not in src
    TPL.write_text(src)
    print("wrote", TPL)


if __name__ == "__main__":
    main()
