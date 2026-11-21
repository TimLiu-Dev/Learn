#!/bin/zsh
# Reproduces every coverage and property capture in ../captures/cov_*.txt and prop_*.txt.
# Usage: zsh run.sh <empty scratch dir>   (never builds inside the course folder; cap_* stay untouched)
set -e
HERE=${0:A:h}; L45=${HERE:h}; OUT=$1; CAP=$L45/captures
mkdir -p $OUT && rm -rf $OUT/ck $OUT/cj $OUT/demo
cp -R $L45/cap_kotlin $OUT/ck; cp -R $L45/cap_java $OUT/cj; cp -R $HERE/demo $OUT/demo

# 1. JaCoCo on both capstones: add the plugin and the report task.
for p in ck cj; do
  python3 - $OUT/$p/app/build.gradle.kts <<'PY'
import sys; p = sys.argv[1]; s = open(p).read()
s = s.replace("    application\n}", "    application\n    jacoco\n}", 1)
s += "\ntasks.jacocoTestReport {\n    dependsOn(tasks.test)\n    reports {\n        html.required = true\n        csv.required = true\n    }\n}\n"
open(p, "w").write(s)
PY
  (cd $OUT/$p && ./gradlew -q --console=plain test jacocoTestReport > /dev/null)
done
python3 $HERE/summarize.py $OUT/cj/app/build/reports/jacoco/test > $CAP/cov_java.txt
python3 $HERE/summarize.py $OUT/ck/app/build/reports/jacoco/test > $CAP/cov_kotlin.txt
python3 $HERE/redlines.py $OUT/cj/app/build/reports/jacoco/test/html > $CAP/cov_java_red.txt

# 2. The demo: 100% line and branch coverage, and a bug.
(cd $OUT/demo && gradle --console=plain test jacocoTestReport > $OUT/demo_test.txt 2>&1 || true)
{ cat $OUT/demo_test.txt | grep -E "PASSED|FAILED"; python3 $HERE/summarize.py $OUT/demo/build/reports/jacoco/test; } > $CAP/cov_demo.txt

# 3. The property tests: green on the real code, red with a planted off-by-one.
cp $HERE/LendingPropertyTest.kt $OUT/ck/app/src/test/kotlin/shelf/service/
cp $HERE/LendingPropertyTest.java $OUT/cj/app/src/test/java/shelf/service/
(cd $OUT/ck && ./gradlew --console=plain test --tests '*LendingPropertyTest' 2>&1 | grep -E "LendingPropertyTest >|BUILD" > $CAP/prop_kt_green.txt)
(cd $OUT/cj && ./gradlew --console=plain test --tests '*LendingPropertyTest' 2>&1 | grep -E "LendingPropertyTest >|BUILD" > $CAP/prop_jv_green.txt)
sed -i '' 's/>= MAX_OPEN_LOANS/> MAX_OPEN_LOANS/' $OUT/ck/app/src/main/kotlin/shelf/service/LendingService.kt
grep -q '> MAX_OPEN_LOANS) return LendResult.LimitReached' $OUT/ck/app/src/main/kotlin/shelf/service/LendingService.kt
(cd $OUT/ck && ./gradlew --console=plain test --tests '*LendingPropertyTest' > $OUT/prop_kt_red_full.txt 2>&1 || true)
python3 - $OUT/prop_kt_red_full.txt > $CAP/prop_kt_red.txt <<'PY'
import sys
lines = open(sys.argv[1]).read().splitlines()
start = next(i for i, l in enumerate(lines) if "LendingPropertyTest >" in l and "FAILED" in l)
keep = [lines[start]]
for l in lines[start + 1:]:
    if l.strip().startswith("at "): break
    keep.append(l)
print("\n".join(keep).rstrip())
PY
echo done
