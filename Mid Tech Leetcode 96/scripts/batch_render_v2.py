"""
Batch renderer for Problems 24 to 96 using template_engine_v2.
"""

import sys, re, os, glob
sys.path.append('scripts')
from template_engine_v2 import render_lesson_v2

# Import all problem modules
from data_cluster1 import cluster1_problems
from data_cluster2 import cluster2_problems
from data_cluster3 import cluster3_problems
from cluster4_p39 import p39
from cluster4_p40_p41 import p40, p41
from cluster4_p42_p43 import p42, p43
from cluster4_p44_p45 import p44, p45
from cluster5_p46_p47 import p46, p47
from cluster5_p48_p49 import p48, p49
from cluster6_p50_p51 import p50, p51
from cluster6_p52_p54 import p52, p53, p54
from cluster7_p55_p57 import p55, p56, p57
from data_p58_p62 import p58, p59, p60, p61, p62
from data_p63_p67 import p63, p64, p65, p66, p67
from data_p68_p72 import p68, p69, p70, p71, p72
from data_p73_p77 import p73, p74, p75, p76, p77
from data_p78_p81 import p78, p79, p80, p81
from data_p82_p86 import p82, p83, p84, p85, p86
from data_p87_p89 import p87, p88, p89
from data_p90_p93 import p90, p91, p92, p93
from data_p94_p96 import p94, p95, p96

all_problems_24_96 = [
    # 24-27
    cluster1_problems[1], cluster1_problems[2], cluster1_problems[3], cluster1_problems[4],
    # 28-33
    cluster2_problems[0], cluster2_problems[1], cluster2_problems[2], cluster2_problems[3], cluster2_problems[4], cluster2_problems[5],
    # 34-38
    cluster3_problems[0], cluster3_problems[1], cluster3_problems[2], cluster3_problems[3], cluster3_problems[4],
    # 39-45
    p39, p40, p41, p42, p43, p44, p45,
    # 46-49
    p46, p47, p48, p49,
    # 50-54
    p50, p51, p52, p53, p54,
    # 55-57
    p55, p56, p57,
    # 58-89
    p58, p59, p60, p61, p62,
    p63, p64, p65, p66, p67,
    p68, p69, p70, p71, p72,
    p73, p74, p75, p76, p77,
    p78, p79, p80, p81,
    p82, p83, p84, p85, p86,
    p87, p88, p89,
    # 90-96
    p90, p91, p92, p93,
    p94, p95, p96
]

def main():
    print(f"Total problems to render: {len(all_problems_24_96)} (Problems 24 to 96)")
    # Map filenames from master-plan.html
    mp_content = open('master-plan.html', encoding='utf-8').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),.*?file:\s*\"([^\"]+)\"', mp_content):
        num = int(m.group(1))
        files[num] = m.group(2)

    # Fallback to existing files in lessons/
    for f in sorted(glob.glob('lessons/*.html')):
        base = os.path.basename(f)
        m = re.match(r'^0*([0-9]+)-', base)
        if m:
            num = int(m.group(1))
            if num not in files:
                files[num] = base

    success_count = 0
    for p in all_problems_24_96:
        num = p['num']
        prev_f = files.get(num - 1, 'master-plan.html')
        next_f = files.get(num + 1, 'master-plan.html')
        p['prev_file'] = prev_f
        p['next_file'] = next_f
        
        target_file = files.get(num)
        if not target_file:
            slug = p.get('lc_slug', f"p{num}")
            target_file = f"{num:04d}-lc{p['lc']}-{slug}.html"
            files[num] = target_file

        out_path = f"lessons/{target_file}"
        try:
            html_content = render_lesson_v2(p)
            with open(out_path, 'w', encoding='utf-8') as f:
                f.write(html_content)
            success_count += 1
            print(f"  [{success_count}/{len(all_problems_24_96)}] Rendered {out_path} ({len(html_content)} bytes)")
        except Exception as e:
            print(f"❌ ERROR rendering #{num} {p.get('title')}: {e}")
            raise e

    print(f"\n🎉 Successfully batch rendered all {success_count} lessons (24 to 96) to Golden Master v2!")

if __name__ == '__main__':
    main()
