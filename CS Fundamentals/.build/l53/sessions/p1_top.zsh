## TIMEFMT='real %*E s   user %*U s   cpu %P'
cut -d' ' -f3 requests.log | head -3
head -12 requests.log | cut -d' ' -f3 | uniq -c
cut -d' ' -f3 requests.log | sort | uniq -c | sort -rn | head -5
awk '{ n[$3]++ } END { for (p in n) print n[p], p }' requests.log | sort -rn | head -5
time (cut -d' ' -f3 requests.log | sort | uniq -c | sort -rn | head -5 > /dev/null)
time (awk '{ n[$3]++ } END { for (p in n) print n[p], p }' requests.log | sort -rn | head -5 > /dev/null)
