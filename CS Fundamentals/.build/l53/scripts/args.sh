#!/usr/bin/env bash
# "$@" keeps each argument whole; $* splits them again
echo "count: $#"
for a in "$@"; do echo "  \"\$@\" gives [$a]"; done
for a in $*; do echo "  \$*   gives [$a]"; done
