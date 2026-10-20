import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Ex2 {
    record Quote(String shop, Integer cents, String note) {}
    record Shop(String name, long ms, int cents) {}

    public static void main(String[] args) {
        List<Shop> shops = List.of(new Shop("alpha", 120, 1999), new Shop("beta", 300, 1899), new Shop("gamma", 80, -1));
        long start = System.nanoTime();
        try (ExecutorService io = Executors.newFixedThreadPool(3)) {
            List<CompletableFuture<Quote>> futures = new ArrayList<>();
            for (Shop s : shops) {
                futures.add(CompletableFuture.supplyAsync(() -> {
                        try { Thread.sleep(s.ms()); } catch (InterruptedException e) { throw new RuntimeException(e); }
                        if (s.cents() < 0) throw new IllegalStateException(s.name() + " is down");
                        return new Quote(s.name(), s.cents(), "ok");
                    }, io)
                    .completeOnTimeout(new Quote(s.name(), null, "too slow"), 200, TimeUnit.MILLISECONDS)
                    .exceptionally(e -> new Quote(s.name(), null, "failed: " + e.getCause().getMessage())));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            List<Quote> quotes = futures.stream().map(CompletableFuture::join).toList();
            quotes.forEach(System.out::println);
            Quote best = quotes.stream().filter(q -> q.cents() != null)
                    .min(Comparator.comparing(Quote::cents)).orElse(null);
            System.out.println("best: " + best.shop() + " at " + best.cents() + " cents, after "
                    + (System.nanoTime() - start) / 1_000_000 + " ms");
        }
    }
}
