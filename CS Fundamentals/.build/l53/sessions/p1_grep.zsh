## [[ $PWD == */l53w/home/katas/l53 ]] && rm -rf -- *(N) ; cp "/Users/tl/Documents/Learns/CS Fundamentals/.build/l53"/*.kt "/Users/tl/Documents/Learns/CS Fundamentals/.build/l53"/*.java . ; cp "/Users/tl/Documents/Learns/CS Fundamentals/.build/l53"/books.json .
java GenLog.java
ls -lh requests.log
wc -l requests.log
head -3 requests.log
grep -c ' 500 ' requests.log
grep ' 500 ' requests.log | head -3
grep -E ' (404|500) ' requests.log | grep -c POST
grep -v ' 200 ' requests.log | wc -l
grep -n 'GET /metrics 500' requests.log | head -2
grep -rn 'requests.log' --include='*.kt' .
