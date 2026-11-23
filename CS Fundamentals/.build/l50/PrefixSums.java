import java.util.*;

// Prefix sums: pay O(n) once, then every range sum costs O(1).
public class PrefixSums {
    // pre[i] = a[0] + … + a[i - 1], so pre has n + 1 slots and pre[0] = 0.
    static long[] prefixSums(int[] a) {
        long[] pre = new long[a.length + 1];                 // long: a sum of many ints can overflow (L2)
        for (int i = 0; i < a.length; i++) pre[i + 1] = pre[i] + a[i];
        return pre;
    }

    // Sum of a[from, to), half-open like substring: two lookups, however long the range.
    static long rangeSum(long[] pre, int from, int to) { return pre[to] - pre[from]; }

    // How many contiguous runs sum to exactly k? The run a[i, j) sums to k exactly when
    // pre[j] - pre[i] == k, so at each j, count the earlier prefix sums equal to pre[j] - k.
    static int countSubarrays(int[] a, long k) {
        Map<Long, Integer> seen = new HashMap<>();           // prefix sum -> how many times it has occurred
        seen.put(0L, 1);                                     // the empty prefix: lets a run start at index 0
        long sum = 0;
        int count = 0;
        for (int x : a) {
            sum += x;
            count += seen.getOrDefault(sum - k, 0);          // each earlier prefix that is k smaller ends a run here
            seen.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // The obviously correct O(n²) version: try every run. Returns the runs as half-open ranges [from, to).
    static List<String> runsBrute(int[] a, long k) {
        List<String> runs = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            long sum = 0;
            for (int j = i; j < a.length; j++) {
                sum += a[j];
                if (sum == k) runs.add("[" + i + ", " + (j + 1) + ")");
            }
        }
        return runs;
    }

    public static void main(String[] args) {
        int[] a = {3, -1, 4, 1, -5, 9, 2, -6};
        long[] pre = prefixSums(a);
        System.out.println("a   = " + Arrays.toString(a));
        System.out.println("pre = " + Arrays.toString(pre));
        System.out.println("sum of a[2, 6) = " + rangeSum(pre, 2, 6));
        System.out.println("runs summing to 4: " + countSubarrays(a, 4) + "  " + runsBrute(a, 4));

        Random rnd = new Random(50);                         // differential test, as in the window-maximum section
        for (int t = 0; t < 10_000; t++) {
            int[] xs = new int[rnd.nextInt(12)];
            for (int i = 0; i < xs.length; i++) xs[i] = rnd.nextInt(7) - 3;
            long k = rnd.nextInt(7) - 3;
            if (countSubarrays(xs, k) != runsBrute(xs, k).size())
                throw new AssertionError("differs on " + Arrays.toString(xs) + " k=" + k);
        }
        System.out.println("10000 random arrays with negatives: map count == brute force");
    }
}
