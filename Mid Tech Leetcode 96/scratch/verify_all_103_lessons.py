#!/usr/bin/env python3
"""
Comprehensive Static & Dynamic Audit of all 103 LeetCode lessons.
"""

import glob
import re
import os
import sys

print("================================================================")
print("  MID-TECH LEETCODE 103: COMPREHENSIVE CURRICULUM VERIFICATION  ")
print("================================================================\n")

all_passed = True

files = sorted(glob.glob('lessons/*.html'))
print(f"--- 1. File Count Check: Found {len(files)} files (Expected: 103) ---")
if len(files) != 103:
    print(f"❌ FAIL: Expected 103 lesson files, got {len(files)}")
    all_passed = False
else:
    print("✅ PASS: Exactly 103 lesson files found on disk.")

# Check continuous numbering 1..103
nums_found = []
for f in files:
    m = re.search(r'(\d{4})-', os.path.basename(f))
    if m:
        nums_found.append(int(m.group(1)))

expected_nums = list(range(1, 104))
if nums_found != expected_nums:
    missing = set(expected_nums) - set(nums_found)
    print(f"❌ FAIL: Missing lesson numbers: {missing}")
    all_passed = False
else:
    print("✅ PASS: Numbering is strictly continuous from 0001 to 0103.")

# 2. Check for broken gstatic CDN links
print("\n--- 2. Checking for broken gstatic CDN links ---")
broken_cdn = [f for f in files if "gstatic.com/antigravity" in open(f, encoding='utf-8').read()]
if broken_cdn:
    print(f"❌ FAIL: {len(broken_cdn)} files contain broken gstatic URLs!")
    all_passed = False
else:
    print("✅ PASS: 0 broken gstatic URLs across all 103 files.")

# 3. Check for shared assets
print("\n--- 3. Checking shared assets links ---")
missing_theme = [f for f in files if "assets/lesson-theme.css" not in open(f, encoding='utf-8').read()]
missing_runtime = [f for f in files if "assets/lesson-runtime.js" not in open(f, encoding='utf-8').read()]
if missing_theme:
    print(f"❌ FAIL: {len(missing_theme)} files missing lesson-theme.css!")
    all_passed = False
else:
    print("✅ PASS: All 103 lessons link to assets/lesson-theme.css.")

if missing_runtime:
    print(f"❌ FAIL: {len(missing_runtime)} files missing lesson-runtime.js!")
    all_passed = False
else:
    print("✅ PASS: All 103 lessons link to assets/lesson-runtime.js.")

# 4. Check for raw unrendered LaTeX
print("\n--- 4. Checking for raw unrendered LaTeX in body ---")
latex_errors = []
for f in files:
    c = open(f, encoding='utf-8').read()
    body = re.sub(r'<script[\s\S]*?</script>', '', c)
    body = re.sub(r'<code[\s\S]*?</code>', '', body)
    latex = re.findall(r'\$[a-zA-Z0-9_\^\\\{\}\(\)\s]+\$', body)
    if latex:
        latex_errors.append((f, latex[:2]))

if latex_errors:
    print(f"❌ FAIL: Found raw LaTeX in {len(latex_errors)} files: {latex_errors[:3]}")
    all_passed = False
else:
    print("✅ PASS: 0 raw unrendered LaTeX strings in body outside <code>.")

# 5. Check quiz option word count balance across ALL 103 files
print("\n--- 5. Checking 4-MCQ Quiz word count balance ---")
quiz_errors = []
for f in files:
    c = open(f, encoding='utf-8').read()
    matches = re.findall(r'\"id\":\s*\"([ABCD])\",\s*\"text\":\s*\"([^\"]+)\"', c)
    if len(matches) != 16:
        matches = re.findall(r'id:\s*[\"\']([ABCD])[\"\'],\s*text:\s*[\"\']([^\"\']+)[\"\']', c)
    
    if len(matches) != 16:
        quiz_errors.append((f, f"Expected 16 options, found {len(matches)}"))
    else:
        for i in range(0, 16, 4):
            q_num = i // 4 + 1
            w_counts = [len(text.split()) for _, text in matches[i:i+4]]
            if len(set(w_counts)) != 1:
                quiz_errors.append((f, f"Q{q_num} word count mismatch: {w_counts}"))

if quiz_errors:
    print(f"❌ FAIL: Quiz option length mismatches in {len(quiz_errors)} files: {quiz_errors[:5]}")
    all_passed = False
else:
    print(f"✅ PASS: All 103 lessons have 4 diagnostic MCQs with 100% strictly identical option word counts (412 questions / 1,648 options verified!).")

# 6. Check Master Plan
print("\n--- 6. Checking Master Plan ---")
mp_content = open('master-plan.html', encoding='utf-8').read()
mp_probs = re.findall(r'num:\s*([0-9]+)', mp_content)
if len(mp_probs) != 103:
    print(f"❌ FAIL: master-plan.html lists {len(mp_probs)} problems (expected 103)")
    all_passed = False
else:
    print("✅ PASS: master-plan.html lists all 103 problems.")

if "0103-lc5-longest-palindromic-substring.html" not in mp_content:
    print("❌ FAIL: master-plan.html missing link to lesson 103!")
    all_passed = False
else:
    print("✅ PASS: master-plan.html links to capstone lesson 103.")

print("\n================================================================")
if all_passed:
    print("🎉 ALL 103 LESSONS PASSED 100% OF VERIFICATION CHECKS!")
    sys.exit(0)
else:
    print("❌ SOME VERIFICATION CHECKS FAILED!")
    sys.exit(1)
