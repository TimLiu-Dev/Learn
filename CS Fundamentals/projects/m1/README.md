# Mini project M1 · Budget CLI

A command-line budgeting tool over a bank-statement CSV. The full spec,
milestones, hints and review checklist are on the project page:
projects/m1-budget-cli.html. This file is the short terminal version.

## Folders

    starter_kotlin/   starter_java/    pick ONE (Kotlin recommended for M1)
    check/Check.java                   the black-box checker (JDK only)
    data/                              sample statements, rules.txt, budgets.txt
    solution_kotlin/  solution_java/   reference solutions: open only after the checker is green
    bugs/                              seeded bugs that prove the checker catches classic mistakes
    tools/GenStatements.java           regenerates data/*.csv from fixed seeds
    tools/check_bugs.sh                runs the checker against every seeded bug

## Set up (once)

    M1="/path/to/CS Fundamentals/projects/m1"    # adjust
    mkdir -p ~/katas && cp -R "$M1/starter_kotlin" ~/katas/m1
    cd ~/katas/m1
    git init -b main && git add . && git commit -m "Starter"
    git switch -c budget-cli
    ./gradlew installDist

Turn AI completion off in IntelliJ for this project.

## Run your program

    build/install/budget/bin/budget --help
    build/install/budget/bin/budget validate data/statement_2026q1.csv
    build/install/budget/bin/budget report data/statement_2026q1.csv --rules data/rules.txt --month 2026-02

## Run the checker

Rebuild first; the checker runs whatever installDist produced.

    ./gradlew installDist && java "$M1/check/Check.java" build/install/budget/bin/budget --stage 1

`--stage N` runs stages 1..N (1 CLI and validate, 2 CSV and categorise,
3 monthly report, 4 top and budget, 5 edge cases). Leave it off to run all.
Each check prints PASS or FAIL; a FAIL shows the input files, the command,
the expected and actual output, and a hint. Exit code 0 means all passed.

## Unit tests

    ./gradlew test

The starter has two passing example tests and one red one (MoneyTest).
Aim for at least 15 tests of your own, written before the code they test.
