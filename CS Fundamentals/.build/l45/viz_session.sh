#!/bin/bash
# The five requests traced by the routeviz widget, with full status lines and headers.
set -u
PROJ="$1"
RUN=$(mktemp -d)
( cd "$PROJ" && ./gradlew -q installDist ) || exit 1
cd "$RUN"
"$PROJ/app/build/install/app/bin/app" > server.log 2>&1 &
PID=$!
for i in $(seq 1 50); do curl -s localhost:8080/health >/dev/null 2>&1 && break; sleep 0.2; done
curl -s -o /dev/null -X POST localhost:8080/members -d '{"name":"Ada Lovelace","email":"ada@example.com"}'
show() { echo "### $1"; curl -si "${@:2}" | grep -v '^Date:'; echo; }
show post201 -X POST localhost:8080/books -d '{"title":"Dune","author":"Frank Herbert","year":1965}'
show get200 localhost:8080/books/1
curl -s -o /dev/null -X POST localhost:8080/loans -d '{"bookId":1,"memberId":1}'
show loan409 -X POST localhost:8080/loans -d '{"bookId":1,"memberId":1}'
show get404 localhost:8080/books/99
show patch405 -X PATCH localhost:8080/books/1
kill $PID; wait $PID 2>/dev/null
rm -rf "$RUN"
