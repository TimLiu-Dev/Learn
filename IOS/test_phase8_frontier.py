#!/usr/bin/env python3
import os
import subprocess
from build_template import render_lesson_html
from data_phase8_frontier import PHASE8_FRONTIER_LESSONS

os.makedirs("lessons", exist_ok=True)
os.makedirs("exercises", exist_ok=True)

for lesson in PHASE8_FRONTIER_LESSONS:
    html_content = render_lesson_html(lesson)
    html_path = f"lessons/{lesson['slug']}.html"
    with open(html_path, "w", encoding="utf-8") as f:
        f.write(html_content)
    print(f"Generated HTML: {html_path}")

    ex_dir = f"exercises/{lesson['slug']}"
    os.makedirs(ex_dir, exist_ok=True)
    starter_path = f"{ex_dir}/practice.swift"
    with open(starter_path, "w", encoding="utf-8") as f:
        f.write(lesson['exercise']['starter_code'])
    print(f"Generated Starter Exercise: {starter_path}")

    temp_sol_path = f"/tmp/verify_{lesson['id']}.swift"
    with open(temp_sol_path, "w", encoding="utf-8") as f:
        f.write(lesson['exercise']['solution_code'])

    res = subprocess.run([
        "swift", "-module-cache-path", "/tmp/clang-module-cache", temp_sol_path
    ], capture_output=True, text=True)

    if os.path.exists(temp_sol_path):
        os.remove(temp_sol_path)

    if res.returncode != 0:
        print(f"❌ Verification FAILED for {lesson['id']} ({lesson['slug']}):\n{res.stderr or res.stdout}")
        exit(1)
    else:
        print(f"✅ Solution Verified: {lesson['lesson_num']} compiles and passes all assertions!")

print(f"\n🎉 All {len(PHASE8_FRONTIER_LESSONS)} Phase 8 Frontier Lessons & Exercises verified 100% successfully!")
