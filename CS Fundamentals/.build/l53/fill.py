# usage (from .build/): python3 l53/fill.py  ->  writes tpl/0053.html from l53/tpl_src.html
#   <!--OUT:name-->       the whole replayed session out/name.txt (see rec.py)
#   <!--OUT:name@a-b-->   only its visible commands a..b (0-based, inclusive), with their output
#   <!--PIPE-->           viz/pipeline.json, the real stage-by-stage outputs (viz/capture.py)
import os, re
HERE = os.path.dirname(os.path.abspath(__file__))
PROMPT = '<span class="p">$ </span>'


def blocks(name):
    text = open(os.path.join(HERE, 'out', name + '.txt')).read()
    out, cur = [], None
    for line in text.split('\n'):
        if line.startswith(PROMPT):
            cur = [line]; out.append(cur)
        elif cur is not None:
            cur.append(line)
    return ['\n'.join(b) for b in out]


def out(m):
    name, rng = m.group(1), m.group(2)
    bs = blocks(name)
    if rng:
        a, b = map(int, rng.split('-'))
        assert 0 <= a <= b < len(bs), (name, rng, len(bs))
        bs = bs[a:b + 1]
    return '\n'.join(bs)


src = open(os.path.join(HERE, 'tpl_src.html')).read()
src = re.sub(r'<!--OUT:(\w+)(?:@(\d+-\d+))?-->', out, src)
pipe = open(os.path.join(HERE, 'viz', 'pipeline.json')).read()
src = src.replace('<!--PIPE-->', pipe.replace('</', '<\\/'))
left = re.findall(r'<!--(?:OUT|PIPE)[^>]*-->', src)
assert not left, left
open(os.path.join(HERE, '..', 'tpl', '0053.html'), 'w').write(src)
print('wrote tpl/0053.html')
