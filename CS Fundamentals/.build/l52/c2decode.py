# Decode HotSpot's "[MachCode]" hex dump (printed when the hsdis plugin is missing) with clang:
#   java -XX:CompileCommand=print,Branches::sumBig Branches.java > out/c2_print.txt
#   python3 c2decode.py out/c2_print.txt > out/c2_sumBig.txt
# Uses the LAST C2-compiled version in the dump. Each "abcd efgh" group is 4 little-endian bytes.
import re, subprocess, sys, tempfile, os
lines = open(sys.argv[1]).read().splitlines()
start = max(i for i, l in enumerate(lines) if 'Compiled method (c2)' in l)
words = []
for l in lines[start:]:
    if '[/MachCode]' in l:
        break
    m = re.match(r'\s*0x[0-9a-f]+:\s*(.*)', l)
    if m and not m.group(1).startswith(';'):
        words += [int.from_bytes(bytes.fromhex(a + b), 'little') for a, b in re.findall(r'([0-9a-f]{4}) ([0-9a-f]{4})', m.group(1))]
d = tempfile.mkdtemp()
open(f'{d}/c.s', 'w').write('.text\n' + ''.join(f'.inst 0x{w:08x}\n' for w in words))
subprocess.run(['clang', '-c', f'{d}/c.s', '-o', f'{d}/c.o'], check=True)
out = subprocess.run(['objdump', '-d', '--no-show-raw-insn', f'{d}/c.o'], capture_output=True, text=True).stdout
print(lines[start].strip())
print('\n'.join(l for l in out.splitlines() if re.match(r'\s+[0-9a-f]+:', l)))
