import java.util.Locale;
import java.util.Objects;

public class Practice {
    // Amdahl's law: the best speed-up on n cores when a fraction p of the work can run in parallel.
    static double amdahlSpeedup(double p, int n) {
        if (p < 0 || p > 1) throw new IllegalArgumentException("p must be between 0 and 1, was " + p);
        if (n < 1) throw new IllegalArgumentException("n must be at least 1, was " + n);
        return 1 / ((1 - p) + p / n);
    }

    // Little's law: how many threads are busy on average, rounded up to whole threads.
    static int poolSizeFor(int requestsPerSecond, int latencyMs) {
        long busyMs = (long) requestsPerSecond * latencyMs;   // long: 50,000 × 60,000 overflows an int
        return (int) ((busyMs + 999) / 1000);                  // ceiling division: 1.5 threads means 2
    }

    // CHECKS
    static String rounded(double x) { return String.format(Locale.ROOT, "%.3f", x); }

    static String rejects(double p, int n) {
        try {
            amdahlSpeedup(p, n);
            return "accepted";
        } catch (IllegalArgumentException e) {
            return "rejected";
        }
    }

    public static void main(String[] args) {
        check("with 10 percent serial, 64 cores give under 9x", "8.767", () -> rounded(amdahlSpeedup(0.9, 64)));
        check("one core or nothing parallel gives 1x, all parallel gives n", "1.000 1.000 64.000",
                () -> rounded(amdahlSpeedup(0.9, 1)) + " " + rounded(amdahlSpeedup(0.0, 64)) + " " + rounded(amdahlSpeedup(1.0, 64)));
        check("impossible inputs are rejected", "rejected rejected rejected",
                () -> rejects(1.5, 4) + " " + rejects(-0.1, 4) + " " + rejects(0.5, 0));
        check("200 requests per second at 50 ms keep 10 threads busy", 10, () -> poolSizeFor(200, 50));
        check("a fraction of a thread rounds up", "2 1 0", () -> poolSizeFor(30, 50) + " " + poolSizeFor(1, 1) + " " + poolSizeFor(0, 50));
        check("big numbers don't overflow", 3_000_000, () -> poolSizeFor(50_000, 60_000));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
