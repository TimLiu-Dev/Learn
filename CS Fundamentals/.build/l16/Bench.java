import java.util.Random;
import java.util.function.ToLongFunction;

public class Bench {
    static long sumAll(int[] a) {                         // O(n)
        long s = 0;
        for (int x : a) s += x;
        return s;
    }

    static long countPairs(int[] a) {                     // O(n^2): Sedgewick's "2-sum"
        long count = 0;
        for (int i = 0; i < a.length; i++)
            for (int j = i + 1; j < a.length; j++)
                if (a[i] + a[j] == 0) count++;
        return count;
    }

    static long sink = 0;  // every result is added here and printed, so the JIT can't skip the work

    static double bestOf3Ms(int[] a, ToLongFunction<int[]> f) {
        for (int i = 0; i < 3; i++) sink += f.applyAsLong(a);   // warm-up: let the JIT compile f
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            long t0 = System.nanoTime();
            sink += f.applyAsLong(a);
            long t1 = System.nanoTime();
            best = Math.min(best, (t1 - t0) / 1e6);
        }
        return best;
    }

    static void doubling(String name, int startN, int rounds, ToLongFunction<int[]> f) {
        System.out.println("== " + name);
        double prev = 0;
        for (int r = 0, n = startN; r < rounds; r++, n *= 2) {
            Random rnd = new Random(n);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(-1_000_000, 1_000_000);
            double ms = bestOf3Ms(a, f);
            String ratio = prev > 0 ? String.format("%.1f", ms / prev) : "-";
            System.out.printf("n=%,11d %9.2f ms   ratio %s%n", n, ms, ratio);
            prev = ms;
        }
    }

    public static void main(String[] args) {
        doubling("sumAll: O(n)", 1_000_000, 6, Bench::sumAll);
        doubling("countPairs: O(n^2)", 2_000, 6, Bench::countPairs);
        System.out.println("(sink " + sink + ")");
    }
}
