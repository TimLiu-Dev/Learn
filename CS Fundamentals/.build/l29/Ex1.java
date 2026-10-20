import java.util.List;
import java.util.concurrent.*;

// Nearest equivalent with CompletableFuture + virtual threads
public class Ex1 {
    static int price(String item) {
        sleep("durian".equals(item) ? 50 : 200);
        if ("durian".equals(item)) throw new IllegalStateException("no price for durian");
        System.out.println("  priced " + item);
        return item.length() * 10;
    }

    // (a) all-or-nothing: join() throws, but the other tasks are NOT cancelled
    static int totalPrice(List<String> items, ExecutorService exec) {
        var fs = items.stream().map(i -> CompletableFuture.supplyAsync(() -> price(i), exec)).toList();
        return fs.stream().mapToInt(CompletableFuture::join).sum();
    }

    // (b) best effort: turn each failure into 0
    static int totalPriceOrSkip(List<String> items, ExecutorService exec) {
        return items.stream()
            .map(i -> CompletableFuture.supplyAsync(() -> price(i), exec).exceptionally(e -> 0))
            .toList().stream().mapToInt(CompletableFuture::join).sum();
    }

    // (c) CPU work that can be cancelled: check the interrupt flag
    static int countPrimes(int limit) {
        int count = 0;
        for (int n = 2; n <= limit; n++) {
            if (Thread.currentThread().isInterrupted()) throw new CancellationException();
            boolean prime = true;
            for (int d = 2; d < n && prime; d++) prime = n % d != 0;
            if (prime) count++;
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        var cart = List.of("apple", "durian", "kiwi");
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            try { totalPrice(cart, exec); }
            catch (CompletionException e) { System.out.println("a: " + e.getCause().getMessage()); }
            System.out.println("b: " + totalPriceOrSkip(cart, exec));
            long t0 = System.nanoTime();
            Future<Integer> f = exec.submit(() -> countPrimes(1_000_000));
            Integer r = null;
            try { r = f.get(100, TimeUnit.MILLISECONDS); }
            catch (TimeoutException e) { f.cancel(true); }        // interrupt it
            System.out.println("c: " + r + " after " + (System.nanoTime() - t0) / 1_000_000 + " ms");
        }
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
