import re, sys
sys.path.append('scripts')
from template_engine import render_lesson
from cluster6_p50_p51 import p50, p51
from cluster6_p52_p54 import p52, p53, p54

problems = [p50, p51, p52, p53, p54]

def main():
    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        files[num] = m.group(5)

    print(f"Rendering Cluster 6: {len(problems)} problems (50 to 54)...")
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
