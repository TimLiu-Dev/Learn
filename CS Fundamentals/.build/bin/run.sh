#!/bin/bash
# usage: run.sh file.kt|file.java ... : compile and run each, printing output
K="${KOTLINC:-kotlinc}"   # set KOTLINC to a kotlinc binary if it is not on PATH
for f in "$@"; do
  echo "===== $f"
  case "$f" in
    *.kt) d="$(mktemp -d)"; j="$d/$(basename "$f" .kt).jar"; "$K" "$f" -include-runtime -d "$j" 2>&1 | grep -v '^$' ; [ -f "$j" ] && java -jar "$j" 2>&1; rm -rf "$d";;
    *.java) java "$f" 2>&1;;
  esac
done
