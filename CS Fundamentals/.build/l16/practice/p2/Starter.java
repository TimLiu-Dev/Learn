import java.util.*;
import java.util.function.IntToLongFunction;

public class Practice {
    // count.applyAsLong(n) = how many steps some code takes at size n.
    static List<Double> doublingRatios(IntToLongFunction count, int start, int rows) {
        throw new UnsupportedOperationException("TODO: count each size once, then divide each count by the one before it");
    }

    // T(2n) / T(n) ≈ 2^b, so b ≈ log2(ratio).
    static int exponent(double ratio) {
        throw new UnsupportedOperationException("TODO: log2, then round to the nearest whole number");
    }

    // CHECKS
    // Step counts to experiment on. None of them times anything.
    static long linearSteps(int n) { return n; }

    static long nLogNSteps(int n) {                 // a halving loop inside a linear one
        long steps = 0;
        for (int i = 0; i < n; i++)
            for (int k = n; k > 1; k /= 2) steps++;
        return steps;
    }

    static long uniqueSlowComparisons(int n) {      // uniqueSlow, counting comparisons
        List<String> result = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i < n; i++) {
            comparisons += result.size();           // contains compares with every element so far
            result.add("w" + i);
        }
        return comparisons;
    }

    public static void main(String[] args) {
        check("a linear count gives ratios of 2", List.of(2.0, 2.0, 2.0), () -> doublingRatios(Practice::linearSteps, 1_000, 4));
        check("one size gives no ratios", List.of(), () -> doublingRatios(Practice::linearSteps, 1_000, 1));
        check("exponent of 1.0 is 0", 0, () -> exponent(1.0));
        check("exponent of 2.1 is 1", 1, () -> exponent(2.1));
        check("exponent of 3.9 is 2", 2, () -> exponent(3.9));
        check("exponent of 7.6 is 3", 3, () -> exponent(7.6));
        check("n log n ratios are between 2 and 2.5", true, () -> {
            List<Double> r = doublingRatios(Practice::nLogNSteps, 1_024, 4);
            return r.size() == 3 && r.stream().allMatch(x -> x > 2.0 && x < 2.5);
        });
        check("the hidden loop in uniqueSlow is quadratic", 2, () -> {
            List<Double> r = doublingRatios(Practice::uniqueSlowComparisons, 500, 5);
            return exponent(r.get(r.size() - 1));
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
