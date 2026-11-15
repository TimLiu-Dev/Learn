# usage: python3 q.py done L10 [start L50]  : move lessons between the lines of queue.md
import sys
p = __file__.replace("q.py", "queue.md")
lines = open(p).read().split("\n")
args = sys.argv[1:]
while args:
    op, n = args[0], args[1]; args = args[2:]
    if op == "done":
        lines[1] = " ".join(w for w in lines[1].split(" ") if w != n)
        lines[0] += " " + n
    elif op == "start":
        lines[2] = " ".join(w for w in lines[2].split(" ") if w != n)
        lines[1] += " " + n
open(p, "w").write("\n".join(lines))
print("\n".join(lines[:3]))
