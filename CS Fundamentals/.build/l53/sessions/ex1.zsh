grep ' 500 ' requests.log | cut -d' ' -f3 | sort | uniq -c | sort -rn | head -3
awk '{ n[$3]++; if ($4 == 500) bad[$3]++ } END { for (p in n) printf "%-13s %5.2f%%\n", p, 100 * bad[p] / n[p] }' requests.log | sort -k2 -rn | head -3
