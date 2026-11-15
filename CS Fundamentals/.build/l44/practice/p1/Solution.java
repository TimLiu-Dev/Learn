import java.util.Objects;

public class Practice {
    // Assumptions in, numbers out. Each name says its unit.
    static final long SECONDS_PER_MONTH = 30L * 24 * 3600;   // 2,592,000

    // newPerMonth: new rows per month; hotFraction: the share of one month's new rows that is hot
    record Assumptions(double newPerMonth, double readsPerWrite, double bytesPerRow, int years, double hotFraction) { }

    // storageBytes: every row written in years; cacheBytes: the hot share of one month's rows
    record Estimate(double writesPerSec, double readsPerSec, double storageBytes, double cacheBytes) { }

    static Estimate estimate(Assumptions a) {
        double writesPerSec = a.newPerMonth() / SECONDS_PER_MONTH;
        return new Estimate(
            writesPerSec,
            writesPerSec * a.readsPerWrite(),
            a.newPerMonth() * 12 * a.years() * a.bytesPerRow(),   // months, not years
            a.newPerMonth() * a.hotFraction() * a.bytesPerRow());
    }

    // codes distinct codes are numbered 0 .. codes-1, so count the base62 digits of codes-1.
    // Dividing never overflows; multiplying up to 62^k would.
    static int minCodeLength(long codes) {
        if (codes < 1) throw new IllegalArgumentException("need at least one code: " + codes);
        long rest = (codes - 1) / 62;
        int length = 1;
        while (rest > 0) {
            rest /= 62;
            length++;
        }
        return length;
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
