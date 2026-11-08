# usage: python3 rec.py SESSION...  (run from .build/l53)
# Replays sessions/NAME.zsh in a clean interactive zsh (no rc files, system PATH) and writes
# out/NAME.txt as HTML for a <pre class="term">. Lines starting "## " run hidden; "#= old => new"
# replaces text in the output (scratch paths -> /Users/you).
import subprocess, sys, os, html, re
HERE = os.path.dirname(os.path.abspath(__file__))
S = '/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad'
HOME = S + '/l53w/home'
KDIR = S + '/l53w/kotlinc/bin'  # a copy of ../tools/kotlinc at a path without spaces
if not os.path.isdir(KDIR):
    import shutil; shutil.copytree(os.path.join(HERE, '..', 'tools', 'kotlinc'), S + '/l53w/kotlinc', symlinks=True)
for name in sys.argv[1:]:
    src = open(os.path.join(HERE, 'sessions', name + '.zsh')).read().splitlines()
    cmds, subs, cur = [], [(HOME, '/Users/you')], ''
    for line in src:
        if line.startswith('#= '):
            a, b = line[3:].split(' => '); subs.append((a, b)); continue
        cur = cur + line + '\n' if cur else line + '\n'
        if line.endswith('\\'): continue
        hidden = cur.startswith('## ')
        cmds.append((hidden, cur[3:] if hidden else cur)); cur = ''
    script = 'exec 2>&1; PS1=""; PS2=""; unsetopt PROMPT_SP PROMPT_CR; cd "$HOME/katas/l53" || exit 1\n'
    script += 'SRC="%s"; cp "$SRC"/*.kt "$SRC"/*.java "$SRC"/books.json "$SRC"/*.txt "$SRC"/scripts/* . 2>/dev/null; cp "$SRC"/final/* . 2>/dev/null\n' % HERE
    for i, (hidden, c) in enumerate(cmds):
        script += f'print -r -- "@@{i}@@"\n{c}'
    script += 'print -r -- "@@END@@"\n'
    env = {'HOME': HOME, 'PATH': '/usr/bin:/bin:/usr/sbin:/sbin:' + KDIR, 'TERM': 'dumb', 'LANG': 'en_US.UTF-8',
           'USER': 'tl', 'LOGNAME': 'tl', 'SHELL': '/bin/zsh', 'TMPDIR': S + '/l53w/tmp/'}
    os.makedirs(HOME + '/katas/l53', exist_ok=True); os.makedirs(S + '/l53w/tmp', exist_ok=True)
    r = subprocess.run(['/bin/zsh', '-f', '-i'], input=script, capture_output=True, text=True, env=env, timeout=900)
    raw = (r.stdout + r.stderr)
    parts = re.split(r'@@(\d+|END)@@\n', r.stdout)
    # parts: [pre, idx, text, idx, text, ...]
    outs = {}
    for k in range(1, len(parts) - 1, 2):
        if parts[k] != 'END': outs[int(parts[k])] = parts[k + 1]
    res = []
    for i, (hidden, c) in enumerate(cmds):
        if hidden: continue
        o = outs.get(i, '')
        for a, b in subs: c = c.replace(a, b); o = o.replace(a, b)
        lines = c.rstrip('\n').split('\n')
        res.append('<span class="p">$ </span>' + html.escape(lines[0], quote=False))
        res += [html.escape(l, quote=False) for l in lines[1:]]
        if o.strip('\n'): res.append(html.escape(o.rstrip('\n'), quote=False))
    open(os.path.join(HERE, 'out', name + '.txt'), 'w').write('\n'.join(res))
    if r.stderr.strip(): open(os.path.join(HERE, 'out', name + '.stderr'), 'w').write(r.stderr)
    print(name, 'ok', len(res), 'lines', '(stderr captured)' if r.stderr.strip() else '')
