import glob
import re
import os

WORKSPACE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LESSONS_DIR = os.path.join(WORKSPACE_DIR, "lessons")

def extract_quizzes(file_pattern):
    paths = sorted(glob.glob(os.path.join(LESSONS_DIR, file_pattern)))
    result = []
    for p in paths:
        with open(p, "r", encoding="utf-8") as f:
            txt = f.read()
        # Find all questions
        # Note: each q is enclosed in <div class="q" data-answer="..."> ... </div>
        q_matches = re.finditer(r'<div class="q" data-answer="(\d+)">([\s\S]*?<div class="q-explain">[\s\S]*?</div>\s*</div>)', txt)
        file_qs = []
        for qm in q_matches:
            ans = int(qm.group(1))
            body = qm.group(2)
            has_why = "data-why" in body
            prompt_m = re.search(r'<p class="q-prompt">([\s\S]*?)</p>', body)
            prompt = prompt_m.group(1).strip() if prompt_m else ""
            options = re.findall(r'<li>([\s\S]*?)</li>', body)
            explain_m = re.search(r'<div class="q-explain">([\s\S]*?)</div>', body)
            explain = explain_m.group(1).strip() if explain_m else ""
            file_qs.append({
                "prompt": prompt,
                "ans": ans,
                "options": options,
                "explain": explain,
                "has_why": has_why
            })
        result.append((os.path.basename(p), file_qs))
    return result

if __name__ == "__main__":
    import sys
    pattern = sys.argv[1] if len(sys.argv) > 1 else "*.html"
    res = extract_quizzes(pattern)
    for fn, qs in res:
        print(f"=== {fn} ({len(qs)} questions) ===")
        for i, q in enumerate(qs):
            print(f"Q{i+1}: {q['prompt'][:80]}... [has_why: {q['has_why']}]")
