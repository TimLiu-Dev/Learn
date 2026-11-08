#!/usr/bin/env bash
# backup_broken.sh FILE...: copy files into backup/<date>/  (four bugs, on purpose)
dest=backup/$(date +%Y%m%d)
mkdir -p $dest
for f in $*; do
  cp $f $dest
done
echo "backed up $# files to $dest"
