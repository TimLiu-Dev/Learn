import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pipeline {
    record User(int id, String name) {}
    record Order(String item, int qty) {}

    static final long START = System.nanoTime();
    static void log(String msg) {
        System.out.printf("%4d ms [%s] %s%n", (System.nanoTime() - START) / 1_000_000,
                Thread.currentThread().getName(), msg);
    }
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    // three "remote calls" with fake latency
    static User fetchUser(int id) { sleep(200); log("got user"); return new User(id, "Ada"); }
    static List<Order> fetchOrders(User u) {
        sleep(300); log("got orders for " + u.name());
        return List.of(new Order("coffee", 2), new Order("bagel", 1));
    }
    static Map<String, Integer> fetchPrices() { sleep(250); log("got prices"); return Map.of("coffee", 350, "bagel", 275); }

    static int price(List<Order> orders, Map<String, Integer> prices) {
        return orders.stream().mapToInt(o -> o.qty() * prices.get(o.item())).sum();
    }

    public static void main(String[] args) {
        log("sequential: one call after another");
        int seq = price(fetchOrders(fetchUser(42)), fetchPrices());
        log("sequential total = " + seq + " cents");

        try (ExecutorService io = Executors.newFixedThreadPool(4)) {   // our own pool for blocking calls
            log("async: prices run while user → orders runs");
            CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> fetchUser(42), io);
            CompletableFuture<List<Order>> orders =
                    user.thenCompose(u -> CompletableFuture.supplyAsync(() -> fetchOrders(u), io));
            CompletableFuture<Map<String, Integer>> prices = CompletableFuture.supplyAsync(Pipeline::fetchPrices, io);
            CompletableFuture<Integer> total = orders.thenCombine(prices, Pipeline::price);
            log("pipeline built, nothing waited yet");
            log("async total = " + total.join() + " cents");
        }
    }
}
