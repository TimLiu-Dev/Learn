head -3 requests.log | sed 's/GET/get/'
head -3 requests.log | sed -E 's/ ([0-9]+)ms$/ \1/'
sed -n '2,4p' requests.log
head -1000 requests.log > small.log
sed -i 's/ms$//' small.log
sed -i '' 's/ms$//' small.log
head -2 small.log
awk '{ print $3, $5 }' small.log | head -2
awk '$4 >= 500 { print $1, $3 }' small.log | head -3
awk '{ sum[$3] += $5; n[$3]++ } END { for (p in sum) printf "%-13s %6.1f ms avg over %d\n", p, sum[p]/n[p], n[p] }' small.log | sort
