import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// A hot key expires and 50 requests arrive at once. Plain cache-aside sends all
// 50 to the database; request coalescing sends one and lets the other 49 wait for it.
public class Stampede {
    static final AtomicInteger dbQueries = new AtomicInteger();

    static String slowDbFind(int id) {               // the database: 50 ms per query
        dbQueries.incrementAndGet();
        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return "Dune";
    }

    // Plain cache-aside: check, miss, load, put
    static final Map<Integer, String> cache = new ConcurrentHashMap<>();
    static String getPlain(int id) {
        String v = cache.get(id);
        if (v == null) { v = slowDbFind(id); cache.put(id, v); }
        return v;
    }

    // Coalescing: the first miss creates a future; everyone else waits on the same one
    static final Map<Integer, CompletableFuture<String>> inFlight = new ConcurrentHashMap<>();
    static String getCoalesced(int id) {
        String v = cache.get(id);
        if (v != null) return v;
        var mine = new CompletableFuture<String>();
        var running = inFlight.putIfAbsent(id, mine);  // atomic: exactly one caller wins
        if (running != null) return running.join();    // someone is already loading: wait
        try {
            String loaded = slowDbFind(id);
            cache.put(id, loaded);
            mine.complete(loaded);
            return loaded;
        } catch (RuntimeException e) {
            mine.completeExceptionally(e);              // waiters see the failure too
            throw e;
        } finally {
            inFlight.remove(id, mine);
        }
    }

    public static void main(String[] args) throws Exception {
        burst("plain", () -> getPlain(1));
        cache.clear();                                  // the hot key expires again
        burst("coalesced", () -> getCoalesced(1));
    }

    static void burst(String name, Callable<String> request) throws Exception {
        dbQueries.set(0);
        var gate = new CountDownLatch(1);
        long t;
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            var results = new ArrayList<Future<String>>();
            for (int i = 0; i < 50; i++) results.add(pool.submit(() -> { gate.await(); return request.call(); }));
            t = System.nanoTime();
            gate.countDown();                           // release all 50 at once
            for (var f : results) f.get();
        }
        System.out.printf("%-10s 50 requests -> %2d database queries, %d ms%n",
                name, dbQueries.get(), (System.nanoTime() - t) / 1_000_000);
    }
}
