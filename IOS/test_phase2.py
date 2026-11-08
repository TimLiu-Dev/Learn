#!/usr/bin/env python3
import os
import subprocess
from build_template import render_lesson_html
from data_phase2 import PHASE2_LESSONS
import data_phase2_part2  # Appends part 2

os.makedirs("lessons", exist_ok=True)
os.makedirs("exercises", exist_ok=True)

for lesson in PHASE2_LESSONS:
    # 1. Write HTML
    html_content = render_lesson_html(lesson)
    html_path = f"lessons/{lesson['slug']}.html"
    with open(html_path, "w", encoding="utf-8") as f:
        f.write(html_content)
    print(f"Generated HTML: {html_path}")

    # 2. Write starter exercise
    ex_dir = f"exercises/{lesson['slug']}"
    os.makedirs(ex_dir, exist_ok=True)
    starter_path = f"{ex_dir}/practice.swift"
    with open(starter_path, "w", encoding="utf-8") as f:
        f.write(lesson['exercise']['starter_code'])
    print(f"Generated Starter Exercise: {starter_path}")

    # 3. Verify solution code with Swift compiler
    temp_sol_path = f"/tmp/verify_{lesson['id']}.swift"
    with open(temp_sol_path, "w", encoding="utf-8") as f:
        f.write(lesson['exercise']['solution_code'])

    res = subprocess.run([
        "swift", "-module-cache-path", "/tmp/clang-module-cache", temp_sol_path
    ], capture_output=True, text=True)

    if os.path.exists(temp_sol_path):
        os.remove(temp_sol_path)

    if res.returncode != 0:
        print(f"❌ Verification FAILED for {lesson['id']}:\n{res.stderr or res.stdout}")
        exit(1)
    else:
        print(f"✅ Solution Verified: {lesson['lesson_num']} compiles and passes all assertions!")

print(f"\n🎉 All {len(PHASE2_LESSONS)} Phase 2 Lessons & Exercises verified 100% successfully!")
