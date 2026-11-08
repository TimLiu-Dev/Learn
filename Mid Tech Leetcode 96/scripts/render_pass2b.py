import re, sys, os
sys.path.append('scripts')
from template_engine import render_lesson

from data_p58_p62 import p58, p59, p60, p61, p62
from data_p63_p67 import p63, p64, p65, p66, p67
from data_p68_p72 import p68, p69, p70, p71, p72
from data_p73_p77 import p73, p74, p75, p76, p77
from data_p78_p81 import p78, p79, p80, p81
from data_p82_p86 import p82, p83, p84, p85, p86
from data_p87_p89 import p87, p88, p89

all_pass2b = [
    p58, p59, p60, p61, p62,
    p63, p64, p65, p66, p67,
    p68, p69, p70, p71, p72,
    p73, p74, p75, p76, p77,
    p78, p79, p80, p81,
    p82, p83, p84, p85, p86,
    p87, p88, p89
]

def main():
    with open('master-plan.html', 'r', encoding='utf-8') as f:
        c = f.read()

    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        files[num] = m.group(5)

    os.makedirs('lessons', exist_ok=True)
    print(f"Rendering Pass 2B: {len(all_pass2b)} lessons (58 to 89)...")

    for p in all_pass2b:
        num = p['num']
        p['prev_file'] = files.get(num - 1, 'master-plan.html')
        p['next_file'] = files.get(num + 1, 'master-plan.html')
        fname = f"lessons/{files[num]}"
        html = render_lesson(p)
        with open(fname, 'w', encoding='utf-8') as f:
            f.write(html)
        print(f"  Generated {fname} ({len(html)} bytes)")

    print(f"Successfully generated all {len(all_pass2b)} lessons for Pass 2B!")

if __name__ == '__main__':
    main()
