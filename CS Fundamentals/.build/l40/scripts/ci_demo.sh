#!/bin/bash
# Part 3: commit a CI workflow, then run exactly what it runs in a clean clone.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
newrepo "$L40W/repos/ci"
rsync -a --exclude .gradle --exclude build --exclude .kotlin "$L40W/fix_kotlin/" .
mkdir -p .github/workflows && cp "$L40W/ci/ci.yml" .github/workflows/ci.yml
git add . && tick && git commit -q -m "Add book catalog with CI"
rm -rf "$L40W/repos/ci_clone"
cd "$L40W/repos"
show git clone -q ci ci_clone
show cd ci_clone
show ls -a
show git ls-files .github
show ./gradlew build
