cat scores.txt
java Grade.java < scores.txt; echo "exit $?"
java Grade.java < scores.txt > grades.txt 2> errors.txt; echo "exit $?"
cat grades.txt
cat errors.txt
java Grade.java < /dev/null; echo "exit $?"
printf '90\n85\n' | java Grade.java && echo "all good"
java Grade.java < scores.txt > /dev/null 2>&1 || echo "some lines were bad"
kotlinc Grade.kt -include-runtime -d grade.jar 2>/dev/null && java -jar grade.jar < scores.txt; echo "exit $?"
