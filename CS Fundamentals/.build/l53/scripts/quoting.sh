#!/usr/bin/env bash
# Why every "$variable" in a script wears quotes
f="my notes.txt"
touch "$f"
echo "--- ls -l \$f (unquoted)"
ls -l $f
echo "--- ls -l \"\$f\" (quoted)"
ls -l "$f"
