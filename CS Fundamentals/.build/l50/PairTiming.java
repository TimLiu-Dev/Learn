import java.util.function.LongSupplier;
import java.util.function.ToLongBiFunction;

public class PairTiming {
    // Worst case for both: all values even, target odd, so no pair is ever found.
    static long bruteSteps(int[] a, int target) {
        long steps = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++) {
                steps++;
                if (a[i] + a[j] == target) return steps;
            }
        return steps;
    }

    static long twoPointerSteps(int[] a, int target) {
        long steps = 0;
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            steps++;
            int sum = a[lo] + a[hi];
            if (sum == target) return steps;
            if (sum < target) lo++; else hi--;
        }
        return steps;
    }

    static long sink = 0;  // L16: every result is used, so the JIT can't skip the work

    static double bestOf3Ms(LongSupplier f) {
        for (int i = 0; i < 3; i++) sink += f.getAsLong();          // warm-up (L16)
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            long t0 = System.nanoTime();
            sink += f.getAsLong();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
        }
        return best;
    }

    static void doubling(String name, int startN, int rounds, ToLongBiFunction<int[], Integer> f) {
        System.out.println("== " + name);
        double prev = 0;
        for (int r = 0, n = startN; r < rounds; r++, n *= 2) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = i * 2;               // sorted, all even
            int target = n + 1;                                     // odd: never found
            long steps = f.applyAsLong(a, target);
            double ms = bestOf3Ms(() -> f.applyAsLong(a, target));
            String ratio = prev > 0 ? String.format("%.1f", ms / prev) : "-";
            System.out.printf("n=%,10d  steps=%,15d  %9.3f ms  ratio %s%n", n, steps, ms, ratio);
            prev = ms;
        }
    }

    public static void main(String[] args) {
        doubling("brute force: O(n^2)", 5_000, 5, PairTiming::bruteSteps);
        doubling("two pointers: O(n)", 1_000_000, 5, PairTiming::twoPointerSteps);
        System.out.println("(sink " + sink + ")");
    }
}
