import os
import sys
from dump_quizzes import extract_quizzes

def main():
    part = sys.argv[1] if len(sys.argv) > 1 else "000[5-9]*.html"
    files = extract_quizzes(part)
    for fn, qs in files:
        print(f"=== {fn} ===")
        for i, q in enumerate(qs):
            print(f"  Q{i}: {q['prompt']}")
            print(f"    Ans {q['ans']}: {q['options'][q['ans']] if q['ans'] < len(q['options']) else '?'}")
            for oi, o in enumerate(q['options']):
                marker = "*" if oi == q['ans'] else " "
                print(f"    {marker} [{oi}] {o}")
            print(f"    Explain: {q['explain']}")
            print()

if __name__ == "__main__":
    main()
