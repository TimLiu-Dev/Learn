# M4 · Parallel Job Runner

The full page, with the spec, milestones and hints: ../m4-job-runner.html

## Setup

    mkdir -p ~/katas && cp -R starter_java ~/katas/m4      # or starter_kotlin
    cd ~/katas/m4 && git init -b main && git add . && git commit -m "M4 starter"
    git switch -c runner
    ./gradlew installDist
    build/install/runner/bin/runner jobs/example.txt --workers 2 --retries 2

The launcher is app/build/install/runner/bin/runner. (Gradle puts it under app/
because the project lives in the app subproject.)

## The checker

From ~/katas/m4, with COURSE set to the course folder:

    ./gradlew installDist && java "$COURSE/projects/m4/check/Check.java" app/build/install/runner/bin/runner --stage 1

--stage N runs stages 1..N and stops after the first stage with a FAIL.
Stages: 1 parse and run one at a time · 2 parallelism and backpressure ·
3 retries, backoff, timeouts · 4 dependencies, skips, cycles ·
5 graceful shutdown on SIGTERM · 6 stress (1,000 jobs).
A full run takes about 15 seconds. Leave out --stage to run everything.

## The command

    runner <jobs-file> --workers N [--retries R] [--timeout-ms T] [--queue Q] [--grace-ms G]

Defaults: R=0, T=0 (no timeout), Q=16, G=3000. Exit codes: 0 every job done,
1 some job not done, 2 bad input, 143 stopped by SIGTERM.

## What's here

    starter_kotlin/, starter_java/    Gradle 9.7.1 projects to copy (JDK 25 toolchain)
    check/Check.java                  the checker (JDK only, one file)
    solution_kotlin/, solution_java/  reference solutions: open them only after the checker is green
    bugs/                             seeded bugs the checker must catch (BUG.txt + patch.diff each)
    tools/verify.sh                   builds both solutions and every bug, runs the checker on each
