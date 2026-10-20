import java.util.concurrent.*;

// Nearest equivalent: virtual threads + CompletableFuture (L28)
public class Builders {
    static int price(String item) {
        sleep(300);                        // pretend network call
        return item.length() * 10;
    }

    public static void main(String[] args) throws Exception {
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> job = exec.submit(() -> {
                sleep(100);
                System.out.println("submit done");
            });
            job.get();

            long t0 = System.nanoTime();
            int total = price("apple") + price("kiwi");
            System.out.println("sequential total " + total);
            System.out.println("  took ~" + ms(t0) + " ms");

            t0 = System.nanoTime();
            CompletableFuture<Integer> a =
                CompletableFuture.supplyAsync(() -> price("apple"), exec);
            CompletableFuture<Integer> b =
                CompletableFuture.supplyAsync(() -> price("kiwi"), exec);
            System.out.println("concurrent total " + (a.join() + b.join()));
            System.out.println("  took ~" + ms(t0) + " ms");
        }
    }

    static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
