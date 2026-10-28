package shelf.load;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import shelf.Config;
import shelf.Shelf;
import shelf.store.Db;
import shelf.store.SqliteBookStore;

public final class LoadTest {
    static final int BOOKS = 200;
    static final long DB_DELAY_MS = Long.parseLong(System.getenv().getOrDefault("LOAD_DB_DELAY_MS", "2"));   // Exercise 2: try 20

    record Hit(String client, long startMs, double ms, int status) {}

    record Result(double p50, double p95, double p99, int rps, int dbReads, String hit, int r429, int r5xx, String series) {}

    public static void main(String[] args) throws Exception {
        Config before = Config.defaults().withPool(0).withCache(0).withRate(0, 0).withDrain(0);   // features off
        Config after = Config.defaults().withDrain(0);                                               // the defaults
        Result b = run(before), a = run(after);
        System.out.println("16 paced clients x 200 requests (85% GET /books/{id}, 10% lists, 5% PUT) + 1 greedy client x 1000 GETs");
        System.out.println("Every database read costs an extra " + DB_DELAY_MS + " ms. Latency percentiles are for the 16 paced clients.");
        System.out.printf("%-38s %6s %6s %6s %7s %8s %6s %6s %5s%n", "", "p50", "p95", "p99", "req/s", "DB reads", "hit%", "429s", "5xx");
        row("before (no pool, no cache, no limit)", b);
        row("after (pool 8, cache, limit 50/s)", a);
        System.out.println("(ms; on my machine, on localhost)");
        System.out.println("SERIES before " + b.series());
        System.out.println("SERIES after " + a.series());
    }

    static void row(String name, Result r) {
        System.out.printf("%-38s %6.1f %6.1f %6.1f %7d %8d %6s %6d %5d%n", name, r.p50(), r.p95(), r.p99(), r.rps(), r.dbReads(), r.hit(), r.r429(), r.r5xx());
    }

    static Result run(Config config) throws Exception {
        Path file = Files.createTempFile("shelf-load", ".db");
        try (Db db = new Db(file)) {
            db.createSchema();
            var s = new SqliteBookStore(db);
            for (int i = 0; i < BOOKS; i++) s.add("Book " + (i + 1), "Author " + (i % 20), 1950 + i % 70);
        }
        SlowBookStore[] slow = new SlowBookStore[1];
        Shelf shelf = new Shelf(config.withPort(0).withDbFile(file), Clock.systemUTC(), line -> {},
                                b -> slow[0] = new SlowBookStore(b, DB_DELAY_MS)).start();
        String base = "http://127.0.0.1:" + shelf.port();
        HttpClient client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build();
        ConcurrentLinkedQueue<Hit> hits = new ConcurrentLinkedQueue<>();
        long t0 = System.nanoTime();

        interface Send { void go(String who, String method, String path, String body) throws Exception; }
        Send send = (who, method, path, body) -> {
            long start = System.nanoTime();
            var req = HttpRequest.newBuilder(URI.create(base + path)).header("X-Client-Id", who).header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build();
            int status = client.send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
            hits.add(new Hit(who, (start - t0) / 1_000_000, (System.nanoTime() - start) / 1e6, status));
        };

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int c = 0; c < 16; c++) {
                int me = c;
                pool.submit(() -> {
                    Random rnd = new Random(me);                       // fixed seed: the same requests every run
                    for (int i = 0; i < 200; i++) {
                        double roll = rnd.nextDouble();
                        String who = "client-" + me;
                        if (roll < 0.85) send.go(who, "GET", "/books/" + zipf(rnd), null);
                        else if (roll < 0.95) send.go(who, "GET", "/books?limit=10&offset=" + rnd.nextInt(20) * 10, null);
                        else {
                            long id = zipf(rnd);
                            send.go(who, "PUT", "/books/" + id, "{\"title\":\"Book " + id + "\",\"author\":\"Author " + (id % 20) + "\"}");
                        }
                        Thread.sleep(20);                              // think time: ~40 requests/s per client
                    }
                    return null;
                });
            }
            pool.submit(() -> { Random rnd = new Random(99); for (int i = 0; i < 1000; i++) send.go("greedy", "GET", "/books/" + zipf(rnd), null); return null; });
        }
        double seconds = (System.nanoTime() - t0) / 1e9;
        String metrics = client.send(HttpRequest.newBuilder(URI.create(base + "/metrics")).build(), HttpResponse.BodyHandlers.ofString()).body();
        Matcher m = Pattern.compile("shelf_cache_hit_ratio (\\S+)").matcher(metrics);
        String hit = m.find() ? String.format("%.0f", Double.parseDouble(m.group(1)) * 100) : "-";   // the cache's own count
        shelf.close();
        Files.deleteIfExists(file);

        List<Hit> all = new ArrayList<>(hits);
        List<Double> paced = all.stream().filter(h -> !h.client().equals("greedy")).map(Hit::ms).sorted().toList();
        Map<Long, List<Hit>> buckets = new TreeMap<>();
        all.forEach(h -> buckets.computeIfAbsent(h.startMs() / 500, k -> new ArrayList<>()).add(h));
        StringBuilder series = new StringBuilder("[");
        buckets.forEach((bucket, hs) -> {
            List<Double> p = hs.stream().filter(h -> !h.client().equals("greedy")).map(Hit::ms).sorted().toList();
            if (series.length() > 1) series.append(',');
            series.append(String.format("{\"t\":%s,\"n\":%d,\"p95\":%.1f,\"r429\":%d,\"err\":%d}", bucket * 0.5, hs.size(),
                p.isEmpty() ? 0.0 : p.get((int) ((p.size() - 1) * 0.95)),
                hs.stream().filter(h -> h.status() == 429).count(), hs.stream().filter(h -> h.status() >= 500).count()));
        });
        series.append(']');
        return new Result(pct(paced, 0.50), pct(paced, 0.95), pct(paced, 0.99), (int) (all.size() / seconds), slow[0].reads.get(),
                          hit, (int) all.stream().filter(h -> h.status() == 429).count(), (int) all.stream().filter(h -> h.status() >= 500).count(),
                          series.toString());
    }

    static double pct(List<Double> sorted, double p) {
        return sorted.get((int) ((sorted.size() - 1) * p));
    }

    /** Popular books get most of the traffic (Zipf-like, as in L41): book 1 most of all. */
    static long zipf(Random rnd) {
        return (long) (BOOKS * Math.pow(rnd.nextDouble(), 3)) + 1;
    }
}
