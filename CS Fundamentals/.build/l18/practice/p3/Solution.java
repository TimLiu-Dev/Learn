import java.util.*;

public class Practice {
    // Number of pairs i < j with a[i] > a[j]. Leaves a unchanged.
    static long countInversions(int[] a) { return sortAndCount(a.clone()); }

    // Merge-sorts a in place and returns the number of inversions it had.
    static long sortAndCount(int[] a) {
        if (a.length < 2) return 0;                  // 0 or 1 items: sorted, no pairs
        int[] left = Arrays.copyOfRange(a, 0, a.length / 2);
        int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);
        long count = sortAndCount(left) + sortAndCount(right);   // pairs inside each half
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (right[j] < left[i]) {
                count += left.length - i;            // right[j] is smaller than every item left[i..] still waiting
                a[k++] = right[j++];
            } else {
                a[k++] = left[i++];                  // ties take from the left: equal is not an inversion
            }
        }
        while (i < left.length) a[k++] = left[i++];
        while (j < right.length) a[k++] = right[j++];
        return count;
    }

    // CHECKS
    static long bruteForce(int[] a) {
        long n = 0;
        for (int i = 0; i < a.length; i++) for (int j = i + 1; j < a.length; j++) if (a[i] > a[j]) n++;
        return n;
    }

    public static void main(String[] args) {
        check("empty and single-element arrays have none", List.of(0L, 0L), () -> List.of(countInversions(new int[0]), countInversions(new int[]{42})));
        check("small cases", List.of(1L, 2L, 0L), () -> List.of(countInversions(new int[]{2, 1}), countInversions(new int[]{3, 1, 2}), countInversions(new int[]{1, 2, 3, 4})));
        check("equal values are not inversions", List.of(0L, 3L), () -> List.of(countInversions(new int[]{2, 2, 2}), countInversions(new int[]{3, 1, 3, 1})));
        check("reversed has every pair, and sortAndCount sorts", "499500 true", () -> {
            int[] a = new int[1_000];
            for (int i = 0; i < a.length; i++) a[i] = 999 - i;
            long n = sortAndCount(a);
            int[] sorted = new int[1_000];
            for (int i = 0; i < sorted.length; i++) sorted[i] = i;
            return n + " " + Arrays.equals(sorted, a);
        });
        check("countInversions leaves its input alone", "[3, 1, 2]", () -> { int[] a = {3, 1, 2}; countInversions(a); return Arrays.toString(a); });
        check("agrees with brute force on random arrays with duplicates", true, () -> {
            Random rnd = new Random(18);
            for (int t = 0; t < 200; t++) {
                int[] a = new int[rnd.nextInt(40)];
                for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
                if (countInversions(a) != bruteForce(a)) return false;
            }
            return true;
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
