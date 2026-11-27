## rm -rf linkdemo; mkdir linkdemo
## cd linkdemo
echo hello > notes.txt
ln notes.txt hard.txt
ln -s notes.txt soft.txt
ls -li
rm notes.txt
cat hard.txt
cat soft.txt
mkdir -p releases/v1 releases/v2 && ln -s releases/v1 current
ln -s releases/v2 next && mv next current
ls -l current releases/v1
ln -s releases/v2 next && mv -h next current
ls -l current
## cd ..
java Links.java
kotlinc Links.kt -include-runtime -d links.jar 2>/dev/null && java -jar links.jar
