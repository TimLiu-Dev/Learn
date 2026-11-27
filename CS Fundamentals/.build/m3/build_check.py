"""Assembles projects/m3/check/Check.java from Check.template.java.

    python3 .build/m3/build_check.py <patient-launcher>

The patient launcher is starter_kotlin's build/install/logreport/bin/logreport (after
./gradlew installDist). Expected outputs are the patient's own reports; the Java patient
was verified to give identical bytes. Large-log hashes are constants checked in P3 notes.
"""
import pathlib, subprocess, sys

W = pathlib.Path(__file__).resolve().parents[2]
M3 = W / 'projects' / 'm3'
patient = sys.argv[1]

LARGE_LOG_SHA = '33337c7fb755f11a9c3107b9d8e5b01db791b466d4cc6a70e490877940715ba9'
LARGE_REPORT_SHA = 'd08186eb9d8059df152dce7351aae42747bb217e4789855593d278b2d9a336c2'

EDGES = [
    ('empty.log', 'An empty file still prints every heading, with zeros and "-".'),
    ('single_line.log', 'With one value, every percentile is that value.'),
    ('malformed.log', 'A line is a request only if the patient\'s regex matches the whole line and Instant.parse'
     ' accepts the timestamp: double, leading or trailing spaces, a tab, a ninth field, 2026-02-30 and hour 25 are'
     ' all malformed. The bytes field goes through Long.parseLong: "abc" and an overflowing number count as 0,'
     ' "+42" is 42 and "-7" is -7.'),
    ('ties.log', 'Equal request counts sort by endpoint name; equal latencies keep file order;'
     ' equal session counts sort by user name.'),
    ('crlf_no_final_newline.log', 'Lines end with \\r\\n here, and the last line has no newline at all.'),
    ('out_of_order.log', 'Timestamps are not in order. Sessions compare each request with the same user\'s'
     ' previous request in file order; first/last request are the earliest and latest timestamps.'),
]

def jstr(s):
    out = []
    for ch in s:
        o = ord(ch)
        if ch == '\\': out.append('\\\\')
        elif ch == '"': out.append('\\"')
        elif ch == '\n': out.append('\\n')
        elif ch == '\r': out.append('\\r')
        elif ch == '\t': out.append('\\t')
        elif o < 32 or o > 126: out.append('\\u%04x' % o)
        else: out.append(ch)
    return '"' + ''.join(out) + '"'

def jlong(s, indent='            '):
    # split long strings after each \n for readability
    parts = s.split('\n')
    pieces = [p + '\n' for p in parts[:-1]] + ([parts[-1]] if parts[-1] else [])
    if not pieces:
        return '""'
    return ('\n' + indent + '+ ').join(jstr(p) for p in pieces)

def report(path):
    r = subprocess.run([patient, str(path)], capture_output=True)
    assert r.returncode == 0 and not r.stderr, (path, r)
    return r.stdout.decode('utf-8')

edge_dir = M3 / 'starter_kotlin' / 'data' / 'edge'
data = []
data.append('    static final String LARGE_LOG_SHA = "%s";' % LARGE_LOG_SHA)
data.append('    static final String LARGE_REPORT_SHA = "%s";' % LARGE_REPORT_SHA)
data.append('')
data.append('    /** The patient\'s report on the small log (seed 7, 1,500 lines). */')
data.append('    static final String SMALL_EXPECTED = ' + jlong(report(M3 / 'starter_kotlin' / 'data' / 'small.log')) + ';')
data.append('')
data.append('    /** {file name, input, the patient\'s report, what the case is about}; same files as data/edge/. */')
data.append('    static final String[][] EDGES = {')
for name, hint in EDGES:
    text = (edge_dir / name).read_bytes().decode('utf-8')
    data.append('        {' + jstr(name) + ',')
    data.append('            ' + jlong(text) + ',')
    data.append('            ' + jlong(report(edge_dir / name)) + ',')
    data.append('            ' + jstr(hint) + '},')
data.append('    };')

gen_src = (M3 / 'tools' / 'LogGen.java').read_text()
a = gen_src.index('    // ---- copied verbatim into Check.java ----')
b = gen_src.index('    // ---- end copy ----') + len('    // ---- end copy ----')
gen = gen_src[a:b]

tpl = (W / '.build' / 'm3' / 'Check.template.java').read_text()
out = tpl.replace('    //@@GEN@@', gen).replace('    //@@DATA@@', '\n'.join(data))
(M3 / 'check' / 'Check.java').write_text(out)
print('wrote', M3 / 'check' / 'Check.java', len(out.splitlines()), 'lines')
