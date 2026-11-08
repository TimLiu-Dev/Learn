## rm -rf demo note.txt note.txt.sig; mkdir demo
ssh-keygen -t ed25519 -C "katas demo key" -f demo/id_demo -N ""
ls -l demo
cat demo/id_demo.pub
ssh-keygen -l -f demo/id_demo.pub
java SshPub.java demo/id_demo.pub
kotlinc SshPub.kt -include-runtime -d sshpub.jar 2>/dev/null && java -jar sshpub.jar demo/id_demo.pub
head -1 demo/id_demo
chmod 644 demo/id_demo && ssh-keygen -y -f demo/id_demo; echo "exit $?"
chmod 600 demo/id_demo && ssh-keygen -y -f demo/id_demo
echo "the library opens at 9" > note.txt
ssh-keygen -Y sign -f demo/id_demo -n file note.txt
echo "katas@example.com $(cut -d' ' -f1,2 demo/id_demo.pub)" > demo/allowed_signers
ssh-keygen -Y verify -f demo/allowed_signers -I katas@example.com -n file -s note.txt.sig < note.txt
echo "the library opens at 6" > note.txt
ssh-keygen -Y verify -f demo/allowed_signers -I katas@example.com -n file -s note.txt.sig < note.txt; echo "exit $?"
