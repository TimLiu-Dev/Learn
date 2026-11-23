import java.util.*;

public class Practice {
    // The length of the longest contiguous run of a that sums to exactly k (0 if none).
    static int longestWithSum(int[] a, long k) {
        Map<Long, Integer> firstAt = new HashMap<>();       // prefix sum -> the first index where it occurred
        long sum = 0;
        int best = 0;
        for (int i = 0; i < a.length; i++) {
            throw new UnsupportedOperationException("TODO: add a[i] to sum, look up sum - k, remember where sum first occurred");
        }
        return best;
    }

    // CHECKS
    public static void main(String[] args) {
        check("a run with negatives inside is found", 4, () -> longestWithSum(new int[]{1, -1, 5, -2, 3}, 3));
        check("a run may start at index 0", 2, () -> longestWithSum(new int[]{2, 1, 9}, 3));
        check("the earliest start gives the longest run", 4, () -> longestWithSum(new int[]{1, -1, 1, -1}, 0));
        check("no run gives 0", 0, () -> longestWithSum(new int[]{1, 2, 3}, 7));
        check("an empty array gives 0", 0, () -> longestWithSum(new int[]{}, 0));
        check("sums that pass the int range still work", 3,
                () -> longestWithSum(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 1}, 2L * Integer.MAX_VALUE + 1));
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
