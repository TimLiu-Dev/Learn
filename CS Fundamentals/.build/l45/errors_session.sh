#!/bin/bash
# Sends four bad bodies to Shelf to compare how kotlinx.serialization and Jackson 3 report them.
set -u
PROJ="$1"
RUN=$(mktemp -d)
( cd "$PROJ" && ./gradlew -q installDist ) || exit 1
cd "$RUN"
"$PROJ/app/build/install/app/bin/app" > server.log 2>&1 &
PID=$!
for i in $(seq 1 50); do curl -s localhost:8080/health >/dev/null 2>&1 && break; sleep 0.2; done
show() { echo "\$ $*"; eval "$@"; echo; }
show "curl -s -X POST localhost:8080/books -d '{\"title\": oops}'"
show "curl -s -X POST localhost:8080/books -d '{\"title\":\"Dune\",\"author\":\"Herbert\",\"pages\":412}'"
show "curl -s -X POST localhost:8080/books -d '{\"title\":\"Dune\",\"author\":\"Herbert\",\"year\":\"abc\"}'"
show "curl -s -X POST localhost:8080/books"
show "curl -s -X POST localhost:8080/members -d '{\"name\":\" \",\"email\":\"ada\"}'"
show "curl -s -X POST localhost:8080/members -d '{\"name\":\"Ada\",\"email\":\"ADA@Example.com\"}'"
show "curl -s -X POST localhost:8080/members -d '{\"name\":\"Ada again\",\"email\":\"ada@example.com\"}'"
show "curl -s -X POST localhost:8080/loans -d '{\"bookId\":1}'"
kill $PID; wait $PID 2>/dev/null
rm -rf "$RUN"
