#!/bin/bash
# usage: go.sh Main.kt Other.kt ... [-- args]   or   go.sh Main.java [-- args]
# Kotlin: compiles all listed .kt files with sqlite-jdbc on the classpath, runs the first file's main.
# Java: runs the first .java file with the JDK's multi-file source launcher (JEP 458).
set -e
W="/Users/tl/Documents/Learns/CS Fundamentals"
JAR="$W/.build/libs/sqlite-jdbc-3.53.4.0.jar"
K="$W/.build/tools/kotlinc/bin/kotlinc"
files=(); while [ $# -gt 0 ] && [ "$1" != "--" ]; do files+=("$1"); shift; done; [ "$1" = "--" ] && shift
case "${files[0]}" in
  *.kt) d="$(mktemp -d)"; "$K" "${files[@]}" -cp "$JAR" -include-runtime -d "$d/app.jar" 2>&1 | grep -v '^$' || true
        main="$(basename "${files[0]}" .kt)Kt"
        java --enable-native-access=ALL-UNNAMED -cp "$d/app.jar:$JAR" "$main" "$@"; rc=$?; rm -rf "$d"; exit $rc;;
  *.java) exec java --enable-native-access=ALL-UNNAMED -cp "$JAR" "${files[0]}" "$@";;
esac
