## rm -f access.jsonl shell.txt kotlin.txt java.txt; kotlinc GenJsonLog.kt -include-runtime -d genjson.jar 2>/dev/null
## TIMEFMT='real %*E s   user %*U s'
java GenJsonLog.java
java -jar genjson.jar && shasum access.jsonl
ls -lh access.jsonl
head -2 access.jsonl
head -1 access.jsonl | jq .
./logq_broken.sh access.jsonl; echo "exit $?"
bash -c 'set -o pipefail; seq 1 100000 | head -1; echo "exit $?"'
bash -c 'set -o pipefail; seq 1 100000 | head -1; echo "PIPESTATUS: ${PIPESTATUS[*]}"'
./logq.sh access.jsonl; echo "exit $?"
./logq.sh access.jsonl > shell.txt
kotlinc LogQ.kt -include-runtime -d logq.jar 2>/dev/null && java -jar logq.jar access.jsonl > kotlin.txt
java LogQ.java access.jsonl > java.txt
diff shell.txt kotlin.txt && diff shell.txt java.txt && echo "all three agree"
time ./logq.sh access.jsonl > /dev/null
time java -jar logq.jar access.jsonl > /dev/null
time java LogQ.java access.jsonl > /dev/null
./logq.sh; echo "exit $?"
