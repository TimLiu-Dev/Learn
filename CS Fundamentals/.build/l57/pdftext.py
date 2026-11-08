# Minimal PDF text dump: inflate FlateDecode streams, print text from Tj/TJ operators.
import re, sys, zlib
data = open(sys.argv[1], 'rb').read()
out = []
for m in re.finditer(rb'stream\r?\n', data):
    start = m.end(); end = data.find(b'endstream', start)
    raw = data[start:end]
    try: s = zlib.decompress(raw)
    except Exception:
        try: s = zlib.decompressobj().decompress(raw)
        except Exception: continue
    if b'TJ' not in s and b'Tj' not in s: continue
    line = []
    for t in re.finditer(rb'\[(.*?)\]\s*TJ|\((.*?)(?<!\\)\)\s*Tj|(T\*|Td|TD|Tm|\')', s, re.S):
        if t.group(3):
            if line: out.append(''.join(line)); line = []
            continue
        if t.group(1) is not None:
            parts = []
            for p in re.finditer(rb'\((.*?)(?<!\\)\)|(-?\d+\.?\d*)', t.group(1), re.S):
                if p.group(1) is not None: parts.append(p.group(1))
                elif float(p.group(2)) < -200: parts.append(b' ')
            txt = b''.join(parts)
        else: txt = t.group(2)
        txt = re.sub(rb'\\([()\\])', rb'\1', txt)
        txt = re.sub(rb'\\(\d{3})', lambda q: bytes([int(q.group(1), 8)]), txt)
        line.append(txt.decode('latin-1'))
    if line: out.append(''.join(line))
print('\n'.join(out))
