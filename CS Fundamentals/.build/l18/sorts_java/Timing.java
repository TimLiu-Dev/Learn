import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Timing {
    static int[] randomInts(int n, long seed) {
        Random rnd = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt();
        return a;
    }

    // Best of 3 runs, in milliseconds. Each run sorts a fresh copy.
    static double timeMs(int[] input, Consumer<int[]> sort) {
        double best = Double.MAX_VALUE;
        for (int run = 0; run < 3; run++) {
            int[] a = input.clone();
            long start = System.nanoTime();
            sort.accept(a);
            best = Math.min(best, (System.nanoTime() - start) / 1e6);
        }
        return best;
    }

    public static void main(String[] args) {
        List<String> names = List.of("insertion", "merge", "quick", "Arrays.sort");
        List<Consumer<int[]>> sorts = List.of(InsertionSort::sort, MergeSort::sort, QuickSort::sort, Arrays::sort);
        for (int warm = 0; warm < 5; warm++)                                 // JIT warm-up
            for (Consumer<int[]> s : sorts) timeMs(randomInts(5_000, warm), s);

        StringBuilder head = new StringBuilder("       n");
        for (String name : names) head.append(String.format("%14s", name));
        System.out.println(head);
        double[] prev = new double[sorts.size()];
        for (int n : new int[] {10_000, 20_000, 40_000, 80_000}) {
            int[] input = randomInts(n, 42);
            StringBuilder row = new StringBuilder(String.format("%,8d", n));
            for (int k = 0; k < sorts.size(); k++) {
                double ms = timeMs(input, sorts.get(k));
                String ratio = prev[k] > 0 ? String.format("x%.1f", ms / prev[k]) : "";
                row.append(String.format("%9.2f%5s", ms, ratio));
                prev[k] = ms;
            }
            System.out.println(row);
        }
    }
}
