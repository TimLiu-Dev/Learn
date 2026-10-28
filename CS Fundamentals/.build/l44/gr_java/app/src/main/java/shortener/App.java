package shortener;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "";
        switch (mode) {
            case "demo" -> demo(args.length > 1 ? Long.parseLong(args[1]) : 20);
            case "load" -> load();
            default -> System.out.println("usage: demo [seconds] | load");
        }
    }

    /** A real server on a fixed port, for the curl session. Stops itself. */
    static void demo(long seconds) throws Exception {
        Path db = Files.createTempFile("shortener", ".db");
        var server = new ShortenerServer(new LinkStore(db.toString()), Clock.systemUTC(), 10_000,
                                         new RateLimiter(3.0, 0.1), 18044, true);
        server.start();
        System.out.println("listening on http://127.0.0.1:" + server.port() + " for " + seconds + " s");
        Thread.sleep(seconds * 1000);
        server.stop();
        Files.deleteIfExists(db);
    }

    /** Zipf-like popularity: rank r is chosen with weight 1/r (a few links get most clicks). */
    static List<String> zipfCodes(List<String> codes, int n, long seed) {
        double[] cdf = new double[codes.size()];
        double total = 0;
        for (int i = 0; i < cdf.length; i++) { total += 1.0 / (i + 1); cdf[i] = total; }
        for (int i = 0; i < cdf.length; i++) cdf[i] /= total;
        var rnd = new Random(seed);
        var out = new ArrayList<String>(n);
        for (int k = 0; k < n; k++) {
            double u = rnd.nextDouble();
            int i = 0;
            while (cdf[i] < u) i++;
            out.add(codes.get(i));
        }
        return out;
    }

    static long[] run(ShortenerServer server, HttpClient client, List<String> requests) {
        long[] nanos = new long[requests.size()];
        var next = new AtomicInteger();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int c = 0; c < 32; c++) {                                // 32 concurrent clients
                pool.submit(() -> {
                    for (int i; (i = next.getAndIncrement()) < requests.size(); ) {
                        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + "/" + requests.get(i))).build();
                        long t0 = System.nanoTime();
                        var res = client.send(req, HttpResponse.BodyHandlers.discarding());
                        nanos[i] = System.nanoTime() - t0;
                        if (res.statusCode() != 302) throw new IllegalStateException("got " + res.statusCode());
                    }
                    return null;
                });
            }
        }
        return nanos;
    }

    static void load() throws Exception {
        System.out.printf("%-28s %8s %8s %8s %9s %11s%n", "config", "p50", "p95", "p99", "hit rate", "DB queries");
        long[][] configs = {{0, 0}, {0, 10_000}, {1, 0}, {1, 10_000}};
        for (long[] cfg : configs) {
            long latencyMs = cfg[0];
            int cacheSize = (int) cfg[1];
            Path db = Files.createTempFile("load", ".db");
            var store = new LinkStore(db.toString(), latencyMs);
            var codes = new ArrayList<String>();
            for (int i = 1; i <= 1000; i++) codes.add(store.create("https://example.com/page/" + i, null, 0, null).code());
            var server = new ShortenerServer(store, Clock.systemUTC(), cacheSize, new RateLimiter(10, 1), 0, false);
            server.start();
            var client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();

            run(server, client, zipfCodes(codes, 2_000, 7));             // warm-up: JIT, connections, cache
            server.cache.hits.set(0);
            server.cache.misses.set(0);
            store.queries.set(0);
            long[] sorted = run(server, client, zipfCodes(codes, 10_000, 44));
            Arrays.sort(sorted);
            long hits = server.cache.hits.get();
            double hitRate = (double) hits / (hits + server.cache.misses.get());
            String config = (cacheSize > 0 ? "LRU cache" : "no cache") + ", DB +" + latencyMs + "ms";
            System.out.printf("%-28s %8s %8s %8s %8.1f%% %11d%n", config, p(sorted, 0.50), p(sorted, 0.95),
                              p(sorted, 0.99), hitRate * 100, store.queries.get());
            server.stop();
            Files.deleteIfExists(db);
        }
    }

    static String p(long[] sorted, double q) {
        return "%.2fms".formatted(sorted[(int) (q * (sorted.length - 1))] / 1e6);
    }
}
