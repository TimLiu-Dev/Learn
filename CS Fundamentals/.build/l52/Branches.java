import java.util.Arrays;
import java.util.Random;
import java.util.function.ToLongFunction;

// Sum the "big" values (>= 128) of 2,097,152 random bytes, the same loop over the same numbers,
// once in random order and once sorted. Only the order changes, so only the branch changes.
public class Branches {
    static long sumBig(int[] data) {
        long s = 0;
        for (int x : data) if (x >= 128) s += x;
        return s;
    }

    // No branch at all: build a mask from the sign bit (L48). (x - 128) >> 31 is -1 (all ones)
    // when x < 128 and 0 otherwise, so ~ of it keeps x only when x >= 128.
    static long sumBigBranchless(int[] data) {
        long s = 0;
        for (int x : data) s += ~((x - 128) >> 31) & x;
        return s;
    }

    static long sink = 0;

    static double bestOf(int[] data, ToLongFunction<int[]> f) {
        double best = Double.MAX_VALUE;
        for (int r = 0; r < 7; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < 20; i++) sink += f.applyAsLong(data);
            best = Math.min(best, (System.nanoTime() - t0) / 20.0 / data.length);
        }
        return best;
    }

    public static void main(String[] args) {
        int[] random = new int[2_097_152];
        Random rng = new Random(42);                          // ONE generator for the whole array
        for (int i = 0; i < random.length; i++) random[i] = rng.nextInt(256);
        int[] sorted = random.clone();
        Arrays.sort(sorted);
        for (int[] d : new int[][]{random, sorted})
            for (int k = 0; k < 3; k++) { sumBig(d); sumBigBranchless(d); }   // warm up (L16)
        System.out.printf("if, random order  %.2f ns per element%n", bestOf(random, Branches::sumBig));
        System.out.printf("if, sorted        %.2f ns per element%n", bestOf(sorted, Branches::sumBig));
        System.out.printf("mask, random      %.2f ns per element%n", bestOf(random, Branches::sumBigBranchless));
        System.out.printf("mask, sorted      %.2f ns per element%n", bestOf(sorted, Branches::sumBigBranchless));
        System.out.println("(sink " + sink + ")");
    }
}
