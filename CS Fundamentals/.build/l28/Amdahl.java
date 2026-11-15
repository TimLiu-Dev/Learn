import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Amdahl {
    static int countPrimes(int from, int to) {               // same trial division as Sizing
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

    // Amdahl's law: p = the fraction of the 1-thread time that can run in parallel
    static double speedup(double p, int n) {
        return 1 / ((1 - p) + p / n);
    }

    static final int SIZE = 150_000, SERIAL_CHUNKS = 28, CHUNKS = 144;

    // Pretend chunks 0..27 can't be split (say, they parse the input). Only the rest is parallel.
    static int serialPart() {
        return countPrimes(0, SERIAL_CHUNKS * SIZE);
    }

    static int parallelPart(int threads) throws Exception {
        int total = 0;
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = SERIAL_CHUNKS; i < CHUNKS; i++) {
                int from = i * SIZE, to = (i + 1) * SIZE;
                tasks.add(() -> countPrimes(from, to));
            }
            for (Future<Integer> f : pool.invokeAll(tasks)) total += f.get();
        }
        return total;
    }

    static long millis(Callable<Integer> work) throws Exception {
        long start = System.nanoTime();
        work.call();
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        millis(() -> serialPart() + parallelPart(cores));    // warm-up run, result ignored
        long serial = millis(Amdahl::serialPart);
        long one = millis(() -> serialPart() + parallelPart(1));
        double p = 1 - (double) serial / one;
        System.out.printf("1 thread: %d ms, of which %d ms is serial, so p = %.2f%n", one, serial, p);
        for (int n : new int[] { 4, cores }) {
            long t = millis(() -> serialPart() + parallelPart(n));
            System.out.printf("%2d threads: %4d ms  measured %4.1fx  Amdahl's limit %4.1fx%n",
                    n, t, (double) one / t, speedup(p, n));
        }
        System.out.printf("64 cores could give at most %.1fx; infinitely many, %.1fx%n",
                speedup(p, 64), 1 / (1 - p));
    }
}
