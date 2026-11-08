import java.util.Arrays;
import java.util.Random;

public class Knapsack {
    // 0/1 knapsack: pick items (each at most once) with total weight <= capacity, maximising value.
    static int bruteForce(int[] w, int[] v, int cap) {            // every subset: 2^n
        int best = 0;
        for (int mask = 0; mask < (1 << w.length); mask++) {
            int weight = 0, value = 0;
            for (int i = 0; i < w.length; i++) if ((mask & (1 << i)) != 0) { weight += w[i]; value += v[i]; }
            if (weight <= cap && value > best) best = value;
        }
        return best;
    }

    static int dp(int[] w, int[] v, int cap) {                    // L21: best[c] = best value within capacity c
        int[] best = new int[cap + 1];
        for (int i = 0; i < w.length; i++)
            for (int c = cap; c >= w[i]; c--) best[c] = Math.max(best[c], best[c - w[i]] + v[i]);
        return best[cap];                                         // high to low: each item used at most once
    }

    public static void main(String[] args) {
        Random rnd = new Random(51);
        int n = 24;
        int[] w = new int[n], v = new int[n];
        for (int i = 0; i < n; i++) w[i] = 1 + rnd.nextInt(100);
        for (int i = 0; i < n; i++) v[i] = 1 + rnd.nextInt(100);
        int cap = 500;
        for (int i = 0; i < 3; i++) { bruteForce(Arrays.copyOf(w, 16), Arrays.copyOf(v, 16), cap); dp(w, v, cap); }
        long t0 = System.nanoTime();
        int a = bruteForce(w, v, cap);
        long tb = System.nanoTime() - t0;
        t0 = System.nanoTime();
        int b = dp(w, v, cap);
        long td = System.nanoTime() - t0;
        System.out.println("n=" + n + " cap=" + cap + "  brute force=" + a + " in " + tb / 1_000_000 + " ms (2^" + n + " = " + (1 << n) + " subsets)");
        System.out.println("n=" + n + " cap=" + cap + "  dp=" + b + " in " + td / 1000 + " µs (" + (long) n * (cap + 1) + " updates at most)");
        // The same items weighed in milligrams instead of kilograms: same problem, a million times the table
        long cells = (long) n * (cap * 1_000_000L + 1);
        System.out.println("weights x 1,000,000: the dp needs up to " + cells + " updates and a " + (cap * 1_000_000L + 1) * 4 / 1_000_000 + " MB array");
    }
}
