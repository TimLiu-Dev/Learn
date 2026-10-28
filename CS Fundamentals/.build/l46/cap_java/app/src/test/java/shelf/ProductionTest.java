package shelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import shelf.domain.Book;
import shelf.store.BookStore;

// Integration tests for the v2 additions: each starts a whole Shelf on port 0 with a temp database.
class ProductionTest {
    /** A clock the test moves by hand (L38), so rate-limit refills need no sleeping. */
    static final class MutableClock extends Clock {
        Instant now = Instant.parse("2026-10-27T10:00:00Z");
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    /** Counts reads that reach the real store, and can make them slow. Java has no `by`: forward by hand (L15). */
    static final class SpyBookStore implements BookStore {
        private final BookStore inner;
        private final long delayMs;
        final AtomicInteger finds = new AtomicInteger();
        SpyBookStore(BookStore inner, long delayMs) { this.inner = inner; this.delayMs = delayMs; }
        @Override public Optional<Book> find(long id) {
            finds.incrementAndGet();
            try { Thread.sleep(delayMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return inner.find(id);
        }
        @Override public Book add(String t, String a, Integer y) { return inner.add(t, a, y); }
        @Override public List<Book> list(int limit, int offset, String author) { return inner.list(limit, offset, author); }
        @Override public int count(String author) { return inner.count(author); }
        @Override public Optional<Book> update(long id, String t, String a, Integer y) { return inner.update(id, t, a, y); }
        @Override public boolean delete(long id) { return inner.delete(id); }
    }

    private final Path dbFile = Files.createTempFile("shelf-prod", ".db");
    private final MutableClock clock = new MutableClock();
    private final List<String> logs = new CopyOnWriteArrayList<>();
    private final HttpClient client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build();
    private SpyBookStore spy;
    private Shelf shelf;

    ProductionTest() throws Exception {}

    private void start(Config config, long delayMs) throws Exception {
        shelf = new Shelf(config.withPort(0).withDbFile(dbFile), clock, logs::add,
                          b -> spy = new SpyBookStore(b, delayMs)).start();
    }

    private void start() throws Exception {
        start(Config.defaults().withDrain(0), 0);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (shelf != null) shelf.close();
        Files.deleteIfExists(dbFile);
    }

    private HttpResponse<String> call(String method, String path, String body, String... headers) throws Exception {
        var b = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + shelf.port() + path))
            .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
            .header("Content-Type", "application/json");
        for (int i = 0; i < headers.length; i += 2) b.header(headers[i], headers[i + 1]);
        return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void twentySimultaneousLoansOfOneBookGiveOne201AndNineteen409sNeverA500() throws Exception {
        start();
        call("POST", "/books", "{\"title\":\"Dune\",\"author\":\"Frank Herbert\"}");
        for (int i = 0; i < 20; i++) call("POST", "/members", "{\"name\":\"M" + i + "\",\"email\":\"m" + i + "@example.com\"}");
        CountDownLatch gate = new CountDownLatch(1);
        Map<Integer, Integer> codes = new TreeMap<>();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> fs = new ArrayList<>();
            for (int m = 1; m <= 20; m++) {
                int member = m;
                fs.add(pool.submit(() -> { gate.await(); return call("POST", "/loans", "{\"bookId\":1,\"memberId\":" + member + "}").statusCode(); }));
            }
            gate.countDown();
            for (Future<Integer> f : fs) codes.merge(f.get(), 1, Integer::sum);
        }
        assertEquals(Map.of(201, 1, 409, 19), codes);
    }

    @Test
    void theCacheServesRepeatReadsAndAnUpdateInvalidatesIt() throws Exception {
        start();
        call("POST", "/books", "{\"title\":\"Dnue\",\"author\":\"Frank Herbert\"}");
        for (int i = 0; i < 5; i++) call("GET", "/books/1", null);
        assertEquals(1, spy.finds.get());                         // 5 reads, 1 trip to SQLite
        call("PUT", "/books/1", "{\"title\":\"Dune\",\"author\":\"Frank Herbert\"}");
        assertTrue(call("GET", "/books/1", null).body().contains("\"Dune\""));   // not the stale "Dnue"
        assertEquals(2, spy.finds.get());
    }

    @Test
    void aClientOverItsLimitGets429WithRetryAfterUntilTokensRefill() throws Exception {
        start(Config.defaults().withRate(3, 1).withDrain(0), 0);
        for (int i = 0; i < 3; i++) assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "ada").statusCode());
        var refused = call("GET", "/books/9", null, "X-Client-Id", "ada");
        assertEquals(429, refused.statusCode());
        assertEquals("1", refused.headers().firstValue("Retry-After").orElseThrow());
        assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "bob").statusCode());   // other clients unaffected
        assertEquals(200, call("GET", "/health", null, "X-Client-Id", "ada").statusCode());    // exempt
        clock.now = clock.now.plusSeconds(1);                      // one token back
        assertEquals(404, call("GET", "/books/9", null, "X-Client-Id", "ada").statusCode());
    }

    @Test
    void everyRequestGetsOneJsonLogLineThatJoinsTheCallersTraceAndMetricsCountIt() throws Exception {
        start();
        String trace = "4bf92f3577b34da6a3ce929d0e0e4736";
        var r = call("GET", "/books/7", null, "traceparent", "00-" + trace + "-00f067aa0ba902b7-01", "X-Request-Id", "req-1");
        assertEquals("req-1", r.headers().firstValue("X-Request-Id").orElseThrow());
        assertTrue(r.headers().firstValue("traceparent").orElseThrow().startsWith("00-" + trace + "-"));
        String line = logs.stream().filter(l -> l.contains("\"req-1\"")).findFirst().orElseThrow();
        assertTrue(line.contains("\"traceId\":\"" + trace + "\"") && line.contains("\"status\":404") && line.contains("\"path\":\"/books/7\""), line);
        assertTrue(call("GET", "/metrics", null).body().contains("shelf_requests_total{code=\"404\"} 1"));
    }

    @Test
    void shutdownLetsAnInFlightRequestFinishThenRefusesNewOnes() throws Exception {
        start(Config.defaults(), 500);                             // drainSeconds = 5
        call("POST", "/books", "{\"title\":\"Dune\",\"author\":\"Frank Herbert\"}");
        int port = shelf.port();
        CompletableFuture<HttpResponse<String>> slow = client.sendAsync(
            HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/books/1")).build(), HttpResponse.BodyHandlers.ofString());
        Thread.sleep(100);                                         // the request is now inside the slow store
        long t0 = System.nanoTime();
        shelf.close();
        long took = (System.nanoTime() - t0) / 1_000_000;
        shelf = null;
        assertEquals(200, slow.get().statusCode());                // it finished
        assertTrue(took < 2000, "close() took " + took + " ms");  // and close() didn't wait the full 5 s
        assertThrows(ConnectException.class, () -> client.send(
            HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/health")).build(), HttpResponse.BodyHandlers.ofString()));
    }
}
