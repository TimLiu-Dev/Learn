import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

public class Retry {
    static class Shop {
        private final AtomicLong nextId = new AtomicLong(1);
        final Map<Long, String> orders = new ConcurrentHashMap<>();
        private final Map<String, Long> byKey = new ConcurrentHashMap<>();

        long post(String item) {                    // POST /orders: creates a NEW order every time
            long id = nextId.getAndIncrement();
            orders.put(id, item);
            return id;
        }
        long put(long id, String item) {            // PUT /orders/{id}: "make order id look like this"
            orders.put(id, item);
            return id;
        }
        long postWithKey(String key, String item) { // POST + Idempotency-Key header
            return byKey.computeIfAbsent(key, k -> post(item));   // same key -> same result
        }
    }

    // The server does the work, then the connection drops before the reply arrives. Once.
    static class FlakyNetwork {
        private int calls = 0;
        <T> T send(Supplier<T> request) {
            T reply = request.get();
            if (calls++ == 0) throw new UncheckedIOException(new IOException("connection reset before reply"));
            return reply;
        }
    }

    static <T> T withRetry(FlakyNetwork net, Supplier<T> request) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return net.send(request);
            } catch (UncheckedIOException e) {
                System.out.println("  attempt " + attempt + ": " + e.getCause().getMessage() + ", retrying");
            }
        }
        throw new IllegalStateException("gave up");
    }

    public static void main(String[] args) {
        Map<String, Function<Shop, Long>> scenarios = new LinkedHashMap<>();
        scenarios.put("POST", shop -> shop.post("Dune"));
        scenarios.put("PUT", shop -> shop.put(42, "Dune"));
        scenarios.put("POST + key", shop -> shop.postWithKey("c0ffee", "Dune"));
        for (var s : scenarios.entrySet()) {
            var shop = new Shop();
            var net = new FlakyNetwork();
            System.out.println(s.getKey() + ":");
            withRetry(net, () -> s.getValue().apply(shop));
            System.out.println("  orders on the server: " + shop.orders);
        }
    }
}
