import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Sizing {
    // CPU-bound work: count the primes in [from, to) by trial division
    static int countPrimes(int from, int to) {
        int count = 0;
        for (int n = from; n < to; n++) {
            if (n < 2) continue;
            boolean prime = true;
            for (int d = 2; d * d <= n; d++) {
                if (n % d == 0) { prime = false; break; }
            }
            if (prime) count++;
        }
        return count;
    }

    static long[] timeWith(int threads) throws Exception {
        int chunks = 144, size = 150_000;                  // 144 tasks of 150,000 numbers each
        long start = System.nanoTime();
        long total = 0;
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < chunks; i++) {
                int from = i * size, to = (i + 1) * size;
                tasks.add(() -> countPrimes(from, to));
            }
            for (Future<Integer> f : pool.invokeAll(tasks)) total += f.get();
        }
        return new long[] { total, (System.nanoTime() - start) / 1_000_000 };
    }

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("availableProcessors = " + cores);
        timeWith(cores);                                   // warm-up run, result ignored
        for (int threads : new int[] { 1, 4, cores, cores * 4 }) {
            long[] r = timeWith(threads);
            System.out.printf("%3d threads: %6d ms   (%d primes)%n", threads, r[1], r[0]);
        }
    }
}
