import java.util.Arrays;

// Pair-sum on a SORTED array, and in-place dedupe: two indices instead of two nested loops.
public class TwoPointers {
    // Brute force: try every pair. O(n²) comparisons. Returns {i, j} or null.
    static int[] pairBrute(int[] a, int target) {
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] + a[j] == target) return new int[] {i, j};
        return null;
    }

    // Two pointers: start at both ends and move one of them inward each step. O(n).
    static int[] pairTwoPointers(int[] a, int target, boolean trace) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            int sum = a[lo] + a[hi];
            if (trace) System.out.println("  lo=" + lo + " hi=" + hi + "  " + a[lo] + " + " + a[hi] + " = " + sum);
            if (sum == target) return new int[] {lo, hi};
            if (sum < target) lo++;       // a[lo] is too small to pair with anything left
            else hi--;                    // a[hi] is too big to pair with anything left
        }
        return null;
    }

    // Remove duplicates from a sorted array in place; returns the new logical length.
    // slow = where the next unique value goes; fast = the value being examined.
    static int dedupe(int[] a) {
        if (a.length == 0) return 0;
        int slow = 1;
        for (int fast = 1; fast < a.length; fast++) {
            if (a[fast] != a[slow - 1]) a[slow++] = a[fast];
        }
        return slow;
    }

    public static void main(String[] args) {
        int[] a = {1, 3, 4, 6, 8, 11, 15};
        System.out.println("find 14:");
        System.out.println("two pointers -> " + Arrays.toString(pairTwoPointers(a, 14, true)));
        System.out.println("brute force  -> " + Arrays.toString(pairBrute(a, 14)));
        System.out.println("find 13 -> " + Arrays.toString(pairTwoPointers(a, 13, false)));

        int[] d = {1, 1, 2, 3, 3, 3, 7, 9, 9};
        int n = dedupe(d);
        System.out.println("dedupe -> length " + n + ": " + Arrays.toString(Arrays.copyOf(d, n))
                + "  (whole array: " + Arrays.toString(d) + ")");
    }
}
