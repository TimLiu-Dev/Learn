import java.util.Objects;

public class Practice {
    // Assumptions in, numbers out. Each name says its unit.
    static final long SECONDS_PER_MONTH = 30L * 24 * 3600;   // 2,592,000

    // newPerMonth: new rows per month; hotFraction: the share of one month's new rows that is hot
    record Assumptions(double newPerMonth, double readsPerWrite, double bytesPerRow, int years, double hotFraction) { }

    // storageBytes: every row written in years; cacheBytes: the hot share of one month's rows
    record Estimate(double writesPerSec, double readsPerSec, double storageBytes, double cacheBytes) { }

    static Estimate estimate(Assumptions a) {
        throw new UnsupportedOperationException("TODO: writes/s, reads/s, storage and cache, each from the assumptions");
    }

    // The shortest base62 length with at least `codes` distinct codes (62^k >= codes).
    static int minCodeLength(long codes) {
        throw new UnsupportedOperationException("TODO: count base62 digits, without letting anything overflow");
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        Assumptions shortener = new Assumptions(100e6, 100.0, 500.0, 5, 0.2);
        Assumptions pastebin = new Assumptions(10e6, 10.0, 10_000.0, 5, 0.2);
        check("shortener writes per second", 38.58, () -> cents(estimate(shortener).writesPerSec()));
        check("shortener reads per second", 3858.02, () -> cents(estimate(shortener).readsPerSec()));
        check("shortener storage bytes (3 TB)", 3_000_000_000_000L, () -> Math.round(estimate(shortener).storageBytes()));
        check("shortener cache bytes (10 GB)", 10_000_000_000L, () -> Math.round(estimate(shortener).cacheBytes()));
        check("pastebin storage bytes (6 TB)", 6_000_000_000_000L, () -> Math.round(estimate(pastebin).storageBytes()));
        check("pastebin cache bytes (20 GB)", 20_000_000_000L, () -> Math.round(estimate(pastebin).cacheBytes()));
        check("62 codes", 1, () -> minCodeLength(62));
        check("63 codes", 2, () -> minCodeLength(63));
        check("62^2 + 1 codes", 3, () -> minCodeLength(3845));
        check("6 billion codes", 6, () -> minCodeLength(6_000_000_000L));
        check("one link still needs one character", 1, () -> minCodeLength(1));
        check("every long fits in 11 characters", 11, () -> minCodeLength(Long.MAX_VALUE));
    }

    static double cents(double x) { return Math.round(x * 100) / 100.0; }

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
