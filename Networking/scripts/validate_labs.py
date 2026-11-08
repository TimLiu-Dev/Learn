#!/usr/bin/env python3
"""
validate_labs.py — Automated verification script that extracts canonical solutions
and test harnesses from all HTML lessons, compiles them with OpenJDK 25,
and ensures 100% of test assertions pass.
"""

import glob
import os
import re
import subprocess
import sys
import tempfile

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")
JAVAC = "/opt/homebrew/opt/openjdk@25/bin/javac"
JAVA = "/opt/homebrew/opt/openjdk@25/bin/java"

if not os.path.exists(JAVAC):
    JAVAC = "javac"
if not os.path.exists(JAVA):
    JAVA = "java"

def validate_lesson(html_path):
    with open(html_path, "r", encoding="utf-8") as f:
        content = f.read()

    sol_match = re.search(r'<script type="text/plain" class="solution-code">\s*(.*?)\s*</script>', content, re.DOTALL)
    harness_match = re.search(r'<script type="text/plain" class="test-harness">\s*(.*?)\s*</script>', content, re.DOTALL)

    if not sol_match or not harness_match:
        return None, "No code practice widget found"

    sol_code = sol_match.group(1).strip()
    harness_code = harness_match.group(1).strip()

    if "// __STUDENT_CODE__" in harness_code:
        full_source = harness_code.replace("// __STUDENT_CODE__", sol_code)
    else:
        full_source = sol_code + "\n\n" + harness_code

    with tempfile.TemporaryDirectory(prefix="net-lab-val-") as temp_dir:
        sol_file = os.path.join(temp_dir, "Solution.java")
        with open(sol_file, "w", encoding="utf-8") as sf:
            sf.write(sol_code)

        lab_file = os.path.join(temp_dir, "Lab.java")
        with open(lab_file, "w", encoding="utf-8") as lf:
            lf.write(harness_code.replace("// __STUDENT_CODE__", ""))

        # Compile both
        c_res = subprocess.run([JAVAC, "-Xlint:all", sol_file, lab_file], capture_output=True, text=True)
        if c_res.returncode != 0:
            return False, f"Compilation failed:\n{c_res.stderr}"

        # Run Lab
        r_res = subprocess.run([JAVA, "-cp", temp_dir, "Lab"], capture_output=True, text=True, timeout=5)
        if r_res.returncode != 0:
            return False, f"Execution failed (exit {r_res.returncode}):\n{r_res.stdout}\n{r_res.stderr}"

        out = r_res.stdout.strip()
        summary_match = re.search(r"SUMMARY:(\d+)/(\d+)", out)
        if summary_match:
            passed, total = int(summary_match.group(1)), int(summary_match.group(2))
            if passed == total and total > 0:
                return True, f"Passed {passed}/{total} tests"
            else:
                return False, f"Failed checks: {passed}/{total}\n{out}"
        else:
            return True, f"Exited 0:\n{out}"

def main():
    files = sorted(glob.glob(os.path.join(LESSONS_DIR, "*.html")))
    print(f"Auditing {len(files)} lessons for interactive code practice harnesses...\n")

    tested = 0
    passed_count = 0
    failed_count = 0

    for path in files:
        base = os.path.basename(path)
        ok, msg = validate_lesson(path)
        if ok is None:
            continue
        tested += 1
        if ok:
            passed_count += 1
            print(f"  ✅ {base}: {msg}")
        else:
            failed_count += 1
            print(f"  ❌ {base}: {msg}")

    print(f"\nAudit complete: {tested} code practices tested, {passed_count} passed, {failed_count} failed.")
    if failed_count > 0:
        sys.exit(1)

if __name__ == "__main__":
    main()
