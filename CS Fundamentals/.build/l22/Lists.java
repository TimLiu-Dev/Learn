import java.util.*;
import java.util.function.LongSupplier;

public class Lists {
    static final int N = 5_000_000;

    static double[] bestMs(LongSupplier f) {
        double best = Double.MAX_VALUE;
        long result = 0;
        for (int i = 0; i < 5; i++) {
            long t = System.nanoTime();
            result = f.getAsLong();
            best = Math.min(best, (System.nanoTime() - t) / 1e6);
        }
        return new double[] {best, result};
    }

    public static void main(String[] args) {
        int[] array = new int[N];                          // N ints side by side
        List<Integer> boxed = new ArrayList<>(N);          // N pointers to Integer objects
        for (int i = 0; i < N; i++) { array[i] = i; boxed.add(i); }
        List<Integer> shuffled = new ArrayList<>(boxed);   // same objects, visited out of memory order
        Collections.shuffle(shuffled, new Random(1));
        List<Integer> linked = new LinkedList<>(boxed);    // N nodes, each pointing to the next

        String[] names = {"int[]", "ArrayList<Integer>", "ArrayList, shuffled", "LinkedList<Integer>"};
        double[][] r = {
            bestMs(() -> { long s = 0; for (int x : array) s += x; return s; }),
            bestMs(() -> { long s = 0; for (int x : boxed) s += x; return s; }),
            bestMs(() -> { long s = 0; for (int x : shuffled) s += x; return s; }),
            bestMs(() -> { long s = 0; for (int x : linked) s += x; return s; }),
        };
        for (int i = 0; i < r.length; i++)
            System.out.printf("%-20s %7.1f ms  %5.1fx  (sum %d)%n", names[i], r[i][0], r[i][0] / r[0][0], (long) r[i][1]);
    }
}
