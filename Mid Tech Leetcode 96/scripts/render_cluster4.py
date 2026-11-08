import re, sys
sys.path.append('scripts')
from template_engine import render_lesson
from cluster4_p39 import p39
from cluster4_p40_p41 import p40, p41
from cluster4_p42_p43 import p42, p43
from cluster4_p44_p45 import p44, p45

problems = [p39, p40, p41, p42, p43, p44, p45]

def main():
    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        files[num] = m.group(5)

    print(f"Rendering Cluster 4: {len(problems)} problems (39 to 45)...")
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
