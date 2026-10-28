package cache;

import cache.Catalog.CachedCatalog;
import cache.Catalog.SqliteCatalog;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("bench")) bench(); else finalDemo();
    }

    // ── Final: the same SQLite catalog served with and without the cache ─────────
    static void finalDemo() throws Exception {
        var file = Files.createTempFile("l41-final", ".db");
        new SqliteCatalog(file, 0).seed(200);

        var plainDb = new SqliteCatalog(file, 20);
        var plainServer = Server.start(plainDb, () -> "{\"dbQueries\":" + plainDb.queries.get() + "}");

        var cachedDb = new SqliteCatalog(file, 20);
        var cached = new CachedCatalog(cachedDb, new LruCache<>(50, Duration.ofSeconds(60)));
        var cachedServer = Server.start(cached, () -> {
            Stats s = cached.stats();
            return "{\"hits\":" + s.hits() + ",\"misses\":" + s.misses() + ",\"evictions\":" + s.evictions()
                    + ",\"coalesced\":" + cached.coalesced.get() + ",\"dbQueries\":" + cachedDb.queries.get() + "}";
        });
        var client = HttpClient.newHttpClient();
        String base = "http://127.0.0.1:" + cachedServer.getAddress().getPort();

        Callers call = (method, path, body) -> {
            var req = HttpRequest.newBuilder(URI.create(base + path))
                    .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                    .build();
            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return (method + " " + path + " -> " + res.statusCode() + " " + res.body()).trim();
        };

        System.out.println(call.call("GET", "/books/1", null));
        System.out.println(call.call("GET", "/books/1", null));
        System.out.println(call.call("PUT", "/books/1", "Dune"));
        System.out.println(call.call("GET", "/books/1", null));
        System.out.println(call.call("GET", "/books/999", null));
        System.out.println(call.call("GET", "/stats", null));

        // 50 concurrent requests for a cold key: coalescing sends one query to the database
        long before = cachedDb.queries.get();
        var gate = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 50; i++) pool.submit(() -> { gate.await(); return call.call("GET", "/books/7", null); });
            gate.countDown();
        }
        System.out.println("50 concurrent GET /books/7 -> " + (cachedDb.queries.get() - before)
                + " database query, coalesced " + cached.coalesced.get());

        System.out.println();
        loadTest("without cache", plainServer.getAddress().getPort(), client);
        System.out.println("  " + plainDb.queries.get() + " database queries");
        long q0 = cachedDb.queries.get();
        Stats s0 = cached.stats();
        loadTest("with cache", cachedServer.getAddress().getPort(), client);
        Stats s1 = cached.stats();
        long hits = s1.hits() - s0.hits(), misses = s1.misses() - s0.misses();
        System.out.printf("  %d database queries, hit rate %.1f%%, %d evictions%n",
                cachedDb.queries.get() - q0, 100.0 * hits / (hits + misses), s1.evictions() - s0.evictions());

        plainServer.stop(0);
        cachedServer.stop(0);
        Files.delete(file);
    }

    interface Callers { String call(String method, String path, String body) throws Exception; }

    /** 16 clients x 125 requests, ids drawn from a Zipf-like distribution over 200 books. */
    static void loadTest(String name, int port, HttpClient client) throws Exception {
        double[] cdf = new double[200];
        double total = 0;
        for (int r = 1; r <= 200; r++) { total += 1.0 / r; cdf[r - 1] = total; }
        final double sum = total;
        var latencies = Collections.synchronizedList(new ArrayList<Double>());
        long t = System.nanoTime();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int c = 0; c < 16; c++) {
                final long seed = 41 + c;
                pool.submit(() -> {
                    var rnd = new Random(seed);
                    for (int i = 0; i < 125; i++) {
                        int idx = Arrays.binarySearch(cdf, rnd.nextDouble() * sum);
                        int id = (idx >= 0 ? idx : -idx - 1) + 1;
                        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/books/" + id)).build();
                        long start = System.nanoTime();
                        client.send(req, HttpResponse.BodyHandlers.discarding());
                        latencies.add((System.nanoTime() - start) / 1e6);
                    }
                    return null;
                });
            }
        }
        long wall = (System.nanoTime() - t) / 1_000_000;
        var sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        System.out.printf("%-13s %d requests in %d ms: p50 %.1f ms, p95 %.1f ms, p99 %.1f ms%n",
                name, sorted.size(), wall, pct(sorted, 50), pct(sorted, 95), pct(sorted, 99));
    }

    static double pct(List<Double> sorted, double p) {
        return sorted.get(Math.max(0, (int) Math.ceil(p / 100 * sorted.size()) - 1));
    }

    // ── Part 3: my LruCache vs a synchronized LinkedHashMap under contention ─────
    static void bench() throws Exception {
        int cap = 1_000;
        var mine = new LruCache<Integer, Integer>(cap, Duration.ofHours(1));
        Map<Integer, Integer> jdk = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) { return size() > cap; }
        });
        var impls = new LinkedHashMap<String, BiConsumer<Integer, Boolean>>();
        impls.put("LruCache (ReentrantLock)", (k, read) -> { if (read) mine.get(k); else mine.put(k, k); });
        impls.put("synchronized LinkedHashMap", (k, read) -> { if (read) jdk.get(k); else jdk.put(k, k); });
        for (int threads : new int[] {1, 8}) {
            for (var e : impls.entrySet()) {
                long best = Long.MAX_VALUE;
                for (int round = 0; round < 4; round++) best = Math.min(best, run(threads, e.getValue()));   // first runs warm up the JIT
                System.out.printf("%-27s %d thread%s: %4d ms for %,d ops%n", e.getKey(), threads, threads == 1 ? " " : "s", best, threads * 400_000);
            }
        }
        System.out.println("LruCache: size " + mine.size() + ", " + mine.stats());
    }

    static long run(int threads, BiConsumer<Integer, Boolean> op) throws Exception {
        var gate = new CountDownLatch(1);
        var done = new CountDownLatch(threads);
        var pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final long seed = 41 + t;
            pool.submit(() -> {
                var rnd = new Random(seed);
                gate.await();
                for (int i = 0; i < 400_000; i++) op.accept(rnd.nextInt(2_000), rnd.nextInt(10) < 8);   // 80% reads, 20% writes
                done.countDown();
                return null;
            });
        }
        long start = System.nanoTime();
        gate.countDown();
        done.await();
        pool.shutdown();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
