import java.util.List;
import java.util.Objects;

public class Practice {
    static final int MINUTES_PER_DAY = 1440;

    // Reduce delta first (MCS: take remainders at every step), so start + delta can't overflow.
    static int minuteAfter(int start, int delta) {
        return Math.floorMod(start + Math.floorMod(delta, MINUTES_PER_DAY), MINUTES_PER_DAY);
    }

    // How many times n can be halved before it reaches 1: floor(log2 n). n must be at least 1.
    static int halvings(long n) {
        if (n < 1) throw new IllegalArgumentException("n must be at least 1, was " + n);
        int count = 0;
        for (long x = n; x > 1; x /= 2) count++;
        return count;
    }

    // CHECKS
    public static void main(String[] args) {
        check("twenty minutes after 23:50 is 00:10", 10, () -> minuteAfter(23 * 60 + 50, 20));
        check("thirty minutes before 00:10 is 23:40", 1420, () -> minuteAfter(10, -30));
        check("whole days cancel out (backwards)", 600, () -> minuteAfter(600, -3 * MINUTES_PER_DAY));
        check("whole days cancel out (forwards)", 605, () -> minuteAfter(600, 1000 * MINUTES_PER_DAY + 5));
        check("a huge delta does not overflow", 126, () -> minuteAfter(1439, Integer.MAX_VALUE));
        check("a huge negative delta does not overflow", 1312, () -> minuteAfter(0, Integer.MIN_VALUE));
        check("halvings is floor of log2", List.of(0, 1, 1, 3, 9, 29, 62),
            () -> List.of(halvings(1), halvings(2), halvings(3), halvings(8), halvings(1000), halvings(1_000_000_000), halvings(Long.MAX_VALUE)));
        check("halvings rejects zero", "IllegalArgumentException", () -> {
            try { halvings(0); return "no exception"; } catch (IllegalArgumentException e) { return "IllegalArgumentException"; }
        });
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
