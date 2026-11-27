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
    // ---- copied verbatim into Check.java ----
    static final class Gen {
        // method, path template, weight, base latency ms, normal status
        // {id} -> a number, {n} -> a small number, {hash} -> hex, {q} -> a word
        private static final Object[][] ENDPOINTS = {
            {"GET", "/api/products/{id}", 30, 20, 200},
            {"GET", "/api/products?page={n}&size=20", 10, 35, 200},
            {"GET", "/api/products/{id}/reviews", 6, 45, 200},
            {"GET", "/api/v2/products/{id}", 3, 18, 200},
            {"HEAD", "/api/products/{id}", 1, 5, 200},
            {"GET", "/api/orders/{id}", 8, 40, 200},
            {"POST", "/api/orders", 4, 120, 201},
            {"GET", "/api/users/{id}/orders", 5, 60, 200},
            {"PUT", "/api/users/{id}", 1, 70, 200},
            {"POST", "/api/cart/{id}/items", 5, 50, 201},
            {"DELETE", "/api/cart/{id}/items/{id}", 2, 30, 204},
            {"GET", "/api/search?q={q}", 8, 80, 200},
            {"POST", "/api/login", 2, 150, 200},
            {"GET", "/api/reports/{n}/monthly", 1, 400, 200},
            {"GET", "/static/app.{hash}.js", 6, 3, 200},
            {"GET", "/static/logo.png", 4, 2, 200},
            {"GET", "/health", 5, 1, 200},
        };
        private static final String[] WORDS = {
            "shoes", "laptop", "coffee", "desk", "lamp", "phone", "book", "chair", "tea", "bag",
        };
        private static final String[] BUNDLES = {"3f9a2c1e", "7b04d6aa", "c21e9f30"}; // three deploys
        private static final int TOTAL_WEIGHT;
        static {
            int t = 0;
            for (Object[] e : ENDPOINTS) t += (Integer) e[2];
            TOTAL_WEIGHT = t;
        }
        static final long START_MS = 1_772_409_600_000L; // 2026-03-02T00:00:00.000Z
        static final long SPAN_MS = 3L * 24 * 60 * 60 * 1000; // about three days of traffic

        static void generate(long seed, int lines, Appendable out) throws IOException {
            Random rnd = new Random(seed);
            int userPool = Math.min(3000, Math.max(20, lines / 50));
            int heavy = Math.min(10, userPool / 2);
            long avgGap = Math.max(1, SPAN_MS / Math.max(1, lines));
            long t = START_MS;
            StringBuilder sb = new StringBuilder(128);
            for (int i = 0; i < lines; i++) {
                t += rnd.nextInt((int) (2 * avgGap + 1));
                sb.setLength(0);
                appendTimestamp(sb, t);
                sb.append(" 10.").append(rnd.nextInt(256)).append('.').append(rnd.nextInt(256))
                  .append('.').append(rnd.nextInt(256)).append(' ');
                int u = rnd.nextInt(100);
                if (u < 8) sb.append('-');
                else if (u < 33) appendUser(sb, 1 + rnd.nextInt(heavy));       // a few heavy users (apps, bots)
                else appendUser(sb, heavy + 1 + rnd.nextInt(userPool - heavy));
                Object[] e = pickEndpoint(rnd);
                String method = (String) e[0];
                int base = (Integer) e[3];
                int status = (Integer) e[4];
                sb.append(' ').append(method).append(' ');
                appendPath(sb, (String) e[1], rnd);
                int s = rnd.nextInt(1000);
                if (s < 20) status = 404;
                else if (s < 25) status = 500;
                else if (s < 28) status = 503;
                else if (method.equals("GET") && ((String) e[1]).startsWith("/static/") && s < 400) status = 304;
                int latency = base + rnd.nextInt(base + 1);
                if (rnd.nextInt(100) < 6) latency += rnd.nextInt(base * 10 + 1);
                if (rnd.nextInt(2000) == 0) latency += rnd.nextInt(20_000);
                if (status == 503) latency += 5_000 + rnd.nextInt(5_000);
                sb.append(' ').append(status).append(' ').append(latency).append(' ');
                if (status == 304 || status == 204 || method.equals("HEAD")) sb.append('-');
                else sb.append(200 + rnd.nextInt(20_000));
                if (rnd.nextInt(1000) == 0) corrupt(sb, rnd);
                out.append(sb).append('\n');
            }
        }

        private static Object[] pickEndpoint(Random rnd) {
            int r = rnd.nextInt(TOTAL_WEIGHT);
            for (Object[] e : ENDPOINTS) {
                r -= (Integer) e[2];
                if (r < 0) return e;
            }
            throw new AssertionError();
        }

        private static void appendUser(StringBuilder sb, int id) {
            sb.append('u');
            if (id < 1000) sb.append('0');
            if (id < 100) sb.append('0');
            if (id < 10) sb.append('0');
            sb.append(id);
        }

        private static void appendPath(StringBuilder sb, String template, Random rnd) {
            int i = 0;
            while (i < template.length()) {
                char c = template.charAt(i);
                if (c == '{') {
                    int end = template.indexOf('}', i);
                    switch (template.substring(i + 1, end)) {
                        case "id" -> sb.append(1 + rnd.nextInt(99_999));
                        case "n" -> sb.append(2020 + rnd.nextInt(7));
                        case "q" -> sb.append(WORDS[rnd.nextInt(WORDS.length)]);
                        case "hash" -> sb.append(BUNDLES[rnd.nextInt(BUNDLES.length)]);
                        default -> throw new AssertionError(template);
                    }
                    i = end + 1;
                } else {
                    sb.append(c);
                    i++;
                }
            }
        }

        /** About one line in a thousand is broken, as in real logs. */
        private static void corrupt(StringBuilder sb, Random rnd) {
            switch (rnd.nextInt(3)) {
                case 0 -> sb.setLength(10 + rnd.nextInt(sb.length() / 2)); // truncated write
                case 1 -> {                                                  // latency missing
                    int end = sb.lastIndexOf(" ");
                    int start = sb.lastIndexOf(" ", end - 1);
                    sb.replace(start + 1, end, "-");
                }
                default -> {                                                 // a stack trace line
                    sb.setLength(0);
                    sb.append("\tat com.shop.api.OrderController.get(OrderController.java:")
                      .append(10 + rnd.nextInt(300)).append(')');
                }
            }
        }

        /** yyyy-MM-ddTHH:mm:ss.SSSZ in UTC, by hand so the bytes never depend on a formatter. */
        static void appendTimestamp(StringBuilder sb, long epochMs) {
            long days = Math.floorDiv(epochMs, 86_400_000L);
            int msOfDay = (int) Math.floorMod(epochMs, 86_400_000L);
            // civil-from-days, Howard Hinnant's algorithm
            long z = days + 719_468;
            long era = Math.floorDiv(z, 146_097);
            long doe = z - era * 146_097;
            long yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365;
            long y = yoe + era * 400;
            long doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
            long mp = (5 * doy + 2) / 153;
            long d = doy - (153 * mp + 2) / 5 + 1;
            long m = mp < 10 ? mp + 3 : mp - 9;
            if (m <= 2) y++;
            sb.append(y).append('-');
            pad(sb, (int) m, 2).append('-');
            pad(sb, (int) d, 2).append('T');
            pad(sb, msOfDay / 3_600_000, 2).append(':');
            pad(sb, msOfDay / 60_000 % 60, 2).append(':');
            pad(sb, msOfDay / 1000 % 60, 2).append('.');
            pad(sb, msOfDay % 1000, 3).append('Z');
        }

        private static StringBuilder pad(StringBuilder sb, int v, int width) {
            String s = Integer.toString(v);
            for (int k = s.length(); k < width; k++) sb.append('0');
            return sb.append(s);
        }
    }
    // ---- end copy ----

    static final String LARGE_LOG_SHA = "33337c7fb755f11a9c3107b9d8e5b01db791b466d4cc6a70e490877940715ba9";
    static final String LARGE_REPORT_SHA = "d08186eb9d8059df152dce7351aae42747bb217e4789855593d278b2d9a336c2";

    /** The patient's report on the small log (seed 7, 1,500 lines). */
    static final String SMALL_EXPECTED = "LOG REPORT\n"
            + "lines          : 1500\n"
            + "parsed         : 1499\n"
            + "malformed      : 1\n"
            + "first request  : 2026-03-02T00:02:15.696Z\n"
            + "last request   : 2026-03-04T23:32:03.306Z\n"
            + "bytes served   : 14504256\n"
            + "\n"
            + "STATUS CODES\n"
            + "200      1249   83.3%\n"
            + "201       118    7.9%\n"
            + "204        29    1.9%\n"
            + "304        50    3.3%\n"
            + "404        41    2.7%\n"
            + "500         7    0.5%\n"
            + "503         5    0.3%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "      469     31    103    204   9596  GET /api/products/{id}\n"
            + "      130     60    244    358    441  GET /api/orders/{id}\n"
            + "      129    125    505    823   8015  GET /api/search\n"
            + "      128     54    193    359   7933  GET /api/products\n"
            + "       84     72     90    289    289  GET /api/products/{id}/reviews\n"
            + "       70      2     11   8295   8295  GET /health\n"
            + "       69     78     99    305    305  POST /api/cart/{id}/items\n"
            + "       68     89    120    508    508  GET /api/users/{id}/orders\n"
            + "       59      3      8     16     16  GET /static/logo.png\n"
            + "       53    185    271   1080   1080  POST /api/orders\n"
            + "       46     28     36     36     36  GET /api/v2/products/{id}\n"
            + "       31      4     21     23     23  GET /static/app.c21e9f30.js\n"
            + "       31    243    297    575    575  POST /api/login\n"
            + "       30     46    145    246    246  DELETE /api/cart/{id}/items/{id}\n"
            + "       29      4      6     27     27  GET /static/app.3f9a2c1e.js\n"
            + "       29      5      6     26     26  GET /static/app.7b04d6aa.js\n"
            + "       17    637   4009   4009   4009  GET /api/reports/{id}/monthly\n"
            + "       15      8     34     34     34  HEAD /api/products/{id}\n"
            + "       12    105    133    133    133  PUT /api/users/{id}\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 30\n"
            + "sessions       : 1022\n"
            + "per user (avg) : 34.07\n"
            + "most sessions  :\n"
            + "  u0014      42\n"
            + "  u0004      40\n"
            + "  u0007      40\n"
            + "  u0021      40\n"
            + "  u0025      39\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "    9596     1114    503  GET /api/products/34521\n"
            + "    8295      444    503  GET /health\n"
            + "    8015     1156    503  GET /api/search?q=phone\n"
            + "    7933     1483    503  GET /api/products?page=2020&size=20\n"
            + "    6290      230    503  GET /api/products/65955\n"
            + "    4009      852    200  GET /api/reports/2021/monthly\n"
            + "    1080      222    201  POST /api/orders\n"
            + "     823      157    200  GET /api/search?q=laptop\n"
            + "     793      703    200  GET /api/reports/2026/monthly\n"
            + "     760       23    200  GET /api/reports/2020/monthly\n";

    /** {file name, input, the patient's report, what the case is about}; same files as data/edge/. */
    static final String[][] EDGES = {
        {"empty.log",
            "",
            "LOG REPORT\n"
            + "lines          : 0\n"
            + "parsed         : 0\n"
            + "malformed      : 0\n"
            + "first request  : -\n"
            + "last request   : -\n"
            + "bytes served   : 0\n"
            + "\n"
            + "STATUS CODES\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 0\n"
            + "sessions       : 0\n"
            + "per user (avg) : 0.00\n"
            + "most sessions  :\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n",
            "An empty file still prints every heading, with zeros and \"-\"."},
        {"single_line.log",
            "2026-03-02T10:00:00.250Z 10.0.0.1 u0001 GET /api/orders/42 200 87 5120\n",
            "LOG REPORT\n"
            + "lines          : 1\n"
            + "parsed         : 1\n"
            + "malformed      : 0\n"
            + "first request  : 2026-03-02T10:00:00.250Z\n"
            + "last request   : 2026-03-02T10:00:00.250Z\n"
            + "bytes served   : 5120\n"
            + "\n"
            + "STATUS CODES\n"
            + "200         1  100.0%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "        1     87     87     87     87  GET /api/orders/{id}\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 1\n"
            + "sessions       : 1\n"
            + "per user (avg) : 1.00\n"
            + "most sessions  :\n"
            + "  u0001      1\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "      87        1    200  GET /api/orders/42\n",
            "With one value, every percentile is that value."},
        {"malformed.log",
            "2026-03-02T10:00:00.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:01.000Z 10.0.0.1 u0001 GET\n"
            + "2026-03-02T10:00:02.000Z 10.0.0.1 u0001 GET /api/orders/1 200 - 100\n"
            + "2026-03-02T10:00:03.000Z 10.0.0.1 u0001 GET /api/orders/1 20 40 100\n"
            + "2026-03-02T10:00:04.000Z 10.0.0.1 u0001 GET /api/orders/1 2000 40 100\n"
            + "2026-03-02T10:00:05.000Z 10.0.0.1 u0001 GET /api/orders/1 200 12345678 100\n"
            + "2026-03-02T10:00:06.000Z 10.0.0.1 u0001 get /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:07.000Z 10.0.0.1 u0001 GET api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:08.000Z 10.0.0.1  u0001 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:09.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100 \n"
            + " 2026-03-02T10:00:10.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:11.000Z 10.0.0.1 u00\t01 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:12.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100 extra\n"
            + "2026-03-02 10:00:13.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T10:00:14Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "2026-02-30T10:00:15.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "2026-03-02T25:00:16.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\n"
            + "\tat com.shop.api.OrderController.get(OrderController.java:42)\n"
            + "\n"
            + "2026-03-02T10:00:17.000Z 10.0.0.1 u0002 GET /api/orders/2 200 41 abc\n"
            + "2026-03-02T10:00:18.000Z 10.0.0.1 u0002 GET /api/orders/3 200 42 +42\n"
            + "2026-03-02T10:00:19.000Z 10.0.0.1 u0002 GET /api/orders/4 200 43 -7\n"
            + "2026-03-02T10:00:20.000Z 10.0.0.1 u0002 GET /api/orders/5 200 44 99999999999999999999\n"
            + "2026-03-02T10:00:21.000Z 10.0.0.1 - PATCH / 200 1 -\n"
            + "2026-03-02T10:00:22.000Z 10.0.0.1 - GET /123 200 2 10\n"
            + "2026-03-02T10:00:23.000Z 10.0.0.1 - GET /a/123/ 200 3 10\n"
            + "2026-03-02T10:00:24.000Z 10.0.0.1 - GET /a/12b 200 4 10\n"
            + "2026-03-02T10:00:25.000Z 10.0.0.1 - GET /a//7 200 5 10\n"
            + "2026-03-02T10:00:26.000Z 10.0.0.1 - GET /s?id=/123 200 6 10\n"
            + "2026-03-02T10:00:27.000Z 10.0.0.1 - GET /v2/9/x/0007?q 200 7 10\n",
            "LOG REPORT\n"
            + "lines          : 30\n"
            + "parsed         : 12\n"
            + "malformed      : 18\n"
            + "first request  : 2026-03-02T10:00:00.000Z\n"
            + "last request   : 2026-03-02T10:00:27.000Z\n"
            + "bytes served   : 195\n"
            + "\n"
            + "STATUS CODES\n"
            + "200        12  100.0%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "        5     42     44     44     44  GET /api/orders/{id}\n"
            + "        1      5      5      5      5  GET /a//{id}\n"
            + "        1      4      4      4      4  GET /a/12b\n"
            + "        1      3      3      3      3  GET /a/{id}/\n"
            + "        1      6      6      6      6  GET /s\n"
            + "        1      7      7      7      7  GET /v2/{id}/x/{id}\n"
            + "        1      2      2      2      2  GET /{id}\n"
            + "        1      1      1      1      1  PATCH /\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 2\n"
            + "sessions       : 2\n"
            + "per user (avg) : 1.00\n"
            + "most sessions  :\n"
            + "  u0001      1\n"
            + "  u0002      1\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "      44       23    200  GET /api/orders/5\n"
            + "      43       22    200  GET /api/orders/4\n"
            + "      42       21    200  GET /api/orders/3\n"
            + "      41       20    200  GET /api/orders/2\n"
            + "      40        1    200  GET /api/orders/1\n"
            + "       7       30    200  GET /v2/9/x/0007?q\n"
            + "       6       29    200  GET /s?id=/123\n"
            + "       5       28    200  GET /a//7\n"
            + "       4       27    200  GET /a/12b\n"
            + "       3       26    200  GET /a/123/\n",
            "A line is a request only if the patient's regex matches the whole line and Instant.parse accepts the timestamp: double, leading or trailing spaces, a tab, a ninth field, 2026-02-30 and hour 25 are all malformed. The bytes field goes through Long.parseLong: \"abc\" and an overflowing number count as 0, \"+42\" is 42 and \"-7\" is -7."},
        {"ties.log",
            "2026-03-02T10:00:00.000Z 10.0.0.0 u0003 GET /zeta 200 500 10\n"
            + "2026-03-02T10:01:00.000Z 10.0.0.1 u0001 GET /alpha 200 500 10\n"
            + "2026-03-02T10:02:00.000Z 10.0.0.2 u0002 GET /mid 200 500 10\n"
            + "2026-03-02T10:03:00.000Z 10.0.0.3 u0003 GET /beta 200 500 10\n"
            + "2026-03-02T10:04:00.000Z 10.0.0.4 u0001 GET /omega 200 500 10\n"
            + "2026-03-02T10:05:00.000Z 10.0.0.5 u0002 GET /gamma 200 500 10\n"
            + "2026-03-02T10:06:00.000Z 10.0.0.6 u0003 GET /delta 200 500 10\n"
            + "2026-03-02T10:07:00.000Z 10.0.0.7 u0001 GET /kappa 200 500 10\n"
            + "2026-03-02T10:08:00.000Z 10.0.0.8 u0002 GET /eta 200 500 10\n"
            + "2026-03-02T10:09:00.000Z 10.0.0.9 u0003 GET /theta 200 500 10\n"
            + "2026-03-02T10:10:00.000Z 10.0.0.10 u0001 GET /iota 200 500 10\n"
            + "2026-03-02T10:11:00.000Z 10.0.0.11 u0002 GET /lambda 200 500 10\n"
            + "2026-03-02T11:00:00.000Z 10.0.0.9 u0004 GET /zeta 200 500 10\n"
            + "2026-03-02T11:00:00.000Z 10.0.0.9 u0004 GET /alpha 200 500 10\n",
            "LOG REPORT\n"
            + "lines          : 14\n"
            + "parsed         : 14\n"
            + "malformed      : 0\n"
            + "first request  : 2026-03-02T10:00:00.000Z\n"
            + "last request   : 2026-03-02T11:00:00.000Z\n"
            + "bytes served   : 140\n"
            + "\n"
            + "STATUS CODES\n"
            + "200        14  100.0%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "        2    500    500    500    500  GET /alpha\n"
            + "        2    500    500    500    500  GET /zeta\n"
            + "        1    500    500    500    500  GET /beta\n"
            + "        1    500    500    500    500  GET /delta\n"
            + "        1    500    500    500    500  GET /eta\n"
            + "        1    500    500    500    500  GET /gamma\n"
            + "        1    500    500    500    500  GET /iota\n"
            + "        1    500    500    500    500  GET /kappa\n"
            + "        1    500    500    500    500  GET /lambda\n"
            + "        1    500    500    500    500  GET /mid\n"
            + "        1    500    500    500    500  GET /omega\n"
            + "        1    500    500    500    500  GET /theta\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 4\n"
            + "sessions       : 4\n"
            + "per user (avg) : 1.00\n"
            + "most sessions  :\n"
            + "  u0001      1\n"
            + "  u0002      1\n"
            + "  u0003      1\n"
            + "  u0004      1\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "     500        1    200  GET /zeta\n"
            + "     500        2    200  GET /alpha\n"
            + "     500        3    200  GET /mid\n"
            + "     500        4    200  GET /beta\n"
            + "     500        5    200  GET /omega\n"
            + "     500        6    200  GET /gamma\n"
            + "     500        7    200  GET /delta\n"
            + "     500        8    200  GET /kappa\n"
            + "     500        9    200  GET /eta\n"
            + "     500       10    200  GET /theta\n",
            "Equal request counts sort by endpoint name; equal latencies keep file order; equal session counts sort by user name."},
        {"crlf_no_final_newline.log",
            "2026-03-02T10:00:00.000Z 10.0.0.1 u0001 GET /api/orders/1 200 40 100\r\n"
            + "2026-03-02T10:00:01.000Z 10.0.0.1 u0001 GET /api/orders/2 404 41 100\r\n"
            + "2026-03-02T10:00:02.000Z 10.0.0.1 u0002 POST /api/orders 201 120 300",
            "LOG REPORT\n"
            + "lines          : 3\n"
            + "parsed         : 3\n"
            + "malformed      : 0\n"
            + "first request  : 2026-03-02T10:00:00.000Z\n"
            + "last request   : 2026-03-02T10:00:02.000Z\n"
            + "bytes served   : 500\n"
            + "\n"
            + "STATUS CODES\n"
            + "200         1   33.3%\n"
            + "201         1   33.3%\n"
            + "404         1   33.3%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "        2     40     41     41     41  GET /api/orders/{id}\n"
            + "        1    120    120    120    120  POST /api/orders\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 2\n"
            + "sessions       : 2\n"
            + "per user (avg) : 1.00\n"
            + "most sessions  :\n"
            + "  u0001      1\n"
            + "  u0002      1\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "     120        3    201  POST /api/orders\n"
            + "      41        2    404  GET /api/orders/2\n"
            + "      40        1    200  GET /api/orders/1\n",
            "Lines end with \\r\\n here, and the last line has no newline at all."},
        {"out_of_order.log",
            "2026-03-02T10:00:00.000Z 10.0.0.1 u0001 GET /a 200 10 1\n"
            + "2026-03-02T09:00:00.000Z 10.0.0.1 u0001 GET /a 200 11 1\n"
            + "2026-03-02T09:20:00.000Z 10.0.0.1 u0001 GET /a 200 12 1\n"
            + "2026-03-02T08:00:00.000Z 10.0.0.2 u0002 GET /b 200 13 1\n"
            + "2026-03-02T08:30:00.000Z 10.0.0.2 u0002 GET /b 200 14 1\n"
            + "2026-03-02T09:00:00.001Z 10.0.0.2 u0002 GET /b 200 15 1\n"
            + "2026-03-03T00:00:00.000Z 10.0.0.3 u0003 GET /c 200 16 1\n"
            + "2026-03-01T23:59:59.999Z 10.0.0.3 u0003 GET /c 200 17 1\n",
            "LOG REPORT\n"
            + "lines          : 8\n"
            + "parsed         : 8\n"
            + "malformed      : 0\n"
            + "first request  : 2026-03-01T23:59:59.999Z\n"
            + "last request   : 2026-03-03T00:00:00.000Z\n"
            + "bytes served   : 8\n"
            + "\n"
            + "STATUS CODES\n"
            + "200         8  100.0%\n"
            + "\n"
            + "ENDPOINTS\n"
            + " requests    p50    p95    p99    max  endpoint\n"
            + "        3     11     12     12     12  GET /a\n"
            + "        3     14     15     15     15  GET /b\n"
            + "        2     16     17     17     17  GET /c\n"
            + "\n"
            + "USERS\n"
            + "distinct users : 3\n"
            + "sessions       : 4\n"
            + "per user (avg) : 1.33\n"
            + "most sessions  :\n"
            + "  u0002      2\n"
            + "  u0001      1\n"
            + "  u0003      1\n"
            + "\n"
            + "SLOWEST REQUESTS\n"
            + "      ms     line status  request\n"
            + "      17        8    200  GET /c\n"
            + "      16        7    200  GET /c\n"
            + "      15        6    200  GET /b\n"
            + "      14        5    200  GET /b\n"
            + "      13        4    200  GET /b\n"
            + "      12        3    200  GET /a\n"
            + "      11        2    200  GET /a\n"
            + "      10        1    200  GET /a\n",
            "Timestamps are not in order. Sessions compare each request with the same user's previous request in file order; first/last request are the earliest and latest timestamps."},
    };
}
