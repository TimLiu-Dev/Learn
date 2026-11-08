import re, sys, os
sys.path.append('scripts')
from template_engine import render_lesson

from data_p90_p93 import p90, p91, p92, p93
from data_p94_p96 import p94, p95, p96

all_pass3 = [p90, p91, p92, p93, p94, p95, p96]

def main():
    with open('master-plan.html', 'r', encoding='utf-8') as f:
        c = f.read()

    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        files[num] = m.group(5)

    os.makedirs('lessons', exist_ok=True)
    print(f"Rendering Pass 3: {len(all_pass3)} master hard lessons (90 to 96)...")

    for p in all_pass3:
        num = p['num']
        p['prev_file'] = files.get(num - 1, 'master-plan.html')
        p['next_file'] = files.get(num + 1, 'master-plan.html')
        fname = f"lessons/{files[num]}"
        html = render_lesson(p)
        with open(fname, 'w', encoding='utf-8') as f:
            f.write(html)
        print(f"  Generated {fname} ({len(html)} bytes)")

    print(f"Successfully generated all {len(all_pass3)} lessons for Pass 3!")

if __name__ == '__main__':
    main()
