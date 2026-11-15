import java.util.Arrays;
import java.util.Objects;

public class Practice {
    // Nearest-rank percentile: the value at rank ceil(p/100 * n) of the sorted data (rank 1 = smallest).
    static double percentile(double[] ms, double p) {
        throw new UnsupportedOperationException("TODO: sort a copy, compute the rank, index from 0");
    }

    // Probability that at least one of `calls` independent calls is slow.
    static double fanOutSlow(double perCall, int calls) {
        throw new UnsupportedOperationException("TODO: start from the chance that every call is fast");
    }

    // CHECKS
    public static void main(String[] args) {
        double[] ten = {7, 2, 10, 4, 1, 9, 3, 8, 6, 5};
        check("ten samples: p50", 5.0, () -> percentile(ten, 50));
        check("ten samples: p90", 9.0, () -> percentile(ten, 90));
        check("ten samples: p100 is the maximum", 10.0, () -> percentile(ten, 100));
        double[] three = {30, 10, 20};
        check("small sample: p1", 10.0, () -> percentile(three, 1));
        check("small sample: p50", 20.0, () -> percentile(three, 50));
        check("small sample: p99", 30.0, () -> percentile(three, 99));
        double[] ties = {5, 100, 5, 5, 5};
        check("ties: p80", 5.0, () -> percentile(ties, 80));
        check("ties: p81", 100.0, () -> percentile(ties, 81));
        check("the caller's array is not reordered", "[3.0, 1.0, 2.0]", () -> {
            double[] ms = {3, 1, 2};
            percentile(ms, 50);
            return Arrays.toString(ms);
        });
        check("fan-out: 10 calls", "0.0956", () -> String.format(java.util.Locale.ROOT, "%.4f", fanOutSlow(0.01, 10)));
        check("fan-out: 100 calls", "0.634", () -> String.format(java.util.Locale.ROOT, "%.3f", fanOutSlow(0.01, 100)));
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
