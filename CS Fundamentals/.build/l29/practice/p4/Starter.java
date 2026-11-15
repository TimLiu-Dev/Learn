import java.util.*;
import java.util.concurrent.*;

// Nearest Java equivalent: no coroutine to cancel, so the timeout and the
// cancelling are done on the futures themselves.
public class Practice {
    // A Java client: every call starts work and returns a future at once.
    interface PriceClient {
        CompletableFuture<Integer> price(String item);
    }

    // Ask for every price, then wait until all have arrived.
    static int total(PriceClient client, List<String> items) {
        throw new UnsupportedOperationException("TODO: start all the futures, then join each one");
    }

    // Like total, but null after timeoutMs, with the pending futures cancelled.
    static Integer totalOrNull(PriceClient client, List<String> items, long timeoutMs) throws Exception {
        throw new UnsupportedOperationException("TODO: wait with a timeout; on timeout, cancel and return null");
    }

    // CHECKS
    // Answers instantly, from a price list.
    record InstantClient(Map<String, Integer> prices) implements PriceClient {
        public CompletableFuture<Integer> price(String item) { return CompletableFuture.completedFuture(prices.get(item)); }
    }

    // Like a batch server: no price is answered until every item has been asked for.
    // A future still waiting after half a second completes with -1000.
    static class BatchClient implements PriceClient {
        final Map<String, Integer> prices;
        final Map<String, CompletableFuture<Integer>> pending = new LinkedHashMap<>();
        BatchClient(Map<String, Integer> prices) { this.prices = prices; }

        public CompletableFuture<Integer> price(String item) {
            CompletableFuture<Integer> f = new CompletableFuture<Integer>().completeOnTimeout(-1000, 500, TimeUnit.MILLISECONDS);
            pending.put(item, f);
            if (pending.size() == prices.size()) pending.forEach((i, fu) -> fu.complete(prices.get(i)));
            return f;
        }
    }

    // "durian" is slow: it answers 99 only after a second.
    static class SlowDurianClient implements PriceClient {
        final List<CompletableFuture<Integer>> futures = new ArrayList<>();

        public CompletableFuture<Integer> price(String item) {
            CompletableFuture<Integer> f = item.equals("durian")
                ? new CompletableFuture<Integer>().completeOnTimeout(99, 1, TimeUnit.SECONDS)
                : CompletableFuture.completedFuture(10);
            futures.add(f);
            return f;
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> prices = Map.of("apple", 3, "kiwi", 5, "fig", 7);
        List<String> three = List.of("apple", "kiwi", "fig");
        check("total adds up every price", 15, () -> total(new InstantClient(prices), three));
        check("total asks for every price before waiting for any", 15, () -> total(new BatchClient(prices), three));
        check("totalOrNull returns the total when it is in time", 15, () -> totalOrNull(new InstantClient(prices), three, 500));
        SlowDurianClient slow = new SlowDurianClient();
        check("totalOrNull gives up with null", null, () -> totalOrNull(slow, List.of("apple", "durian"), 100));
        check("giving up cancels the pending future", true, () -> slow.futures.get(1).isCancelled());
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
