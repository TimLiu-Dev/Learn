import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * M3 Performance Clinic: black-box checker.
 *
 *   java check/Check.java <launcher> --baseline <baseline-launcher> [--stage N]
 *
 *   launcher   your build/install/logreport/bin/logreport (after ./gradlew installDist)
 *   baseline   the untouched patient you copied before changing anything
 *   --stage N  run stages 1..N (default 4)
 *
 * Stage 1  same report as the patient on the small log
 * Stage 2  same report on the edge-case logs, same errors for bad arguments
 * Stage 3  at least 5x faster than the patient on the 1,000,000-line log (target 10x)
 * Stage 4  finishes the 1,000,000-line log in a 64 MB heap (-Xmx64m)
 *
 * Generated files (the logs and the patient's timings) are cached in
 * <java.io.tmpdir>/m3-clinic; delete that folder to start fresh.
 * Uses only the JDK. Generated from .build/m3/Check.template.java; do not edit by hand.
 */
public class Check {
    static final int SMALL_LINES = 1500;
    static final long SMALL_SEED = 7;
    static final int LARGE_LINES = 1_000_000;
    static final long LARGE_SEED = 2027;
    static final double PASS_FACTOR = 5.0;
    static final double TARGET_FACTOR = 10.0;
    static final String HEAP = "-Xmx64m";

    static Path cache;
    static int passed;
    static int failed;

    public static void main(String[] args) {
        String launcherArg = null;
        String baselineArg = null;
        int maxStage = 4;
        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--baseline" -> baselineArg = args[++i];
                    case "--stage" -> maxStage = Integer.parseInt(args[++i]);
                    default -> {
                        if (args[i].startsWith("--") || launcherArg != null) throw new IllegalArgumentException(args[i]);
                        launcherArg = args[i];
                    }
                }
            }
            if (launcherArg == null || maxStage < 1 || maxStage > 4) throw new IllegalArgumentException("launcher");
        } catch (RuntimeException e) {
            System.out.println("usage: java check/Check.java <launcher> --baseline <baseline-launcher> [--stage 1..4]");
            System.out.println("  e.g. java ../m3/check/Check.java build/install/logreport/bin/logreport"
                    + " --baseline ../m3-baseline/bin/logreport --stage 2");
            System.exit(2);
        }

        Path launcher = Path.of(launcherArg).toAbsolutePath().normalize();
        Path baseline = baselineArg == null ? null : Path.of(baselineArg).toAbsolutePath().normalize();
        int stage = 1;
        try {
            cache = Path.of(System.getProperty("java.io.tmpdir"), "m3-clinic");
            Files.createDirectories(cache);
            System.out.println("M3 Performance Clinic checker · stages 1.." + maxStage);
            System.out.println("  program : " + launcher);
            System.out.println("  baseline: " + (baseline == null ? "(none)" : baseline));
            System.out.println();
            if (launcherProblem(launcher, "your program") == null) {
                for (stage = 1; stage <= maxStage; stage++) {
                    switch (stage) {
                        case 1 -> stage1(launcher, baseline);
                        case 2 -> stage2(launcher);
                        case 3 -> stage3(launcher, baseline);
                        default -> stage4(launcher, baseline);
                    }
                }
            } else {
                fail(1, "your program can be started", launcherProblem(launcher, "your program"));
            }
        } catch (Exception e) {
            fail(stage, "the checker could run your program", "Unexpected problem: " + e
                    + "\nIf this keeps happening, delete " + cache + " and try again.");
        }
        System.out.println();
        System.out.println("-- summary: " + passed + " passed, " + failed + " failed (stages 1.." + maxStage + ")");
        System.out.println(failed == 0 ? "All checks passed." : "Fix the first FAIL above, then run the checker again.");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ------------------------------------------------------------------ stages

    static void stage1(Path launcher, Path baseline) throws Exception {
        Path small = ensureLog("small", SMALL_SEED, SMALL_LINES, null);
        if (baseline == null) {
            fail(1, "a frozen copy of the untouched patient (--baseline)", """
                    Before you change any code, freeze the patient so you can always compare against it:
                      ./gradlew installDist
                      cp -R build/install/logreport ../m3-baseline
                    then run the checker again with --baseline ../m3-baseline/bin/logreport
                    (stage 3 times your program against it).""");
        } else if (launcherProblem(baseline, "the baseline") != null) {
            fail(1, "a frozen copy of the untouched patient (--baseline)", launcherProblem(baseline, "the baseline"));
        } else {
            Run b = run(baseline, List.of(small.toString()), null, 300);
            if (b.exit == 0 && b.out.equals(SMALL_EXPECTED)) {
                pass(1, "--baseline is the untouched patient");
            } else {
                fail(1, "--baseline is the untouched patient", """
                        The baseline's report on the small log is not the original patient's.
                        Copy it again from a fresh, unmodified starter:
                          cp -R <fresh starter> /tmp/patient && cd /tmp/patient && ./gradlew installDist
                          cp -R build/install/logreport ~/katas/m3-baseline""");
            }
        }
        Run r = run(launcher, List.of(small.toString()), null, 300);
        compareReport(1, "same report as the patient on the small log (1,500 lines)", small, SMALL_EXPECTED, r,
                "data/small.log in the starter is the same file.");
    }

    static void stage2(Path launcher) throws Exception {
        Path dir = cache.resolve("edge");
        Files.createDirectories(dir);
        for (String[] edge : EDGES) {
            Path log = dir.resolve(edge[0]);
            Files.writeString(log, edge[1], StandardCharsets.UTF_8);
            Run r = run(launcher, List.of(log.toString()), null, 120);
            compareReport(2, "same report on edge case " + edge[0], log, edge[2], r, edge[3]);
        }

        Run noArgs = run(launcher, List.of(), null, 60);
        expectError(noArgs, "no arguments", "usage: logreport <access.log>\n");
        Path missing = dir.resolve("does-not-exist.log");
        Run m = run(launcher, List.of(missing.toString()), null, 60);
        expectError(m, "a file that does not exist", "error: cannot read " + missing + "\n");
        Run d = run(launcher, List.of(dir.toString()), null, 60);
        expectError(d, "a directory instead of a file", "error: cannot read " + dir + "\n");
    }

    static void stage3(Path launcher, Path baseline) throws Exception {
        String what = "at least " + fmt1(PASS_FACTOR) + "x faster than the patient on 1,000,000 lines";
        if (baseline == null || launcherProblem(baseline, "the baseline") != null) {
            fail(3, what, "Stage 3 needs --baseline (see stage 1): the speed-up is measured against the untouched patient.");
            return;
        }
        Path large = ensureLog("large", LARGE_SEED, LARGE_LINES, LARGE_LOG_SHA);
        Path expectedFile = cache.resolve("expected-large.out");
        double base = baselineSeconds(baseline, large, expectedFile);
        if (base < 0) return;

        long timeout = Math.max(120, (long) (3 * base));
        System.out.println("  ...timing your program: 1 warm-up run, then 3 timed runs");
        Run warm = run(launcher, List.of(large.toString()), null, timeout);
        if (warm.timedOut) {
            fail(3, what, "Your program did not finish within " + timeout + " s; the patient takes "
                    + fmt1(base) + " s. " + PROFILE_HINT);
            return;
        }
        if (!sameAsExpected(3, what, large, warm, expectedFile)) return;
        double[] secs = new double[3];
        for (int i = 0; i < 3; i++) {
            Run r = run(launcher, List.of(large.toString()), null, timeout);
            if (r.timedOut || r.exit != 0 || !LARGE_REPORT_SHA.equals(sha256(r.out))) {
                fail(3, what, "Timed run " + (i + 1) + " did not give the same report as the warm-up run"
                        + " (exit " + r.exit + (r.timedOut ? ", timed out" : "") + "). Is the output deterministic?"
                        + "\nHint: iterating a HashMap is fine, but only if you sort before printing.");
                return;
            }
            secs[i] = r.seconds;
        }
        Arrays.sort(secs);
        double mine = secs[1];
        double factor = base / mine;
        String result = String.format(Locale.ROOT, "%.1fx faster than the patient (patient %.2f s, yours %.2f s;"
                + " median of 3 after a warm-up; target %sx)", factor, base, mine, fmt1(TARGET_FACTOR));
        if (factor >= PASS_FACTOR) {
            pass(3, result);
            if (factor < TARGET_FACTOR) {
                System.out.println("       note: that passes, but the clinic's target is " + fmt1(TARGET_FACTOR)
                        + "x. Profile again: what is at the top now?");
            }
        } else {
            fail(3, what, "Only " + result + ".\n" + PROFILE_HINT);
        }
    }

    static void stage4(Path launcher, Path baseline) throws Exception {
        String what = "finishes the 1,000,000-line log in a 64 MB heap (JAVA_OPTS=" + HEAP + ")";
        Path large = ensureLog("large", LARGE_SEED, LARGE_LINES, LARGE_LOG_SHA);
        Run r = run(launcher, List.of(large.toString()), HEAP, 300);
        if (r.timedOut) {
            fail(4, what, "Your program did not finish within 300 s with " + HEAP + ". A heap that is almost full"
                    + " makes the GC run constantly; something still keeps a lot of data alive.\n" + MEMORY_HINT);
        } else if (r.exit != 0 && r.err.contains("OutOfMemoryError")) {
            fail(4, what, "Your program ran out of memory:\n" + indent(firstLines(r.err, 4)) + "\n" + MEMORY_HINT);
        } else if (sameAsExpected(4, what, large, r, cache.resolve("expected-large.out"))) {
            pass(4, what + String.format(Locale.ROOT, " · %.2f s", r.seconds));
        }
        if (baseline != null && launcherProblem(baseline, "the baseline") == null) {
            Run b = run(baseline, List.of(large.toString()), HEAP, 120);
            String how = b.err.contains("OutOfMemoryError") ? "OutOfMemoryError" : "exit " + b.exit;
            System.out.println(String.format(Locale.ROOT, "       for comparison, the patient with %s: %s after %.1f s",
                    HEAP, b.timedOut ? "still running" : how, b.seconds));
        }
    }

    static final String PROFILE_HINT = """
            Profile, don't guess. Record a flight recording of one run:
              LOGREPORT_OPTS="-XX:StartFlightRecording=filename=rec.jfr,settings=profile" \\
                build/install/logreport/bin/logreport <the large log>
              jfr view hot-methods rec.jfr     (or open rec.jfr in IntelliJ: Run > Open Profiler Snapshot)
            Fix the frame at the top, re-measure, and repeat. The large log is in""" + " "
            + "<java.io.tmpdir>/m3-clinic, or make it with: java tools/LogGen.java --out large.log";

    static final String MEMORY_HINT = """
            Hint: something keeps every line or every request alive: a list of all lines, a list of all
            records, or a cache that never forgets. Find it with a heap dump:
              LOGREPORT_OPTS="-XX:+HeapDumpOnOutOfMemoryError" JAVA_OPTS=-Xmx64m build/install/logreport/bin/logreport <large log>
            then open the .hprof file in IntelliJ and sort by retained size.""";

    // ---------------------------------------------------------------- baseline

    /** The patient's median time on the large log, measured once and cached. Negative after a FAIL. */
    static double baselineSeconds(Path baseline, Path large, Path expectedFile) throws Exception {
        Path lib = baseline.getParent().resolveSibling("lib");
        StringBuilder key = new StringBuilder(baseline.toRealPath().toString());
        if (Files.isDirectory(lib)) {
            try (Stream<Path> jars = Files.list(lib)) {
                for (Path jar : jars.sorted().toList()) {
                    key.append('|').append(jar.getFileName()).append(Files.size(jar))
                       .append(Files.getLastModifiedTime(jar).toMillis());
                }
            }
        }
        Path timing = cache.resolve("baseline-" + sha256(key.toString()).substring(0, 16) + ".txt");
        if (Files.exists(timing) && Files.exists(expectedFile)) {
            double s = Double.parseDouble(Files.readString(timing).trim().split("\\s+")[0]);
            System.out.println(String.format(Locale.ROOT, "  ...the patient's time (cached): %.2f s"
                    + " (delete %s to measure it again)", s, timing));
            return s;
        }
        System.out.println("  ...timing the patient: 3 runs on 1,000,000 lines. This is slow on purpose and"
                + " happens once; the result is cached.");
        double[] secs = new double[3];
        for (int i = 0; i < 3; i++) {
            Run r = run(baseline, List.of(large.toString()), null, 1800);
            if (r.timedOut || r.exit != 0) {
                fail(3, "timing the patient", "The baseline did not finish (exit " + r.exit
                        + (r.timedOut ? ", timed out after 30 min" : "") + "):\n" + indent(firstLines(r.err, 5)));
                return -1;
            }
            if (!LARGE_REPORT_SHA.equals(sha256(r.out))) {
                fail(3, "timing the patient", "The baseline's report on the large log is not the original patient's."
                        + " Is --baseline really the untouched program?");
                return -1;
            }
            if (i == 0) Files.writeString(expectedFile, r.out, StandardCharsets.UTF_8);
            secs[i] = r.seconds;
            System.out.println(String.format(Locale.ROOT, "     run %d: %.2f s", i + 1, r.seconds));
        }
        Arrays.sort(secs);
        Files.writeString(timing, String.format(Locale.ROOT, "%.3f %.3f %.3f %.3f%n", secs[1], secs[0], secs[1], secs[2]));
        return secs[1];
    }

    // ----------------------------------------------------------------- compare

    static void compareReport(int stage, String what, Path input, String expected, Run r, String hint) {
        if (r.timedOut) {
            fail(stage, what, "Your program did not finish on " + input.getFileName() + " (timed out).");
        } else if (r.exit != 0) {
            fail(stage, what, "Your program exited with code " + r.exit + " on " + input + "; the patient exits 0."
                    + (r.err.isBlank() ? "" : "\nIt printed to stderr:\n" + indent(firstLines(r.err, 8))));
        } else if (!r.out.equals(expected)) {
            fail(stage, what, "Input: " + input + "\n" + difference(expected, r.out)
                    + (hint.isEmpty() ? "" : "\nAbout this input: " + hint));
        } else {
            pass(stage, what);
        }
    }

    static boolean sameAsExpected(int stage, String what, Path input, Run r, Path expectedFile) throws IOException {
        if (r.exit != 0) {
            fail(stage, what, "Your program exited with code " + r.exit + " on " + input + ":\n"
                    + indent(firstLines(r.err, 8)));
            return false;
        }
        if (LARGE_REPORT_SHA.equals(sha256(r.out))) return true;
        String detail = Files.exists(expectedFile)
                ? difference(Files.readString(expectedFile, StandardCharsets.UTF_8), r.out)
                : "Your report differs from the patient's (run stage 3 with --baseline once to see where).";
        fail(stage, what, "Fast is no use if the answer changed. Input: " + input + "\n" + detail);
        return false;
    }

    static void expectError(Run r, String situation, String expectedErr) {
        String what = "same error as the patient for " + situation;
        if (r.exit == 2 && r.out.isEmpty() && r.err.equals(expectedErr)) {
            pass(2, what);
        } else {
            fail(2, what, "expected: exit 2, nothing on stdout, stderr " + show(expectedErr)
                    + "\ngot:      exit " + r.exit + ", stdout " + show(firstLines(r.out, 2))
                    + ", stderr " + show(firstLines(r.err, 3)));
        }
    }

    /** Where two reports first differ, with the section's rule as a hint. */
    static String difference(String expected, String actual) {
        if (actual.isEmpty()) return "Your program printed nothing.";
        String[] e = expected.split("\n", -1);
        String[] a = actual.split("\n", -1);
        int i = 0;
        while (i < e.length && i < a.length && e[i].equals(a[i])) i++;
        String section = "LOG REPORT";
        for (int k = 0; k <= Math.min(i, e.length - 1); k++) {
            if (SECTION_HINTS.containsKey(e[k])) section = e[k];
        }
        StringBuilder sb = new StringBuilder();
        sb.append("First difference at line ").append(i + 1).append(" (section ").append(section).append("):\n");
        for (int k = Math.max(0, i - 2); k < i; k++) sb.append("  same:     ").append(show(e[k])).append('\n');
        sb.append("  expected: ").append(i < e.length ? show(e[i]) : "(end of report)").append('\n');
        sb.append("  got:      ").append(i < a.length ? show(a[i]) : "(end of report)").append('\n');
        sb.append("Rule for this section: ").append(SECTION_HINTS.get(section));
        return sb.toString();
    }

    static final Map<String, String> SECTION_HINTS = Map.of(
            "LOG REPORT", "lines counts every line (blank and malformed ones too); a line is a request only if"
                    + " the patient's regex matches it and Instant.parse accepts its timestamp; bytes use"
                    + " Long.parseLong and anything it rejects counts as 0; first/last request are the earliest"
                    + " and latest timestamps, not the first and last lines.",
            "STATUS CODES", "one row per status code, lowest first; percent = count * 100.0 / parsed, printed"
                    + " with %6.1f in Locale.ROOT.",
            "ENDPOINTS", "most requests first, ties by endpoint name (String.compareTo). Percentiles are nearest-rank:"
                    + " rank = ceil(p * n / 100), value = sorted[rank - 1]. The endpoint drops the query string"
                    + " and replaces each all-digit path segment with {id}.",
            "USERS", "\"-\" is not a user. A new session starts when the gap to the same user's previous request"
                    + " (in file order, not sorted by time) is more than 30 minutes; exactly 30 minutes is the"
                    + " same session. Most sessions: top 5, ties by user name.",
            "SLOWEST REQUESTS", "top 10 by latency; equal latencies keep file order (lower line number first),"
                    + " because the patient's sort is stable. line counts every line of the file from 1.");

    // ------------------------------------------------------------------- runs

    record Run(int exit, String out, String err, double seconds, boolean timedOut) {
    }

    static Run run(Path launcher, List<String> args, String javaOpts, long timeoutSeconds) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(launcher.toString());
        cmd.addAll(args);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> env = pb.environment();
        // The same JVM settings for every run: your shell's options must not change the result.
        for (String v : List.of("JAVA_OPTS", "LOGREPORT_OPTS", "JAVA_TOOL_OPTIONS", "JDK_JAVA_OPTIONS")) env.remove(v);
        if (javaOpts != null) env.put("JAVA_OPTS", javaOpts);
        Path out = Files.createTempFile(cache, "out", ".txt");
        Path err = Files.createTempFile(cache, "err", ".txt");
        try {
            pb.redirectOutput(out.toFile()).redirectError(err.toFile());
            long t0 = System.nanoTime();
            Process p = pb.start();
            p.getOutputStream().close();
            boolean done = p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            double secs = (System.nanoTime() - t0) / 1e9;
            if (!done) {
                p.descendants().forEach(ProcessHandle::destroyForcibly);
                p.destroyForcibly();
                p.waitFor();
            }
            return new Run(done ? p.exitValue() : -1, Files.readString(out, StandardCharsets.UTF_8),
                    Files.readString(err, StandardCharsets.UTF_8), secs, !done);
        } finally {
            Files.deleteIfExists(out);
            Files.deleteIfExists(err);
        }
    }

    static String launcherProblem(Path launcher, String who) {
        if (!Files.exists(launcher)) {
            return "There is no file " + launcher + ".\nBuild " + who + " first: ./gradlew installDist creates"
                    + " build/install/logreport/bin/logreport.";
        }
        if (Files.isDirectory(launcher)) {
            return launcher + " is a directory. Pass the launcher script: .../bin/logreport";
        }
        if (!Files.isExecutable(launcher)) {
            return launcher + " is not executable. Run: chmod +x " + launcher;
        }
        return null;
    }

    // ------------------------------------------------------------------- logs

    static Path ensureLog(String name, long seed, int lines, String expectedSha) throws IOException {
        Path log = cache.resolve(name + "-" + seed + "-" + lines + ".log");
        if (Files.exists(log)) return log;
        if (lines > 100_000) {
            System.out.println("  ...generating a " + String.format(Locale.ROOT, "%,d", lines)
                    + "-line log (about 80 MB) into " + log + " (once)");
        }
        StringBuilder sb = new StringBuilder(lines * 85);
        Gen.generate(seed, lines, sb);
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        if (expectedSha != null && !expectedSha.equals(sha256(bytes))) {
            throw new IllegalStateException("the generated log does not have the expected contents");
        }
        Path tmp = cache.resolve(name + ".tmp");
        Files.write(tmp, bytes);
        Files.move(tmp, log, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return log;
    }

    // ------------------------------------------------------------------ output

    static void pass(int stage, String what) {
        passed++;
        System.out.println("PASS stage " + stage + " · " + what);
    }

    static void fail(int stage, String what, String details) {
        failed++;
        System.out.println("FAIL stage " + stage + " · " + what);
        System.out.println(indent(details));
    }

    static String indent(String s) {
        return "       " + s.strip().replace("\n", "\n       ");
    }

    static String firstLines(String s, int n) {
        String[] lines = s.split("\n", -1);
        if (lines.length <= n) return s.stripTrailing();
        return String.join("\n", Arrays.copyOf(lines, n)) + "\n...";
    }

    /** Quotes a line and makes invisible characters visible. */
    static String show(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\r", "\\r").replace("\t", "\\t").replace("\n", "\\n") + "\"";
    }

    static String fmt1(double d) {
        return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
    }

    static String sha256(String s) {
        return sha256(s.getBytes(StandardCharsets.UTF_8));
    }

    static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ------------------------------------------------------- generated parts
    //@@GEN@@

    //@@DATA@@
}
