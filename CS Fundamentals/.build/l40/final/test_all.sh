#!/bin/sh
# Run both test suites; print one summary line per project, plus any failures.
status=0
for p in books_kt books_java; do
  out=$(cd "$p" && ./gradlew test --console=plain 2>&1)
  rc=$?
  echo "[$p] $(echo "$out" | grep -c ' > .* PASSED$') passed, $(echo "$out" | grep -c ' > .* FAILED$') failed"
  echo "$out" | grep -E ' > .* FAILED$|AssertionFailedError' | sed 's/^/    /'
  [ "$rc" -eq 0 ] || status=1
done
exit $status
