mkdir -p tmpfiles && cd tmpfiles && touch a.tmp b.tmp 'my notes.tmp' keep.txt
ls
find . -name '*.tmp' | xargs rm
ls
find . -name '*.tmp' -print0 | xargs -0 rm
ls
cd .. && rm -r tmpfiles
