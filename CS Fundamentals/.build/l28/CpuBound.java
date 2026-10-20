import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class CpuBound {
    static int primesIn(int from, int to) {
        int count = 0;
        for (int n = Math.max(from, 2); n < to; n++) {
            boolean prime = true;
            for (int d = 2; d * d <= n; d++) if (n % d == 0) { prime = false; break; }
            if (prime) count++;
        }
        return count;
    }

    static void timed(String name, Supplier<ExecutorService> make) throws Exception {
        long start = System.nanoTime();
        long total = 0;
        try (ExecutorService ex = make.get()) {
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 144; i++) {
                int from = i * 150_000, to = (i + 1) * 150_000;
                tasks.add(() -> primesIn(from, to));
            }
            for (Future<Integer> f : ex.invokeAll(tasks)) total += f.get();
        }
        System.out.printf("%-24s %5d ms  (%d primes)%n", name, (System.nanoTime() - start) / 1_000_000, total);
    }

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        for (int round = 0; round < 2; round++) {            // second round = warmed up
            timed("fixed pool of " + cores, () -> Executors.newFixedThreadPool(cores));
            timed("virtual thread per task", Executors::newVirtualThreadPerTaskExecutor);
        }
    }
}
