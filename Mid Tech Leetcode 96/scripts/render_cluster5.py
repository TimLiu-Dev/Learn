import re, sys
sys.path.append('scripts')
from template_engine import render_lesson
from cluster5_p46_p47 import p46, p47
from cluster5_p48_p49 import p48, p49

problems = [p46, p47, p48, p49]

def main():
    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        files[num] = m.group(5)

    print(f"Rendering Cluster 5: {len(problems)} problems (46 to 49)...")
    for p in problems:
        num = p['num']
        p['prev_file'] = files[num - 1]
        p['next_file'] = files[num + 1]
        fname = f"lessons/{files[num]}"
        html = render_lesson(p)
        with open(fname, 'w', encoding='utf-8') as f:
            f.write(html)
        print(f"  Generated {fname} ({len(html)} bytes)")

if __name__ == '__main__':
    main()
