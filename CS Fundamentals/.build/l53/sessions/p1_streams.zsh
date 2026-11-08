ls requests.log nosuch.log
ls requests.log nosuch.log 2>/dev/null
ls requests.log nosuch.log > out.txt
cat out.txt
ls requests.log nosuch.log > out.txt 2>&1
cat out.txt
ls nosuch.log | wc -l
ls nosuch.log 2>&1 | wc -l
