#!/bin/bash
# Runs Shelf from its installDist start script and drives it with curl. Usage: curl_session.sh <project dir>
set -u
PROJ="$1"
RUN=$(mktemp -d)
( cd "$PROJ" && ./gradlew -q installDist ) || exit 1
cd "$RUN"
"$PROJ/app/build/install/app/bin/app" > server.log 2>&1 &
PID=$!
for i in $(seq 1 50); do curl -s localhost:8080/health >/dev/null 2>&1 && break; sleep 0.2; done
show() { echo "\$ $*"; eval "$@"; echo; echo; }
echo "\$ app/build/install/app/bin/app &"
cat server.log
echo
show "curl -s localhost:8080/health"
show "curl -si -X POST localhost:8080/books -d '{\"title\":\"Dune\",\"author\":\"Frank Herbert\",\"year\":1965}'"
show "curl -s -X POST localhost:8080/books -d '{\"title\":\"The Dispossessed\",\"author\":\"Ursula K. Le Guin\",\"year\":1974}'"
show "curl -s 'localhost:8080/books?limit=1'"
show "curl -s -X POST localhost:8080/members -d '{\"name\":\"Ada Lovelace\",\"email\":\"Ada@Example.com\"}'"
show "curl -si -X POST localhost:8080/loans -d '{\"bookId\":1,\"memberId\":1}'"
show "curl -s -X POST localhost:8080/loans -d '{\"bookId\":1,\"memberId\":1}'"
show "curl -s -X POST localhost:8080/loans/1/return"
show "curl -s localhost:8080/members/1/loans"
show "curl -si -X POST localhost:8080/books -d '{\"title\":\"\",\"year\":3000}'"
show "curl -si -X PATCH localhost:8080/books/1"
show "curl -s -X DELETE localhost:8080/books/1"
show "curl -s localhost:8080/books/99"
kill $PID; wait $PID 2>/dev/null
rm -rf "$RUN"
