# Mini project M2 · Mini Search Engine

A search engine over a folder of text files: a tokeniser, an inverted index
with tf-idf ranking, trie autocomplete, heap-based top-k, and a doubling
experiment that shows why an index beats scanning. The full spec, milestones,
hints and review checklist are on the project page:
projects/m2-search-engine.html. This file is the short terminal version.

## Folders

    starter_java/  starter_kotlin/     pick ONE (Java recommended for M2)
    check/Check.java                   the black-box checker (JDK only)
    corpus/                            313 of Aesop's fables, one file each (public domain)
    solution_java/  solution_kotlin/   reference solutions: open only after the checker is green
    bugs/                              seeded bugs that prove the checker catches classic mistakes
    bugs/verify_bugs.sh                runs the checker against every seeded bug
    tools/SplitFables.java             rebuilds corpus/ from the source text (see its header)

## Set up (once)

    M2="/path/to/CS Fundamentals/projects/m2"    # adjust
    mkdir -p ~/katas && cp -R "$M2/starter_java" ~/katas/m2
    cp -R "$M2/corpus" ~/katas/m2/corpus
    cd ~/katas/m2
    git init -b main && git add . && git commit -m "Starter"
    git switch -c search-engine
    ./gradlew installDist

Turn AI completion off in IntelliJ for this project.

## Run your program

    build/install/search/bin/search corpus
    loaded 313 documents, 19284 tokens, 4327 terms

    q fox grapes
    results: 1
    1. the-fox-and-the-grapes 22.2610

    quit

Commands: tokens <text>, stats, doc <name>, q <terms>, or <terms>,
prefix <p>, top <k>, bench <reps> <terms>, quit.

## Run the checker

Rebuild first; the checker runs whatever installDist produced. Run it from
the course's projects/m2 folder so it finds corpus/ (or pass --corpus DIR).

    cd ~/katas/m2 && ./gradlew installDist
    cd "$M2" && java check/Check.java ~/katas/m2/build/install/search/bin/search --stage 1

`--stage N` runs stages 1..N (1 load and stats, 2 tokenisation, 3 AND/OR
queries and ranking, 4 prefix and top-k, 5 performance). Leave it off to run
all. Each check prints PASS or FAIL; a FAIL shows the input, the expected and
actual output, and a hint. Exit code 0 means all passed. Stage 5 builds
corpora of 2,000 to 16,000 generated documents in a temp folder and takes
a few seconds.

## Unit tests

    ./gradlew test

The starter has one passing example test and two red ones (TokenizerTest).
Aim for at least 15 tests of your own, including property-style tests that
compare your index, trie and heap against brute force on seeded random input.
