#!/bin/bash
# usage: cap.sh Name [args...] : compile+run Name.kt and Name.java, save outputs to out/Name_kt.txt and out/Name_java.txt
set -u
D="$(cd "$(dirname "$0")" && pwd)"; L="$D/../libs"; K="$D/../tools/kotlinc/bin/kotlinc"
SCP="$L/kotlinx-serialization-core-jvm-1.12.0.jar:$L/kotlinx-serialization-json-jvm-1.12.0.jar"
JCP="$L/jackson-databind-3.2.4.jar:$L/jackson-core-3.2.4.jar:$L/jackson-annotations-2.22.jar"
n="$1"; shift; cd "$D"
if [ -f "$n.kt" ]; then
  t="$(mktemp -d)"
  "$K" -cp "$SCP" "$n.kt" -include-runtime -d "$t/$n.jar" 2>&1 | grep -v '^$' | grep -v '^warning: unable to find kotlin-stdlib' 
  perl -e 'alarm 300; exec @ARGV' java -cp "$t/$n.jar:$SCP" "${n}Kt" "$@" > "out/${n}_kt.txt" 2>&1; echo "kt exit $?"; rm -rf "$t"
fi
if [ -f "$n.java" ]; then
  perl -e 'alarm 300; exec @ARGV' java -cp "$JCP" "$n.java" "$@" > "out/${n}_java.txt" 2>&1; echo "java exit $?"
fi
