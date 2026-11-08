# Captures the real output of each prefix of the widget's pipeline -> viz/pipeline.json
import subprocess, json, os
S = '/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad'
cwd = S + '/l53w/home/katas/l53'
stages = ["head -12 requests.log", "cut -d' ' -f3", "sort", "uniq -c", "sort -rn", "head -3"]
notes = [
  "<code>head -12</code> passes on the first 12 lines of the 1,000,000-line log.",
  "<code>cut -d' ' -f3</code> keeps field 3 (fields split on spaces): the path.",
  "<code>sort</code> puts equal paths next to each other. <code>uniq</code> only spots repeats that touch.",
  "<code>uniq -c</code> collapses each run of equal lines into one, with its count in front.",
  "<code>sort -rn</code> sorts numerically (<code>-n</code>), biggest first (<code>-r</code>).",
  "<code>head -3</code> keeps the top three. Same shape as the 1,000,000-line run, on a sample you can check by eye.",
]
out = []
for i in range(len(stages)):
    cmd = ' | '.join(stages[:i + 1])
    r = subprocess.run(['/bin/zsh', '-f', '-c', cmd], cwd=cwd, capture_output=True, text=True,
                       env={'PATH': '/usr/bin:/bin', 'LANG': 'en_US.UTF-8'})
    out.append({"cmd": stages[i], "out": r.stdout.rstrip('\n'), "note": notes[i]})
json.dump({"stages": out}, open(os.path.join(os.path.dirname(__file__), 'pipeline.json'), 'w'), indent=1)
print(json.dumps(out, indent=1))
