"""Sum a JaCoCo CSV report (jacocoTestReport.csv) per package: lines and branches covered."""
import csv
import sys
from collections import defaultdict
from pathlib import Path

rows = list(csv.DictReader(open(Path(sys.argv[1]) / "jacocoTestReport.csv")))
tot = defaultdict(lambda: [0, 0, 0, 0])
for r in rows:
    for key in (r["PACKAGE"] or "(default)", "TOTAL"):
        t = tot[key]
        t[0] += int(r["LINE_COVERED"]); t[1] += int(r["LINE_MISSED"])
        t[2] += int(r["BRANCH_COVERED"]); t[3] += int(r["BRANCH_MISSED"])


def pct(c, m):
    return f"{c:>4}/{c + m:<4} {100 * c / (c + m):5.1f}%" if c + m else f"{'-':>4} {'':4} {'-':>6}"


print(f"{'package':<15}{'lines covered':>20}   {'branches covered':>20}")
for key in sorted(k for k in tot if k != "TOTAL") + ["TOTAL"]:
    c, m, bc, bm = tot[key]
    print(f"{key:<15}{pct(c, m):>20}   {pct(bc, bm):>20}")
