## rm -rf backup
./backup_broken.sh "my notes.txt" missing.txt; echo "exit $?"
ls backup/*
rm -r backup
./backup.sh "my notes.txt" scores.txt; echo "exit $?"
ls backup/*
./backup.sh "my notes.txt" missing.txt; echo "exit $?"
