# usage: python3 v5_nav.py 1|2|3 : rewire prev/next links of existing lessons to the v5 lessons of that batch.
# Edits the built lesson and every source that carries its nav (tpl/00NN.html, lNN/*src*.html).
import sys, re, glob, os
W = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NEW = {
    48: ('0048-maths-for-computer-science.html', 'Maths for Computer Science'),
    49: ('0049-greedy-shortest-paths-and-union-find.html', 'Greedy, Shortest Paths &amp; Union-Find'),
    52: ('0052-from-gates-to-instructions.html', 'From Gates to Instructions'),
    53: ('0053-the-shell-and-the-unix-toolkit.html', 'The Shell &amp; the Unix Toolkit'),
    54: ('0054-how-languages-work.html', 'How Languages Work'),
    55: ('0055-authentication-and-authorization.html', 'Authentication &amp; Authorization'),
    56: ('0056-storage-engines-and-other-data-models.html', 'Storage Engines &amp; Other Data Models'),
    57: ('0057-time-order-and-agreement.html', 'Time, Order &amp; Agreement'),
}
# batch -> [(existing lesson, 'prev'|'next', new lesson)]
BATCH = {
    1: [(15, 'next', 48), (16, 'prev', 48), (21, 'next', 49)],
    2: [(22, 'prev', 52), (22, 'next', 53), (23, 'prev', 53), (23, 'next', 54), (24, 'prev', 54)],
    3: [(33, 'next', 55), (34, 'prev', 55), (36, 'next', 56), (37, 'prev', 56), (43, 'next', 57), (44, 'prev', 57)],
}

def files_for(n):
    fs = glob.glob(f'{W}/lessons/{n:04d}-*.html') + [f'{W}/.build/tpl/{n:04d}.html']
    fs += [f for f in glob.glob(f'{W}/.build/l{n}/*.html') if 'src' in os.path.basename(f)]
    return [f for f in fs if os.path.exists(f)]

def link(side, m):
    href, title = NEW[m]
    return f'<a href="{href}">← L{m} · {title}</a>' if side == 'prev' else f'<a href="{href}">L{m} · {title} →</a>'

for n, side, m in BATCH[int(sys.argv[1])]:
    for f in files_for(n):
        s = open(f).read()
        nav = re.search(r'<div class="lesson-nav">(.*?)</div>', s, re.S)
        if not nav:
            continue
        anchors = re.findall(r'<a href="[^"]*">[^<]*</a>|<span[^>]*>[^<]*</span>', nav.group(1))
        assert len(anchors) == 2, (f, anchors)
        i = 0 if side == 'prev' else 1
        new_block = nav.group(0).replace(anchors[i], link(side, m), 1)
        if new_block != nav.group(0):
            s = s[:nav.start()] + new_block + s[nav.end():]
            open(f, 'w').write(s)
            print(f'L{n} {side} -> L{m}: {os.path.relpath(f, W)}')
