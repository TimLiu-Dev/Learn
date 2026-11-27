// M4 · Parallel Job Runner: the black-box checker.
//
//   java check/Check.java <path-to-launcher> [--stage N]
//
// It runs your finished program (build/install/runner/bin/runner from ./gradlew installDist)
// on jobs files it writes into a temp directory, reads the event lines it prints, and checks
// the rules of the spec. Stages 1..N run in order; it stops after the first stage with a FAIL.
// Ordering between independent jobs is never fixed: only the rules are checked, so a correct
// program passes every time. Timing bounds are generous on purpose.

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {
    static final int STAGES = 6;
    static final String[] STAGE_NAMES = {
        "", "parse and run one job at a time", "parallelism and backpressure", "retries, backoff and timeouts",
        "dependencies, skips and cycles", "graceful shutdown on SIGTERM", "stress: 1,000 jobs"
    };
    static final Pattern LINE = Pattern.compile("t=(\\d{4,}) (\\S+)(?: (.*))?");
    static final Set<String> TERMINAL = Set.of("done", "giveup", "skip", "cancel", "drop");

    static Path launcher;
    static Path dir;
    static int passed = 0, failed = 0;
    static boolean stageFailed = false;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("usage: java check/Check.java <path-to-launcher> [--stage N]");
            System.out.println("  e.g. java check/Check.java app/build/install/runner/bin/runner --stage 1");
            System.exit(2);
        }
        launcher = Path.of(args[0]).toAbsolutePath();
        int upTo = STAGES;
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--stage") && i + 1 < args.length) upTo = Integer.parseInt(args[++i]);
        }
        upTo = Math.max(1, Math.min(STAGES, upTo));
        dir = Files.createTempDirectory("m4check");

        if (!Files.isExecutable(launcher)) {
            fail(1, "finds the launcher", "no executable file at " + launcher,
                "hint:     run ./gradlew installDist first; the launcher is app/build/install/runner/bin/runner "
                    + "(application { applicationName = \"runner\" } in app/build.gradle.kts).");
            summary(1);
            return;
        }

        for (int stage = 1; stage <= upTo; stage++) {
            stageFailed = false;
            switch (stage) {
                case 1 -> stage1();
                case 2 -> stage2();
                case 3 -> stage3();
                case 4 -> stage4();
                case 5 -> stage5();
                case 6 -> stage6();
                default -> { }
            }
            if (stageFailed) {
                if (stage < upTo) System.out.println("\nStopped after stage " + stage + " (" + STAGE_NAMES[stage]
                    + "): fix it before the next stage.");
                summary(stage);
                return;
            }
        }
        summary(upTo);
    }

    static void summary(int stage) {
        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed (stages 1.." + stage + ")");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ───────────────────────────── stages ─────────────────────────────

    static void stage1() throws Exception {
        scenario(1, "runs 3 jobs one at a time (--workers 1)",
            "id=a duration=100\nid=b duration=150\nid=c duration=50\n", "--workers 1", (sc, r, ev) -> {
                List<String> starts = ev.ofKind("start").stream().map(e -> e.id).toList();
                if (!starts.equals(List.of("a", "b", "c")))
                    return "expected: start a, start b, start c (one worker, no deps: file order)\n"
                        + "got:      " + starts;
                return null;
            });
        scenario(1, "ignores comments and blank lines",
            "# a comment line\n\nid=x duration=20\n   \n# another one\nid=y duration=20 fail=0 deps=\n",
            "--workers 1", null);
        scenario(1, "an empty jobs file gives an empty summary", "# nothing to do\n", "--workers 2", null);
        inputError(1, "rejects an unknown key", "id=a duration=10\nid=b duration=10 colour=red\n", "--workers 1",
            List.of("line 2", "colour"));
        inputError(1, "rejects a duplicate id", "id=a duration=10\nid=a duration=20\n", "--workers 1",
            List.of("line 2", "duplicate"));
        inputError(1, "rejects a duration that isn't a number", "id=a duration=soon\n", "--workers 1",
            List.of("line 1", "duration"));
        inputError(1, "rejects a missing --workers", "id=a duration=10\n", "", List.of("--workers"));
        inputError(1, "rejects --workers 0", "id=a duration=10\n", "--workers 0", List.of("--workers"));
        inputErrorRaw(1, "rejects a jobs file that doesn't exist", List.of("no-such-file.txt", "--workers", "1"),
            List.of("no-such-file.txt"));
    }

    static void stage2() throws Exception {
        scenario(2, "8 jobs of 400 ms on 4 workers: 4 at once, about 800 ms", jobs(8, 400), "--workers 4",
            (sc, r, ev) -> {
                if (ev.maxConc != 4) return "expected: 4 attempts running at the same moment (the peak)\n"
                    + "got:      a peak of " + ev.maxConc + "\nhint:     8 jobs and 4 workers: the first 4 should start together.";
                long t = ev.summaryT;
                if (t < 780) return "expected: summary at t ≥ 780 ms (2 rounds of 400 ms)\ngot:      t=" + t
                    + "\nhint:     is the work really a sleep of duration ms?";
                if (t > 2400) return "expected: summary well before t=2400 ms (ideal: 800)\ngot:      t=" + t
                    + "\nhint:     are the jobs running in parallel, or one after another?";
                return null;
            });
        scenario(2, "7 jobs on 3 workers: never more than 3 at once", jobs(7, 300), "--workers 3", (sc, r, ev) ->
            ev.maxConc == 3 ? null : "expected: a peak of exactly 3 attempts at once\ngot:      " + ev.maxConc);
        scenario(2, "more workers than jobs: all 3 run together", jobs(3, 300), "--workers 10", (sc, r, ev) -> {
            if (ev.maxConc != 3) return "expected: a peak of 3 attempts at once\ngot:      " + ev.maxConc;
            if (ev.summaryT > 1500) return "expected: summary before t=1500 ms\ngot:      t=" + ev.summaryT;
            return null;
        });
        scenario(2, "a full queue makes the producer wait (--queue 2, --workers 1)", jobs(10, 80),
            "--workers 1 --queue 2", (sc, r, ev) ->
                ev.maxWaiting >= 2 ? null : "expected: the queue to fill up (at some point 2+ jobs queued but not started)\n"
                    + "got:      at most " + ev.maxWaiting + "\nhint:     print 'queued <id>' as soon as put/send returns.");
    }

    static void stage3() throws Exception {
        scenario(3, "fail=2 with --retries 3: two failures, backoff 50 then 100 ms, done on attempt 3",
            "id=flaky duration=100 fail=2\n", "--workers 1 --retries 3", (sc, r, ev) -> {
                if (!ev.has("done flaky attempt=3")) return "expected: t=… done flaky attempt=3\ngot:      " + ev.terminalOf("flaky");
                if (!ev.has("retry flaky attempt=2 delay=50") || !ev.has("retry flaky attempt=3 delay=100"))
                    return "expected: retry flaky attempt=2 delay=50 and retry flaky attempt=3 delay=100\n"
                        + "hint:     the delay before attempt k+1 is 50 × 2^(k−1) ms.";
                return null;
            });
        scenario(3, "fail=5 with --retries 2: three attempts, then giveup", "id=broken duration=60 fail=5\n",
            "--workers 1 --retries 2", (sc, r, ev) ->
                ev.has("giveup broken attempts=3") ? null
                    : "expected: t=… giveup broken attempts=3 (1 attempt + 2 retries)\ngot:      " + ev.terminalOf("broken"));
        scenario(3, "no --retries means no retry", "id=once duration=30 fail=1\nid=fine duration=30\n", "--workers 1",
            (sc, r, ev) -> ev.has("giveup once attempts=1") && ev.ofKind("retry").isEmpty() ? null
                : "expected: fail once attempt=1, then giveup once attempts=1, and no retry line\n"
                    + "hint:     --retries defaults to 0.");
        scenario(3, "a 4 s job with --timeout-ms 150 times out twice and the program exits at once",
            "id=slow duration=4000\n", "--workers 1 --timeout-ms 150 --retries 1", (sc, r, ev) -> {
                if (!ev.has("giveup slow attempts=2")) return "expected: timeout slow attempt=1, retry, timeout slow attempt=2, giveup slow attempts=2\n"
                    + "got:      " + ev.terminalOf("slow");
                if (ev.summaryT > 1500) return "expected: summary before t=1500 ms (two 150 ms attempts + 50 ms backoff)\n"
                    + "got:      t=" + ev.summaryT + "\nhint:     does something wait for the timed-out work to finish?";
                if (r.exitAfterSummaryMs > 1500) return "expected: the process exits right after the summary\n"
                    + "got:      it exited " + r.exitAfterSummaryMs + " ms after the summary line\n"
                    + "hint:     the timed-out work is still running and keeps the JVM alive (ExecutorService.close() waits for it). "
                    + "A timeout must cancel the work: future.cancel(true), or withTimeout in Kotlin.";
                return null;
            });
        scenario(3, "a job faster than its timeout succeeds", "id=quick duration=100\n", "--workers 1 --timeout-ms 1000",
            (sc, r, ev) -> ev.has("done quick attempt=1") ? null : "expected: done quick attempt=1\ngot:      " + ev.terminalOf("quick"));
        scenario(3, "retries on 2 workers: other jobs keep running",
            "id=a duration=200 fail=1\nid=b duration=200\nid=c duration=200\nid=d duration=200\n",
            "--workers 2 --retries 1", (sc, r, ev) -> ev.has("done a attempt=2") ? null
                : "expected: done a attempt=2\ngot:      " + ev.terminalOf("a"));
    }

    static void stage4() throws Exception {
        scenario(4, "a diamond: b and c wait for a, run together, then d",
            "id=a duration=200\nid=b duration=400 deps=a\nid=c duration=400 deps=a\nid=d duration=100 deps=b,c\n",
            "--workers 2", (sc, r, ev) -> {
                if (!ev.overlap("b", "c")) return "expected: b and c to run at the same time (both only need a; 2 workers)\n"
                    + "got:      they didn't overlap\nhint:     when a is done, both of its dependents are ready together.";
                return null;
            });
        scenario(4, "a failed job's dependents are skipped, others still run",
            "id=a duration=50 fail=9\nid=b duration=50 deps=a\nid=c duration=50 deps=b\nid=d duration=50\n",
            "--workers 2", (sc, r, ev) -> {
                if (!ev.has("skip b dep=a")) return "expected: skip b dep=a\ngot:      " + ev.terminalOf("b");
                if (!ev.has("skip c dep=b")) return "expected: skip c dep=b (a skipped job counts as not succeeded)\ngot:      " + ev.terminalOf("c");
                if (!ev.has("done d attempt=1")) return "expected: done d attempt=1 (d doesn't depend on a)\ngot:      " + ev.terminalOf("d");
                return null;
            });
        scenario(4, "dependents listed first, one worker: queued in topological order, no deadlock",
            "id=report duration=50 deps=build\nid=build duration=50 deps=fetch\nid=fetch duration=50\n", "--workers 1",
            (sc, r, ev) -> {
                List<String> starts = ev.ofKind("start").stream().map(e -> e.id).toList();
                return starts.equals(List.of("fetch", "build", "report")) ? null
                    : "expected: start fetch, start build, start report\ngot:      " + starts;
            });
        inputError(4, "reports a dependency cycle", "id=a duration=10 deps=c\nid=b duration=10 deps=a\nid=c duration=10 deps=b\n",
            "--workers 2", List.of("cycle", "a", "b", "c"));
        inputError(4, "reports a job that depends on itself", "id=a duration=10 deps=a\n", "--workers 1",
            List.of("cycle", "a"));
        inputError(4, "reports an unknown dependency", "id=a duration=10 deps=ghost\n", "--workers 1",
            List.of("ghost"));
    }

    static void stage5() throws Exception {
        String six = jobs(6, 1500);
        signalScenario(5, "SIGTERM: running jobs finish, the rest are dropped, exit 143", six,
            "--workers 2 --queue 2 --grace-ms 5000", 2, (sc, r, ev) -> {
                List<String> started = ev.ofKind("start").stream().map(e -> e.id).toList();
                for (String id : started) {
                    if (!ev.terminalOf(id).startsWith("done"))
                        return "expected: " + id + " was running at SIGTERM, so it finishes: done " + id + " attempt=1\n"
                            + "got:      " + ev.terminalOf(id) + "\nhint:     the hook must wait for running jobs (up to --grace-ms) before it returns.";
                }
                if (ev.count("drop") != 6 - started.size())
                    return "expected: " + (6 - started.size()) + " drop lines (every job that never started)\ngot:      " + ev.count("drop");
                if (r.afterSignalMs > 4500) return "expected: exit within ~1.5 s of SIGTERM\ngot:      " + r.afterSignalMs + " ms";
                return null;
            });
        signalScenario(5, "SIGTERM with --grace-ms 300: long jobs are cancelled after the grace period",
            jobs(4, 10_000), "--workers 2 --queue 1 --grace-ms 300", 2, (sc, r, ev) -> {
                for (Ev e : ev.ofKind("cancel")) {
                    long after = e.t - ev.shutdownT;
                    if (after < 298 || after > 1800)
                        return "expected: cancel lines about 300 ms after the shutdown line\n"
                            + "got:      " + e.raw + " (" + after + " ms after shutdown)";
                }
                if (ev.count("cancel") != 2) return "expected: 2 cancel lines (the two running jobs)\ngot:      " + ev.count("cancel")
                    + "\nhint:     after the grace period, cancel what is still running (future.cancel(true) / cancel the coroutines).";
                if (r.afterSignalMs > 3000) return "expected: exit within ~0.5 s of SIGTERM\ngot:      " + r.afterSignalMs + " ms";
                return null;
            });
    }

    static void stage6() throws Exception {
        Random rnd = new Random(42);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("id=s").append(i).append(" duration=").append(rnd.nextInt(4));
            int roll = rnd.nextInt(100);
            if (roll < 2) sb.append(" fail=9");
            else if (roll < 12) sb.append(" fail=1");
            if (i > 0 && rnd.nextInt(100) < 30) {
                Set<String> deps = new java.util.LinkedHashSet<>();
                int n = 1 + rnd.nextInt(2);
                for (int k = 0; k < n; k++) deps.add("s" + (i - 1 - rnd.nextInt(Math.min(i, 50))));
                sb.append(" deps=").append(String.join(",", deps));
            }
            sb.append('\n');
        }
        scenarioLimit(6, "1,000 mixed jobs (retries, failures, deps) on 16 workers: every event once, counts add up",
            sb.toString(), "--workers 16 --queue 8 --retries 1", 60_000, null);
        scenarioLimit(6, "1,000 instant jobs on 64 workers: no lost or duplicated events", jobs(1000, 0),
            "--workers 64 --queue 4", 60_000, null);
    }

    // ───────────────────────────── scenarios ─────────────────────────────

    interface Extra {
        String check(Scenario sc, Run r, Events ev);
    }

    record JobSpec(String id, long duration, int fail, List<String> deps) {}

    record Scenario(int stage, String name, String jobsText, List<String> args, List<JobSpec> jobs,
                    int workers, int retries, long timeout, int queue, long grace) {
        String command() {
            return "runner jobs.txt " + String.join(" ", args.subList(1, args.size()));
        }
    }

    static String jobs(int n, long duration) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) sb.append("id=j").append(i).append(" duration=").append(duration).append('\n');
        return sb.toString();
    }

    static Scenario make(int stage, String name, String text, String flags) throws IOException {
        Path file = dir.resolve("jobs.txt");
        Files.writeString(file, text);
        List<String> args = new ArrayList<>();
        args.add(file.toString());
        if (!flags.isBlank()) args.addAll(Arrays.asList(flags.trim().split("\\s+")));
        Map<String, String> f = new HashMap<>();
        for (int i = 1; i + 1 < args.size(); i += 2) f.put(args.get(i), args.get(i + 1));
        List<JobSpec> jobs = new ArrayList<>();
        for (String line : text.split("\n")) {
            line = line.strip();
            if (line.isEmpty() || line.startsWith("#")) continue;
            Map<String, String> kv = new HashMap<>();
            for (String tok : line.split("\\s+")) {
                int eq = tok.indexOf('=');
                if (eq > 0) kv.put(tok.substring(0, eq), tok.substring(eq + 1));
            }
            try {
                jobs.add(new JobSpec(kv.get("id"), Long.parseLong(kv.get("duration")), Integer.parseInt(kv.getOrDefault("fail", "0")),
                    Arrays.stream(kv.getOrDefault("deps", "").split(",")).filter(s -> !s.isEmpty()).toList()));
            } catch (RuntimeException e) {
                // a deliberately broken file for an input-error check: no spec needed
            }
        }
        return new Scenario(stage, name, text, args, jobs,
            Integer.parseInt(f.getOrDefault("--workers", "1")), Integer.parseInt(f.getOrDefault("--retries", "0")),
            Long.parseLong(f.getOrDefault("--timeout-ms", "0")), Integer.parseInt(f.getOrDefault("--queue", "16")),
            Long.parseLong(f.getOrDefault("--grace-ms", "3000")));
    }

    static void scenario(int stage, String name, String text, String flags, Extra extra) throws Exception {
        scenarioLimit(stage, name, text, flags, 20_000, extra);
    }

    static void scenarioLimit(int stage, String name, String text, String flags, long limitMs, Extra extra) throws Exception {
        Scenario sc = make(stage, name, text, flags);
        Run r = run(sc.args, limitMs, null, 0);
        judge(sc, r, false, extra);
    }

    static void signalScenario(int stage, String name, String text, String flags, int startsBeforeSignal, Extra extra)
            throws Exception {
        Scenario sc = make(stage, name, text, flags);
        Run r = run(sc.args, 20_000,
            lines -> lines.stream().filter(l -> l.matches("t=\\d+ start .*")).count() >= startsBeforeSignal, 300);
        if (r.signalNanos == 0 && !r.timedOut && r.startError == null) {
            fail(stage, name, "the program ended before " + startsBeforeSignal + " jobs had started, so no SIGTERM was sent",
                show(sc, r));
            return;
        }
        judge(sc, r, true, extra);
    }

    static void judge(Scenario sc, Run r, boolean signalled, Extra extra) {
        if (r.startError != null) {
            fail(sc.stage, sc.name, "could not start " + launcher + ": " + r.startError, "");
            return;
        }
        if (r.timedOut) {
            fail(sc.stage, sc.name, "still running after " + r.limitMs / 1000 + " s, so the checker killed it",
                show(sc, r) + "\nhint:     a stuck program is usually a deadlock. Classic one here: a job holds the only "
                    + "slot while it waits for a dependency that is still in the queue behind it (queue in topological order!). "
                    + "Also: a thread that never ends, or a shutdown hook that never returns.");
            return;
        }
        Events ev = new Events(sc);
        String problem = ev.validate(r.out, signalled);
        if (problem == null) {
            int expected = signalled ? 143 : ev.allDone() ? 0 : 1;
            if (r.exit != expected) {
                String why = signalled ? "SIGTERM ends a JVM with 143 (128 + 15) once the hook returns; don't call halt() or exit(0)."
                    : expected == 0 ? "every job succeeded, so exit 0." : "not every job succeeded, so exit 1.";
                problem = "expected: exit code " + expected + "\ngot:      exit code " + r.exit + "\nhint:     " + why;
            }
        }
        if (problem == null && signalled && r.afterSignalMs > 0 && !ev.sawShutdown)
            problem = "expected: a 't=… shutdown' line after SIGTERM\ngot:      none\nhint:     register a shutdown hook (L53).";
        if (problem == null && extra != null) problem = extra.check(sc, r, ev);
        if (problem == null) pass(sc.stage, sc.name);
        else fail(sc.stage, sc.name, problem, show(sc, r));
    }

    static void inputError(int stage, String name, String text, String flags, List<String> mustMention) throws Exception {
        Scenario sc = make(stage, name, text, flags);
        inputErrorCheck(sc, run(sc.args, 20_000, null, 0), mustMention);
    }

    static void inputErrorRaw(int stage, String name, List<String> args, List<String> mustMention) throws Exception {
        Scenario sc = new Scenario(stage, name, "(no file)", new ArrayList<>(List.of("x")), List.of(), 1, 0, 0, 16, 3000);
        List<String> full = new ArrayList<>(args);
        Run r = run(full, 20_000, null, 0);
        Scenario shown = new Scenario(stage, name, "(none: the file doesn't exist)", prepend("jobs.txt", full.subList(1, full.size())),
            List.of(), 1, 0, 0, 16, 3000);
        inputErrorCheck(shown, r, mustMention);
    }

    static List<String> prepend(String first, List<String> rest) {
        List<String> l = new ArrayList<>();
        l.add(first);
        l.addAll(rest);
        return l;
    }

    static void inputErrorCheck(Scenario sc, Run r, List<String> mustMention) {
        if (r.startError != null || r.timedOut) {
            judge(sc, r, false, null);
            return;
        }
        String err = String.join("\n", r.err);
        String problem = null;
        if (r.exit != 2) problem = "expected: exit code 2 (bad input)\ngot:      exit code " + r.exit;
        else if (r.err.isEmpty() || !r.err.getFirst().startsWith("error: "))
            problem = "expected: stderr starting with 'error: '\ngot:      " + (r.err.isEmpty() ? "(empty stderr)" : r.err.getFirst());
        else if (r.out.stream().anyMatch(l -> l.startsWith("t=")))
            problem = "expected: no event lines on stdout (check the whole input before running anything)\ngot:      " + r.out.getFirst();
        else {
            for (String word : mustMention) {
                if (!err.contains(word)) {
                    problem = "expected: the error to mention '" + word + "'\ngot:      " + r.err.getFirst();
                    break;
                }
            }
        }
        if (problem == null) pass(sc.stage, sc.name);
        else fail(sc.stage, sc.name, problem, show(sc, r));
    }

    // ───────────────────────────── running ─────────────────────────────

    static final class Run {
        List<String> out = new ArrayList<>();
        List<Long> outNanos = new ArrayList<>();
        List<String> err = new ArrayList<>();
        int exit = -1;
        boolean timedOut;
        long limitMs;
        String startError;
        volatile long signalNanos;
        long exitNanos;
        long exitAfterSummaryMs;
        long afterSignalMs;
    }

    static Run run(List<String> args, long limitMs, Predicate<List<String>> trigger, long delayMs) throws Exception {
        Run r = new Run();
        r.limitMs = limitMs;
        List<String> cmd = new ArrayList<>();
        cmd.add(launcher.toString());
        cmd.addAll(args);
        Process p;
        try {
            p = new ProcessBuilder(cmd).directory(dir.toFile()).start();
        } catch (IOException e) {
            r.startError = e.getMessage();
            return r;
        }
        p.getOutputStream().close();
        Thread outReader = Thread.ofPlatform().start(() -> {
            boolean fired = false;
            try (BufferedReader br = p.inputReader()) {
                String line;
                while ((line = br.readLine()) != null) {
                    List<String> snapshot;
                    synchronized (r) {
                        r.out.add(line);
                        r.outNanos.add(System.nanoTime());
                        snapshot = List.copyOf(r.out);
                    }
                    if (trigger != null && !fired && trigger.test(snapshot)) {
                        fired = true;
                        Thread.ofPlatform().start(() -> {
                            try {
                                Thread.sleep(delayMs);
                            } catch (InterruptedException ignored) {
                                return;
                            }
                            if (p.isAlive()) {
                                r.signalNanos = System.nanoTime();
                                // SIGTERM on macOS and Linux. Not p.destroy(): that also closes our end of
                                // the pipes, and we'd never see the drain output.
                                p.toHandle().destroy();
                            }
                        });
                    }
                }
            } catch (IOException ignored) {
                // the process is gone
            }
        });
        Thread errReader = Thread.ofPlatform().start(() -> {
            try (BufferedReader br = p.errorReader()) {
                String line;
                while ((line = br.readLine()) != null) synchronized (r) { r.err.add(line); }
            } catch (IOException ignored) {
                // the process is gone
            }
        });
        boolean finished = p.waitFor(limitMs, TimeUnit.MILLISECONDS);
        r.exitNanos = System.nanoTime();
        if (!finished) {
            r.timedOut = true;
            p.descendants().forEach(ProcessHandle::destroyForcibly);
            p.destroyForcibly();
            p.waitFor();
        }
        outReader.join(3000);
        errReader.join(3000);
        synchronized (r) {
            r.exit = p.exitValue();
            for (int i = r.out.size() - 1; i >= 0; i--) {
                if (r.out.get(i).matches("t=\\d+ summary.*")) {
                    r.exitAfterSummaryMs = (r.exitNanos - r.outNanos.get(i)) / 1_000_000;
                    break;
                }
            }
            if (r.signalNanos != 0) r.afterSignalMs = (r.exitNanos - r.signalNanos) / 1_000_000;
        }
        return r;
    }

    // ───────────────────────────── the rules ─────────────────────────────

    record Ev(int line, long t, String kind, String id, Map<String, String> kv, String raw) {
        int num(String key) {
            return Integer.parseInt(kv.get(key));
        }
    }

    static final class JobState {
        JobSpec spec;
        boolean queued;
        int attempts;
        Ev open;            // the attempt now running
        Ev lastEnd;         // the end of the last attempt
        Ev retry;           // the retry line after it
        Ev terminal;
    }

    static final class Events {
        final Scenario sc;
        final List<Ev> all = new ArrayList<>();
        final Map<String, JobState> jobs = new LinkedHashMap<>();
        int maxConc, maxWaiting;
        long summaryT = -1, shutdownT = -1;
        boolean sawShutdown;

        Events(Scenario sc) {
            this.sc = sc;
            for (JobSpec j : sc.jobs) {
                JobState s = new JobState();
                s.spec = j;
                jobs.put(j.id, s);
            }
        }

        List<Ev> ofKind(String kind) {
            return all.stream().filter(e -> e.kind.equals(kind)).toList();
        }

        long count(String kind) {
            return all.stream().filter(e -> e.kind.equals(kind)).count();
        }

        boolean has(String text) {
            return all.stream().anyMatch(e -> e.raw.substring(e.raw.indexOf(' ') + 1).equals(text));
        }

        String terminalOf(String id) {
            JobState s = jobs.get(id);
            return s == null || s.terminal == null ? "(no terminal line for " + id + ")" : s.terminal.raw.substring(s.terminal.raw.indexOf(' ') + 1);
        }

        boolean allDone() {
            return jobs.values().stream().allMatch(s -> s.terminal != null && s.terminal.kind.equals("done"));
        }

        boolean overlap(String a, String b) {
            long[] ia = interval(a), ib = interval(b);
            if (ia == null || ib == null) return false;
            return ia[0] < ib[1] && ib[0] < ia[1];
        }

        long[] interval(String id) {
            Ev start = null;
            for (Ev e : all) {
                if (!id.equals(e.id)) continue;
                if (e.kind.equals("start")) start = e;
                else if (start != null && Set.of("done", "fail", "timeout", "cancel").contains(e.kind))
                    return new long[] {start.line, e.line};
            }
            return null;
        }

        static String at(Ev e) {
            return "line " + e.line + ": \"" + e.raw + "\"";
        }

        /** Checks every rule of the spec. Returns the first problem, or null. */
        String validate(List<String> out, boolean signalled) {
            long lastT = -1;
            for (int i = 0; i < out.size(); i++) {
                String raw = out.get(i);
                Matcher m = LINE.matcher(raw);
                if (!m.matches()) return "expected: only event lines on stdout, like 't=0012 start a attempt=1'\n"
                    + "got:      line " + (i + 1) + ": \"" + raw + "\"\nhint:     print logs and errors to stderr; the time is at least 4 digits (%04d).";
                long t = Long.parseLong(m.group(1));
                if (t < lastT) return "expected: timestamps that never go back\ngot:      line " + (i + 1) + ": \"" + raw
                    + "\" after t=" + lastT + "\nhint:     read the clock inside the same lock that prints the line.";
                lastT = t;
                String rest = m.group(3) == null ? "" : m.group(3);
                String[] parts = rest.isEmpty() ? new String[0] : rest.split(" ");
                String kind = m.group(2);
                String id = null;
                Map<String, String> kv = new LinkedHashMap<>();
                int from = 0;
                if (!kind.equals("summary") && !kind.equals("shutdown")) {
                    if (parts.length == 0) return "expected: an id after '" + kind + "'\ngot:      line " + (i + 1) + ": \"" + raw + "\"";
                    id = parts[0];
                    from = 1;
                }
                for (int k = from; k < parts.length; k++) {
                    int eq = parts[k].indexOf('=');
                    if (eq < 0) return "expected: key=value fields\ngot:      line " + (i + 1) + ": \"" + raw + "\"";
                    kv.put(parts[k].substring(0, eq), parts[k].substring(eq + 1));
                }
                String need = switch (kind) {
                    case "queued", "drop", "shutdown" -> "";
                    case "start", "done", "fail", "timeout", "cancel" -> "attempt";
                    case "retry" -> "attempt delay";
                    case "giveup" -> "attempts";
                    case "skip" -> "dep";
                    case "summary" -> "total done giveup skip cancel drop";
                    default -> null;
                };
                if (need == null) return "expected: one of queued start done fail timeout retry giveup skip cancel drop shutdown summary\n"
                    + "got:      line " + (i + 1) + ": \"" + raw + "\"";
                for (String key : need.split(" ")) {
                    if (key.isEmpty()) continue;
                    if (!kv.containsKey(key)) return "expected: '" + key + "=' on every " + kind + " line\ngot:      line " + (i + 1) + ": \"" + raw + "\"";
                    if (!key.equals("dep") && !kv.get(key).matches("\\d+"))
                        return "expected: a number after " + key + "=\ngot:      line " + (i + 1) + ": \"" + raw + "\"";
                }
                if (id != null && !jobs.containsKey(id)) return "expected: only ids from the jobs file\ngot:      line " + (i + 1) + ": \"" + raw + "\"";
                all.add(new Ev(i + 1, t, kind, id, kv, raw));
            }
            if (all.isEmpty() || !all.getLast().kind.equals("summary"))
                return "expected: the last line to be 't=… summary total=… done=… giveup=… skip=… cancel=… drop=…'\n"
                    + "got:      " + (all.isEmpty() ? "no output at all (if it crashed, see stderr below)" : "last line \"" + all.getLast().raw + "\"")
                    + (signalled ? "\nhint:     after SIGTERM the JVM halts as soon as your shutdown hook returns. The hook must wait "
                        + "until the drain is over and the summary is printed (a CountDownLatch)." : "");
            if (count("summary") != 1) return "expected: exactly one summary line\ngot:      " + count("summary");
            summaryT = all.getLast().t;

            int running = 0;
            Set<String> waiting = new HashSet<>();      // queued, not yet started, skipped or dropped
            for (Ev e : all) {
                JobState s = e.id == null ? null : jobs.get(e.id);
                if (s != null && s.terminal != null && !e.kind.equals("queued"))   // queued may print late (see below)
                    return "expected: nothing more about " + e.id + " after its last line " + at(s.terminal) + "\ngot:      " + at(e)
                        + "\nhint:     every job ends exactly once.";
                switch (e.kind) {
                    case "queued" -> {
                        if (s.queued) return "expected: each job queued once\ngot:      " + at(e) + " a second time";
                        for (String d : s.spec.deps) {
                            if (!jobs.get(d).queued) return "expected: " + e.id + " queued after its dependency " + d
                                + " (topological order)\ngot:      " + at(e) + " before 'queued " + d + "'\n"
                                + "hint:     sort the jobs with L21's topological sort before you queue them.";
                        }
                        s.queued = true;
                        // The producer prints after put() returns, so a worker may already have
                        // started (even finished) the job: then it isn't waiting.
                        if (s.attempts == 0 && s.terminal == null) waiting.add(e.id);
                        maxWaiting = Math.max(maxWaiting, waiting.size());
                        if (waiting.size() > sc.queue + sc.workers + 1)
                            return "expected: at most --queue " + sc.queue + " jobs waiting in the queue (plus one per worker)\n"
                                + "got:      " + waiting.size() + " queued but not started at " + at(e)
                                + "\nhint:     is the queue bounded? ArrayBlockingQueue(Q) / Channel(Q), and put()/send() must wait when it's full.";
                    }
                    case "start" -> {
                        int k = e.num("attempt");
                        if (sawShutdown) return "expected: no attempt starts after the shutdown line\ngot:      " + at(e)
                            + "\nhint:     check 'shutting down?' and print 'start' under the same lock as 'shutdown'.";
                        if (s.open != null) return "expected: one attempt of " + e.id + " at a time\ngot:      " + at(e)
                            + " while " + at(s.open) + " is still running";
                        if (k != s.attempts + 1) return "expected: start " + e.id + " attempt=" + (s.attempts + 1) + "\ngot:      " + at(e);
                        if (k == 1) {
                            for (String d : s.spec.deps) {
                                JobState ds = jobs.get(d);
                                if (ds.terminal == null || !ds.terminal.kind.equals("done"))
                                    return "expected: " + e.id + " to start only after its dependency " + d + " is done\ngot:      " + at(e)
                                        + " when " + d + " was " + (ds.terminal == null ? "not finished" : "'" + ds.terminal.raw + "'");
                            }
                            waiting.remove(e.id);
                        } else {
                            if (s.retry == null) return "expected: a 'retry " + e.id + " attempt=" + k + " delay=…' line before\n"
                                + "got:      " + at(e);
                            long delay = Long.parseLong(s.retry.kv.get("delay"));
                            long gap = e.t - s.lastEnd.t;
                            if (gap < delay - 2) return "expected: attempt " + k + " of " + e.id + " at least " + delay + " ms after "
                                + at(s.lastEnd) + "\ngot:      " + at(e) + ", only " + gap + " ms later"
                                + "\nhint:     sleep/delay for the backoff before retrying: 50 × 2^(k−1) ms.";
                            if (gap > delay + 1500) return "expected: attempt " + k + " of " + e.id + " about " + delay + " ms after "
                                + at(s.lastEnd) + "\ngot:      " + at(e) + ", " + gap + " ms later (is the backoff in ms?)";
                            s.retry = null;
                        }
                        s.attempts = k;
                        s.open = e;
                        running++;
                        maxConc = Math.max(maxConc, running);
                        if (running > sc.workers) return "expected: at most --workers " + sc.workers + " attempts running at once\n"
                            + "got:      " + running + " running at " + at(e)
                            + "\nhint:     every job must hold a slot (Semaphore) while it runs; did one start without acquiring?";
                    }
                    case "done", "fail", "timeout", "cancel" -> {
                        int k = e.num("attempt");
                        if (s.open == null || s.open.num("attempt") != k)
                            return "expected: '" + e.kind + " " + e.id + " attempt=" + k + "' only while that attempt runs\ngot:      " + at(e)
                                + (s.open == null ? " with no attempt of " + e.id + " running" : " while " + at(s.open) + " runs");
                        long took = e.t - s.open.t;
                        long dur = s.spec.duration;
                        switch (e.kind) {
                            case "done", "fail" -> {
                                boolean shouldFail = k <= s.spec.fail;
                                if (e.kind.equals("done") && shouldFail) return "expected: attempt " + k + " of " + e.id
                                    + " to fail (fail=" + s.spec.fail + ": the first " + s.spec.fail + " attempts fail)\ngot:      " + at(e);
                                if (e.kind.equals("fail") && !shouldFail) return "expected: attempt " + k + " of " + e.id
                                    + " to succeed (fail=" + s.spec.fail + ")\ngot:      " + at(e);
                                if (took < dur - 2) return "expected: " + e.id + " to take at least duration=" + dur + " ms\ngot:      "
                                    + at(e) + ", " + took + " ms after " + at(s.open) + "\nhint:     the simulated work is a sleep of duration ms.";
                                if (sc.timeout > 0 && took > sc.timeout + 500) return "expected: a timeout after --timeout-ms " + sc.timeout
                                    + "\ngot:      " + at(e) + ", " + took + " ms after it started";
                            }
                            case "timeout" -> {
                                if (sc.timeout == 0) return "expected: no timeouts without --timeout-ms\ngot:      " + at(e);
                                if (took < sc.timeout - 2) return "expected: timeout after " + sc.timeout + " ms\ngot:      " + at(e)
                                    + ", only " + took + " ms after it started";
                                if (took > sc.timeout + 500) return "expected: timeout about " + sc.timeout + " ms after the start\ngot:      "
                                    + at(e) + ", " + took + " ms after " + at(s.open)
                                    + "\nhint:     the timeout must fire while the work runs, not after it finishes.";
                                if (dur + 50 < sc.timeout) return "expected: " + e.id + " (duration=" + dur + ") to finish before the "
                                    + sc.timeout + " ms timeout\ngot:      " + at(e);
                            }
                            default -> {   // cancel
                                if (!sawShutdown) return "expected: cancel only after shutdown\ngot:      " + at(e);
                                if (e.t - shutdownT < sc.grace - 2) return "expected: running jobs to get --grace-ms " + sc.grace
                                    + " ms before they're cancelled\ngot:      " + at(e) + ", " + (e.t - shutdownT) + " ms after shutdown";
                            }
                        }
                        running--;
                        s.open = null;
                        s.lastEnd = e;
                        if (e.kind.equals("done") || e.kind.equals("cancel")) s.terminal = e;
                    }
                    case "retry" -> {
                        int k = e.num("attempt");
                        if (s.open != null || s.lastEnd == null || !Set.of("fail", "timeout").contains(s.lastEnd.kind)
                                || s.lastEnd.num("attempt") != k - 1 || s.retry != null)
                            return "expected: 'retry " + e.id + " attempt=" + (s.attempts + 1) + "' right after attempt " + s.attempts
                                + " failed or timed out\ngot:      " + at(e);
                        if (k - 1 > sc.retries) return "expected: at most --retries " + sc.retries + " retries\ngot:      " + at(e);
                        long want = 50L << (k - 2);
                        if (Long.parseLong(e.kv.get("delay")) != want)
                            return "expected: delay=" + want + " before attempt " + k + " (50 × 2^(" + (k - 1) + "−1))\ngot:      " + at(e);
                        s.retry = e;
                    }
                    case "giveup" -> {
                        int k = e.num("attempts");
                        if (s.open != null || s.lastEnd == null || !Set.of("fail", "timeout").contains(s.lastEnd.kind) || s.lastEnd.num("attempt") != k)
                            return "expected: 'giveup " + e.id + " attempts=" + s.attempts + "' right after its last failed attempt\ngot:      " + at(e);
                        if (k != sc.retries + 1) return "expected: giveup only after " + (sc.retries + 1) + " attempts (1 + --retries "
                            + sc.retries + ")\ngot:      " + at(e);
                        s.terminal = e;
                    }
                    case "skip" -> {
                        String d = e.kv.get("dep");
                        if (!s.spec.deps.contains(d)) return "expected: dep= to name one of " + e.id + "'s dependencies " + s.spec.deps
                            + "\ngot:      " + at(e);
                        JobState ds = jobs.get(d);
                        if (ds.terminal == null || !Set.of("giveup", "skip").contains(ds.terminal.kind))
                            return "expected: skip only when a dependency gave up or was skipped\ngot:      " + at(e) + " but " + d + " was "
                                + (ds.terminal == null ? "not finished" : "'" + ds.terminal.raw + "'");
                        if (s.attempts > 0) return "expected: a skipped job never starts\ngot:      " + at(e);
                        waiting.remove(e.id);
                        s.terminal = e;
                    }
                    case "drop" -> {
                        if (!sawShutdown) return "expected: drop only after shutdown\ngot:      " + at(e)
                            + "\nhint:     without a SIGTERM every job runs: done, giveup or skip.";
                        if (s.open != null) return "expected: no drop while an attempt runs\ngot:      " + at(e) + " while " + at(s.open) + " runs";
                        waiting.remove(e.id);
                        s.terminal = e;
                    }
                    case "shutdown" -> {
                        if (!signalled) return "expected: no shutdown line (no signal was sent)\ngot:      " + at(e)
                            + "\nhint:     the hook also runs on a normal exit; make it do nothing once the run has finished.";
                        if (sawShutdown) return "expected: one shutdown line\ngot:      a second one at " + at(e);
                        sawShutdown = true;
                        shutdownT = e.t;
                    }
                    case "summary" -> {
                        for (JobState js : jobs.values()) {
                            if (js.terminal == null) return "expected: every job to end (done, giveup, skip, cancel or drop) before the summary\n"
                                + "got:      no ending for " + js.spec.id + (js.open != null ? " (still running: " + at(js.open) + ")" : "")
                                + (signalled ? "\nhint:     the hook returned before the drain finished, or a job was forgotten." : "");
                        }
                        if (e.num("total") != jobs.size()) return "expected: total=" + jobs.size() + "\ngot:      " + at(e);
                        for (String key : List.of("done", "giveup", "skip", "cancel", "drop")) {
                            long n = jobs.values().stream().filter(js -> js.terminal.kind.equals(key)).count();
                            if (e.num(key) != n) return "expected: " + key + "=" + n + " (that many '" + key + "' lines)\ngot:      " + at(e)
                                + "\nhint:     a counter shared by many threads lost updates? Use AtomicInteger/LongAdder, "
                                + "or derive the counts from one place.";
                        }
                    }
                    default -> { }
                }
            }
            return null;
        }
    }

    // ───────────────────────────── reporting ─────────────────────────────

    static String show(Scenario sc, Run r) {
        StringBuilder sb = new StringBuilder();
        sb.append("command:  ").append(sc.command());
        List<String> lines = sc.jobsText.lines().toList();
        sb.append("\njobs.txt: ");
        if (lines.size() <= 8) sb.append(String.join("\n          ", lines));
        else sb.append(String.join("\n          ", lines.subList(0, 4))).append("\n          … (").append(lines.size()).append(" lines)");
        sb.append("\nexit:     ").append(r.timedOut ? "(killed)" : r.exit);
        if (r.signalNanos != 0) sb.append(" (SIGTERM was sent after the first starts)");
        List<String> out = r.out;
        sb.append("\nstdout:   ");
        if (out.isEmpty()) sb.append("(nothing)");
        else if (out.size() <= 14) sb.append(String.join("\n          ", out));
        else sb.append(String.join("\n          ", out.subList(0, 6))).append("\n          … (").append(out.size() - 12)
            .append(" more lines)\n          ").append(String.join("\n          ", out.subList(out.size() - 6, out.size())));
        if (!r.err.isEmpty()) {
            sb.append("\nstderr:   ").append(String.join("\n          ", r.err.subList(0, Math.min(4, r.err.size()))));
            if (r.err.size() > 4) sb.append("\n          … (").append(r.err.size() - 4).append(" more lines)");
            if (String.join("\n", r.err).contains("NotImplementedError") || String.join("\n", r.err).contains("UnsupportedOperationException"))
                sb.append("\nhint:     a TODO is still in the way: start with milestone ").append(sc.stage).append(" on the project page.");
        }
        return sb.toString();
    }

    static void pass(int stage, String name) {
        passed++;
        System.out.println("PASS stage " + stage + " · " + name);
    }

    static void fail(int stage, String name, String problem, String context) {
        failed++;
        stageFailed = true;
        System.out.println("FAIL stage " + stage + " · " + name);
        for (String l : problem.split("\n")) System.out.println("    " + l);
        if (!context.isEmpty()) for (String l : context.split("\n")) System.out.println("    " + l);
    }
}
