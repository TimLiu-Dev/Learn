import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.LongSupplier;
import java.util.stream.IntStream;

public class Ex4 {
    // Steps for n to reach 1 under the Collatz rule (n/2 if even, 3n+1 if odd).
    static int steps(int start) {
        long n = start;
        int count = 0;
        while (n != 1) { n = (n % 2 == 0) ? n / 2 : 3 * n + 1; count++; }
        return count;
    }

    static class StepSum extends RecursiveTask<Long> {
        private final int lo, hi;
        StepSum(int lo, int hi) { this.lo = lo; this.hi = hi; }
        @Override protected Long compute() {
            if (hi - lo <= 10_000) {                     // threshold: about 10,000 numbers per leaf
                long sum = 0;
                for (int i = lo; i < hi; i++) sum += steps(i);
                return sum;
            }
            int mid = (lo + hi) >>> 1;
            StepSum left = new StepSum(lo, mid);
            left.fork();
            return new StepSum(mid, hi).compute() + left.join();
        }
    }

    static void time(String label, LongSupplier block) {
        long t = System.nanoTime();
        long result = block.getAsLong();
        System.out.printf("%-16s %d in %d ms%n", label, result, (System.nanoTime() - t) / 1_000_000);
    }

    public static void main(String[] args) {
        int n = 5_000_000;
        for (int round = 0; round < 2; round++) {         // round 0 is warm-up
            if (round == 1) System.out.println("--- measured");
            time("plain loop", () -> { long s = 0; for (int i = 1; i < n; i++) s += steps(i); return s; });
            time("RecursiveTask", () -> ForkJoinPool.commonPool().invoke(new StepSum(1, n)));
            time("parallel stream", () -> IntStream.range(1, n).parallel().mapToLong(Ex4::steps).sum());
        }
    }
}
