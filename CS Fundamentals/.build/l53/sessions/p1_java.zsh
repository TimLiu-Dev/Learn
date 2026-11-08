## TIMEFMT='real %*E s   user %*U s   cpu %P'
kotlinc TopPaths.kt -include-runtime -d toppaths.jar 2>/dev/null
java -jar toppaths.jar
java TopPaths.java
cut -d' ' -f3 requests.log | sort | uniq -c | sort -rn | head -5 > shell.txt
java -jar toppaths.jar > kotlin.txt; java TopPaths.java > java.txt
diff shell.txt kotlin.txt && diff shell.txt java.txt && echo "all three agree"
time (java -jar toppaths.jar > /dev/null)
time (java TopPaths.java > /dev/null)
kotlinc Pipe.kt -include-runtime -d pipe.jar 2>/dev/null && java -jar pipe.jar
java Pipe.java
