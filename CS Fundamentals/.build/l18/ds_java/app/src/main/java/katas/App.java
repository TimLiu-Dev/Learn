package katas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class App {
    private static List<Integer> randomInts(int n) {
        Random rnd = new Random(42);
        List<Integer> xs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) xs.add(rnd.nextInt());
        return xs;
    }

    // Best of 3 runs in ms, each on a fresh copy of the input.
    private static double timeMs(List<Integer> input, Consumer<List<Integer>> sort) {
        double best = Double.MAX_VALUE;
        for (int run = 0; run < 3; run++) {
            List<Integer> xs = new ArrayList<>(input);
            long start = System.nanoTime();
            sort.accept(xs);
            best = Math.min(best, (System.nanoTime() - start) / 1e6);
        }
        return best;
    }

    public static void main(String[] args) {
        List<String> names = List.of("insertion", "merge", "quick", "JDK sort()", "int[]");
        List<Consumer<List<Integer>>> sorts = List.of(
            Sorting::insertionSort,
            Sorting::mergeSort,
            Sorting::quickSort,
            xs -> xs.sort(null),                                                  // TimSort on boxed Integers
            xs -> Arrays.sort(xs.stream().mapToInt(Integer::intValue).toArray())); // Dual-Pivot Quicksort on ints (+ copy)
        for (int warm = 0; warm < 5; warm++)                                      // JIT warm-up
            for (Consumer<List<Integer>> s : sorts) timeMs(randomInts(5_000), s);

        StringBuilder head = new StringBuilder("       n");
        for (String name : names) head.append(String.format("%12s", name));
        System.out.println(head);
        for (int n : new int[] {10_000, 20_000, 40_000, 80_000}) {
            List<Integer> input = randomInts(n);
            StringBuilder row = new StringBuilder(String.format("%,8d", n));
            for (Consumer<List<Integer>> s : sorts) row.append(String.format("%12.2f", timeMs(input, s)));
            System.out.println(row);
        }
        System.out.println("(milliseconds, best of 3, on my machine; yours will differ)");
    }
}
