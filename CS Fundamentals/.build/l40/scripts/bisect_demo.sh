#!/bin/bash
# Part 2c: git bisect run finds the commit that broke the checkout total.
set -e
L40W="$1"; source "$L40W/scripts/lib.sh"
newrepo "$L40W/repos/bisect"
cp "$L40W/src/bisect/Checkout.java" .
cp "$L40W/src/bisect/check.sh" . && chmod +x check.sh
git add . && tick && git commit -q -m "Add checkout"
for i in $(seq 2 16); do
  if [ "$i" = 11 ]; then
    sed -i '' 's|return priceCents \* percent / 100;|return percent / 100 * priceCents;   // same maths, reordered|' Checkout.java
    git add . && tick && git commit -q -m "Tidy discount maths"
  else
    echo "Release note $i" >> NOTES.md
    git add . && tick && git commit -q -m "Update release notes ($i)"
  fi
done
show git log --oneline --reverse
show ./check.sh "&&" echo good "||" echo bad
FIRST=$(git rev-list --max-parents=0 HEAD | cut -c1-7)
show git bisect start HEAD $FIRST
show git bisect run ./check.sh
show git bisect reset
