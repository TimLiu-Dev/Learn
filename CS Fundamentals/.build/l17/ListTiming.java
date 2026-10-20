import java.util.*;
import java.util.function.LongSupplier;

public class ListTiming {
    static long sumAll(List<Integer> list) {
        long total = 0;
        for (int x : list) total += x;              // iterator: O(n) for BOTH lists
        return total;
    }

    static long sumByIndex(List<Integer> list) {
        long total = 0;
        for (int i = 0; i < list.size(); i++) total += list.get(i);  // O(1) array, O(n) linked
        return total;
    }

    static double bestMillis(int runs, LongSupplier block) {
        long best = Long.MAX_VALUE;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            long result = block.getAsLong();
            long elapsed = System.nanoTime() - start;
            if (result == 42) System.out.println();  // use the result so it can't be skipped
            best = Math.min(best, elapsed);
        }
        return best / 1e6;
    }

    static List<Integer> range(int n) {
        List<Integer> xs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) xs.add(i);
        return xs;
    }

    public static void main(String[] args) {
        int n = 1_000_000;
        List<Integer> array = new ArrayList<>(range(n));
        List<Integer> linked = new LinkedList<>(array);
        for (int i = 0; i < 3; i++) { sumAll(array); sumAll(linked); }   // warm-up for the JIT
        System.out.printf("iterate 1M:  ArrayList %.1f ms   LinkedList %.1f ms%n",
            bestMillis(7, () -> sumAll(array)), bestMillis(7, () -> sumAll(linked)));

        double previous = 0;
        for (int size : new int[] {5_000, 10_000, 20_000, 40_000}) {
            List<Integer> a = new ArrayList<>(range(size));
            List<Integer> l = new LinkedList<>(a);
            double ta = bestMillis(3, () -> sumByIndex(a));
            double tl = bestMillis(3, () -> sumByIndex(l));
            String ratio = previous > 0 ? String.format("  x%.1f", tl / previous) : "";
            System.out.printf("get(i) loop n=%6d: ArrayList %6.2f ms   LinkedList %8.1f ms%s%n", size, ta, tl, ratio);
            previous = tl;
        }
    }
}
