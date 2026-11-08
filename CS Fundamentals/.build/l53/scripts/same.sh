#!/usr/bin/env bash
# same.sh Name [args...]: run Name.kt and Name.java with the same arguments
# and check that they print exactly the same thing.
set -euo pipefail                      # stop on errors, unset variables and failed pipes

if [[ $# -lt 1 ]]; then
  echo "usage: $0 Name [args...]" >&2
  exit 2
fi
name="$1"
shift                                  # the rest of "$@" goes to the programs

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT              # clean up however the script ends

kotlinc "$name.kt" -include-runtime -d "$tmp/$name.jar"
java -jar "$tmp/$name.jar" "$@" > "$tmp/kotlin.txt"
java "$name.java" "$@" > "$tmp/java.txt"

if diff -u "$tmp/kotlin.txt" "$tmp/java.txt"; then
  echo "same output from $name.kt and $name.java ($(wc -l < "$tmp/java.txt" | tr -d ' ') lines)"
else
  echo "outputs differ" >&2
  exit 1
fi
