#!/usr/bin/env bash
# Proves the checker catches every seeded bug: for each bugs/<name>/, copy solution_java,
# overlay the bug's files, build it, run the checker, and show the FAIL lines.
# Usage (from projects/m2):  bash bugs/verify_bugs.sh [bug-name ...]
set -euo pipefail
cd "$(dirname "$0")/.."
names=("$@")
if [ ${#names[@]} -eq 0 ]; then
  for d in bugs/*/; do names+=("$(basename "$d")"); done
fi
status=0
for name in "${names[@]}"; do
  tmp="$(mktemp -d)"
  rsync -a --exclude build --exclude .gradle --exclude .kotlin solution_java/ "$tmp/"
  cp -R "bugs/$name/src" "$tmp/"
  (cd "$tmp" && ./gradlew -q installDist >/dev/null)
  echo "=== $name"
  if java check/Check.java "$tmp/build/install/search/bin/search" > "$tmp/check.txt"; then
    echo "NOT CAUGHT: every check passed"; status=1
  else
    grep -A4 '^FAIL' "$tmp/check.txt" | head -12
    tail -1 "$tmp/check.txt"
  fi
  rm -rf "$tmp"
done
exit $status
