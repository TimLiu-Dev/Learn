import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ForkJoin {
    static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int d = 2; d * d <= n; d++) if (n % d == 0) return false;
        return true;
    }

    // Count the primes in [lo, hi): split in half until a piece is at most `threshold` numbers.
    static class CountPrimes extends RecursiveTask<Integer> {
        private final int lo, hi, threshold;
        private final AtomicInteger leaves;
        CountPrimes(int lo, int hi, int threshold, AtomicInteger leaves) {
            this.lo = lo; this.hi = hi; this.threshold = threshold; this.leaves = leaves;
        }
        @Override protected Integer compute() {
            if (hi - lo <= threshold) {                       // small enough: a plain loop
                leaves.incrementAndGet();
                int count = 0;
                for (int i = lo; i < hi; i++) if (isPrime(i)) count++;
                return count;
            }
            int mid = (lo + hi) >>> 1;
            CountPrimes left = new CountPrimes(lo, mid, threshold, leaves);
            left.fork();                                      // queue the left half for any worker
            int right = new CountPrimes(mid, hi, threshold, leaves).compute();  // do the right half here
            return right + left.join();                       // then wait for the left half
        }
    }

    public static void main(String[] args) {
        int n = 10_000_000;
        System.out.println("cores = " + Runtime.getRuntime().availableProcessors()
            + ", common pool parallelism = " + ForkJoinPool.getCommonPoolParallelism());
        for (int round = 0; round < 2; round++) {              // round 0 is warm-up, not printed
            long t = System.nanoTime();
            int seq = 0;
            for (int i = 0; i < n; i++) if (isPrime(i)) seq++;
            if (round == 1) System.out.println("plain loop:          " + seq + " primes, " + (System.nanoTime() - t) / 1_000_000 + " ms");
            for (int threshold : new int[] {5_000_000, 100_000, 1_000, 10}) {
                AtomicInteger leaves = new AtomicInteger();
                t = System.nanoTime();
                int count = ForkJoinPool.commonPool().invoke(new CountPrimes(0, n, threshold, leaves));
                long ms = (System.nanoTime() - t) / 1_000_000;
                if (round == 1) System.out.printf("threshold %9d: %d primes, %7d leaves, %4d ms%n", threshold, count, leaves.get(), ms);
            }
        }
    }
}
