#!/bin/bash
# The curl session shown in Part 2. Run it while `./gradlew run --args="demo 25"` is up.
B=http://127.0.0.1:18044
echo '$ curl -i -X POST -H "X-Client-Id: ada" -d '"'"'{"url":"https://example.com/"}'"'"' '$B'/api/links'
OUT=$(curl -si -X POST -H "X-Client-Id: ada" -d '{"url":"https://example.com/"}' $B/api/links)
echo "$OUT"; echo
CODE=$(echo "$OUT" | tr -d '\r' | awk '/^Location:/{print substr($2,2)}')
echo "\$ curl -i $B/$CODE"
curl -si $B/$CODE; echo
echo '$ curl -s -o /dev/null -w "%{http_code} %{url_effective}\n" -L '$B/$CODE
curl -s -o /dev/null -w "%{http_code} %{url_effective}\n" -L $B/$CODE; echo
echo '$ curl -i -X POST -H "X-Client-Id: ada" -d '"'"'{"url":"https://example.com/sale","alias":"sale","ttlSeconds":2}'"'"' '$B'/api/links'
curl -si -X POST -H "X-Client-Id: ada" -d '{"url":"https://example.com/sale","alias":"sale","ttlSeconds":2}' $B/api/links; echo
echo "\$ curl -i $B/nope123"
curl -si $B/nope123; echo
sleep 0.5
echo "\$ curl -s $B/api/links/$CODE/stats"
curl -s $B/api/links/$CODE/stats; echo; echo
sleep 2
echo "\$ sleep 2; curl -i $B/sale"
curl -si $B/sale; echo
echo '$ for i in 1 2; do curl -s -o /dev/null -w "%{http_code}\n" -X POST -H "X-Client-Id: ada" -d '"'"'{"url":"https://example.com/x"}'"'"' '$B'/api/links; done'
for i in 1 2; do curl -s -o /dev/null -w "%{http_code}\n" -X POST -H "X-Client-Id: ada" -d '{"url":"https://example.com/x"}' $B/api/links; done
echo
echo '$ curl -i -X POST -H "X-Client-Id: ada" -d '"'"'{"url":"https://example.com/y"}'"'"' '$B'/api/links'
curl -si -X POST -H "X-Client-Id: ada" -d '{"url":"https://example.com/y"}' $B/api/links; echo
