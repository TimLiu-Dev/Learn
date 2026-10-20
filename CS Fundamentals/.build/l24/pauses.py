# Summarise -Xlog:safepoint output: number of stop-the-world pauses, their total and the longest.
import re, sys

totals = [int(m.group(1)) / 1e6 for line in sys.stdin
          if (m := re.search(r"Total: (\d+) ns", line))]
print(f"pauses={len(totals)} total={sum(totals):.1f}ms max={max(totals):.2f}ms")
