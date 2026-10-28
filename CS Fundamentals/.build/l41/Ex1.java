import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Exercise 1: a service where 5% of calls take 50 ms. Measure its percentiles,
// then add hedging: if no answer after 5 ms, send a second copy and take whichever wins.
public class Ex1 {
    static final AtomicInteger calls = new AtomicInteger();

    static String call() {
        calls.incrementAndGet();
        boolean slow = ThreadLocalRandom.current().nextInt(100) < 5;
        try { Thread.sleep(slow ? 50 : 1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return "ok";
    }

    static String hedged(ExecutorService pool) throws Exception {
        var first = CompletableFuture.supplyAsync(Ex1::call, pool);
        try {
            return first.get(5, TimeUnit.MILLISECONDS);
        } catch (TimeoutException slowSoFar) {
            var second = CompletableFuture.supplyAsync(Ex1::call, pool);
            return (String) CompletableFuture.anyOf(first, second).join();   // first answer wins
        }
    }

    public static void main(String[] args) throws Exception {
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            measure("plain", () -> pool.submit(Ex1::call).get());
            measure("hedged", () -> hedged(pool));
        }
    }

    static void measure(String name, Callable<String> request) throws Exception {
        calls.set(0);
        double[] ms = new double[1_000];
        for (int i = 0; i < ms.length; i++) {
            long t = System.nanoTime();
            request.call();
            ms[i] = (System.nanoTime() - t) / 1e6;
        }
        Arrays.sort(ms);
        System.out.printf("%-7s p50 %5.1f ms  p95 %5.1f ms  p99 %5.1f ms  (%d calls for 1000 requests)%n",
                name, ms[499], ms[949], ms[989], calls.get());
    }
}
