#!/usr/bin/env python3
import re
import subprocess
import sys

print("=== STARTING GOLDEN MASTER V2 AUDIT & VERIFICATION ===")

files_to_check = [
    "master-plan.html",
    "reference/pattern-reference.html",
    "lessons/0001-lc217-contains-duplicate.html",
    "lessons/0023-lc49-group-anagrams.html"
]

all_passed = True

# 1. Check for broken gstatic CDN links
print("\n--- 1. Checking for broken gstatic CDN links ---")
for fpath in files_to_check:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    if "gstatic.com/antigravity/web/dev/tailwindcss.min.js" in content:
        print(f"❌ FAIL: {fpath} still contains gstatic CDN URL!")
        all_passed = False
    else:
        print(f"✅ PASS: {fpath} uses standard CDN")

# 2. Check for raw unrendered $ LaTeX
print("\n--- 2. Checking for raw unrendered LaTeX $ in lessons ---")
for fpath in ["lessons/0001-lc217-contains-duplicate.html", "lessons/0023-lc49-group-anagrams.html"]:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    # match $...$ outside of <script>
    body_content = re.sub(r'<script[\s\S]*?</script>', '', content)
    latex_matches = re.findall(r'\$[a-zA-Z0-9_\^\\\{\}\(\)\s]+\$', body_content)
    if latex_matches:
        print(f"❌ FAIL: {fpath} contains raw LaTeX: {latex_matches[:5]}")
        all_passed = False
    else:
        print(f"✅ PASS: {fpath} contains zero raw LaTeX strings")

# 3. Check quiz option word count balance
print("\n--- 3. Checking quiz option word count balance ---")
for fpath in ["lessons/0001-lc217-contains-duplicate.html", "lessons/0023-lc49-group-anagrams.html"]:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    matches = re.findall(r'id:\s*\"([ABCD])\",\s*text:\s*\"([^\"]+)\"', content)
    if len(matches) != 16:
        print(f"❌ FAIL: {fpath} has {len(matches)} options (expected 16)")
        all_passed = False
    else:
        file_quiz_ok = True
        for i in range(0, 16, 4):
            q_num = i // 4 + 1
            word_counts = [len(text.split()) for _, text in matches[i:i+4]]
            if len(set(word_counts)) != 1:
                print(f"❌ FAIL: {fpath} Q{q_num} word counts mismatch: {word_counts}")
                file_quiz_ok = False
                all_passed = False
        if file_quiz_ok:
            print(f"✅ PASS: {fpath} has 4 questions with 100% identical option word counts")

# 4. Check shared assets
print("\n--- 4. Checking shared assets ---")
for asset in ["assets/lesson-theme.css", "assets/lesson-runtime.js"]:
    try:
        with open(asset, 'r', encoding='utf-8') as f:
            c = f.read()
        print(f"✅ PASS: {asset} exists ({len(c)} bytes)")
    except Exception as e:
        print(f"❌ FAIL: {asset} missing or unreadable: {e}")
        all_passed = False

if all_passed:
    print("\n🎉 ALL STATIC AUDIT CHECKS PASSED!")
    sys.exit(0)
else:
    print("\n❌ SOME CHECKS FAILED!")
    sys.exit(1)
