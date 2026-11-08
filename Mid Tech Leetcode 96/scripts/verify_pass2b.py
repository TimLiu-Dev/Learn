import os, re, glob

def main():
    all_files = glob.glob("lessons/00[5-8][0-9]-*.html")
    lesson_files = sorted([f for f in all_files if 58 <= int(os.path.basename(f)[:4]) <= 89])
    print(f"Verifying all {len(lesson_files)} Pass 2B lesson files (0058 to 0089):")

    checks = {
        'doctype': r'<!DOCTYPE html>',
        'html_close': r'</html>',
        'body_close': r'</body>',
        'whiteboard': r'id="whiteboard"',
        'stepper': r'id="stepper-visual-container"',
        'scratchpad': r'id="scratchpad-editor"',
        'scratchpad_btn': r'id="btn-sp-kotlin"',
        'phase1': r'Phase 1: Clarifying Questions',
        'phase2': r'Phase 2: Space-Time Strategy Pitch',
        'phase3': r'Phase 3: Live Coding Narrative',
        'phase4': r'Phase 4: Cold Edge-Case Verification',
        'senior': r'Senior Architectural &amp; Scalability Follow-Up',
        'mcq': r'checkMCQ\(1,',
        'timer': r'id="recall-countdown"',
        'prev_link': r'href="00[0-9]{2}-[^"]+\.html"',
    }

    all_passed = True
    for fpath in lesson_files:
        fname = os.path.basename(fpath)
        with open(fpath, 'r', encoding='utf-8') as f:
            content = f.read()

        file_errors = []
        for check_name, pattern in checks.items():
            if not re.search(pattern, content):
                file_errors.append(check_name)

        # Check script tags
        scripts = re.findall(r'<script>(.*?)</script>', content, re.DOTALL)
        if len(scripts) < 2:
            file_errors.append(f"insufficient scripts ({len(scripts)})")

        if file_errors:
            print(f"  [FAIL] {fname}: Missing {file_errors}")
            all_passed = False
        else:
            print(f"  [PASS] {fname} ({len(content)} bytes)")

    if all_passed and len(lesson_files) == 32:
        print(f"\n=== ALL HTML STRUCTURE & COMPONENT CHECKS PASSED (32/32) ===")
    else:
        print(f"\n=== VERIFICATION FAILED (Passed: {all_passed}, Count: {len(lesson_files)}/32) ===")

if __name__ == '__main__':
    main()
