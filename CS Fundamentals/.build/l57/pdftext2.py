# Faster PDF text dump: inflate FlateDecode streams, walk string tokens linearly.
import re, sys, zlib
data = open(sys.argv[1], 'rb').read()
out = []
for m in re.finditer(rb'stream\r?\n', data):
    start = m.end(); end = data.find(b'endstream', start)
    try: s = zlib.decompress(data[start:end])
    except Exception: continue
    if b'BT' not in s: continue
    i, n, buf = 0, len(s), []
    while i < n:
        c = s[i]
        if c == 0x28:  # '(' string
            depth, j, chunk = 1, i + 1, bytearray()
            while j < n and depth:
                ch = s[j]
                if ch == 0x5c and j + 1 < n:
                    nx = s[j+1]
                    if 48 <= nx <= 55:
                        k = j + 1; v = 0
                        while k < n and k < j + 4 and 48 <= s[k] <= 55: v = v * 8 + s[k] - 48; k += 1
                        chunk.append(v & 255); j = k; continue
                    chunk.append({0x6e: 10, 0x74: 9}.get(nx, nx)); j += 2; continue
                if ch == 0x28: depth += 1
                elif ch == 0x29:
                    depth -= 1
                    if depth == 0: break
                chunk.append(ch); j += 1
            buf.append(chunk.decode('latin-1')); i = j + 1; continue
        if c in (0x2d,) or 48 <= c <= 57:
            j = i + 1
            while j < n and (48 <= s[j] <= 57 or s[j] == 0x2e): j += 1
            try:
                if float(s[i:j]) < -200: buf.append(' ')
            except ValueError: pass
            i = j; continue
        if s.startswith(b'ET', i) or s.startswith(b'T*', i) or s.startswith(b'Td', i) or s.startswith(b'TD', i):
            buf.append('\n'); i += 2; continue
        i += 1
    out.append(''.join(buf))
print('\n'.join(out))
