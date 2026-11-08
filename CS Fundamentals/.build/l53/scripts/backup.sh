#!/usr/bin/env bash
# backup.sh FILE...: copy files into backup/<date>/
set -euo pipefail
dest="backup/$(date +%Y%m%d)"
mkdir -p "$dest"
for f in "$@"; do
  cp -- "$f" "$dest/"
done
echo "backed up $# files to $dest"
