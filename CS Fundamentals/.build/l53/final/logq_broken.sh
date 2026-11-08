#!/usr/bin/env bash
# logq.sh FILE: answer five questions about a JSON-lines access log
set -euo pipefail
log="${1:?usage: logq.sh access.jsonl}"

echo "== requests: $(wc -l < "$log" | tr -d ' ')"

echo "== by status"
jq -r '.status' "$log" | sort | uniq -c | sort -k2n | awk '{ printf "%s %7d\n", $2, $1 }'

echo "== top 5 endpoints"
jq -r '"\(.method) \(.path)"' "$log" | sed -E 's#/[0-9]+#/{id}#g' |
  sort | uniq -c | sort -rn | head -5 | awk '{ printf "%7d %s %s\n", $1, $2, $3 }'

echo "== 5 slowest requests"
jq -r '[.ms, .method, .path, .requestId] | @tsv' "$log" |
  sort -t$'\t' -k1,1rn -k4,4 | head -5 | awk -F'\t' '{ printf "%5d ms  %s %s  %s\n", $1, $2, $3, $4 }'

echo "== p95 latency of GET /books/{id}"
jq -r 'select(.method == "GET" and (.path | test("^/books/[0-9]+$"))) | .ms' "$log" |
  sort -n | awk '{ a[NR] = $1 } END { print a[int((NR * 95 + 99) / 100)] " ms over " NR " requests" }'

echo "== rate-limited (429) by client"
jq -r 'select(.status == 429) | .client' "$log" | sort | uniq -c | sort -rn | awk '{ printf "%7d %s\n", $1, $2 }'
