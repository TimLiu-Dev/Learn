package shortener;

import static org.junit.jupiter.api.Assertions.*;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShortenerTest {
    /** A clock the test can move forward (L38: inject time, don't read it). */
    static class TestClock extends Clock {
        Instant now = Instant.parse("2026-10-26T12:00:00Z");
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    Path db;
    TestClock clock = new TestClock();
    LinkStore store;
    ShortenerServer server;
    HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();

    @BeforeEach void start() throws Exception {
        db = Files.createTempFile("test", ".db");
        store = new LinkStore(db.toString());
        server = new ShortenerServer(store, clock, 10_000, new RateLimiter(3, 0.001), 0, false);
        server.start();
    }

    @AfterEach void stop() throws Exception {
        server.stop();
        Files.deleteIfExists(db);
    }

    HttpResponse<String> get(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + path)).build(),
                         HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String body, String client, String key) throws Exception {
        var b = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + "/api/links"))
            .header("X-Client-Id", client).POST(HttpRequest.BodyPublishers.ofString(body));
        if (key != null) b.header("Idempotency-Key", key);
        return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String body) throws Exception { return post(body, "c1", null); }

    String location(HttpResponse<String> res) { return res.headers().firstValue("Location").orElseThrow(); }

    @Test void base62RoundTrips() {
        for (long n : new long[]{0, 61, 62, 916_132_832L, Long.MAX_VALUE}) assertEquals(n, Base62.decode(Base62.encode(n)));
    }

    @Test void createReturns201AndTheCodeRedirectsWith302() throws Exception {
        var res = post("{\"url\":\"https://example.com/a\"}");
        assertEquals(201, res.statusCode());
        assertTrue(location(res).matches("/[0-9a-zA-Z]{7}"), location(res));
        var hop = get(location(res));
        assertEquals(302, hop.statusCode());
        assertEquals("https://example.com/a", location(hop));
    }

    @Test void customAliasWorksOnceThenConflicts() throws Exception {
        assertEquals(201, post("{\"url\":\"https://example.com/docs\",\"alias\":\"docs\"}").statusCode());
        var again = post("{\"url\":\"https://example.com/other\",\"alias\":\"docs\"}", "c2", null);
        assertEquals(409, again.statusCode());
        assertEquals("application/problem+json", again.headers().firstValue("Content-Type").orElseThrow());
    }

    @Test void badUrlsAreRejectedWithAProblem() throws Exception {
        var res = post("{\"url\":\"javascript:alert(1)\"}");
        assertEquals(400, res.statusCode());
        assertTrue(res.body().contains("http(s)"));
    }

    @Test void unknownCodesAre404AndTheMissIsCached() throws Exception {
        assertEquals(404, get("/nope123").statusCode());
        long before = store.queries.get();
        assertEquals(404, get("/nope123").statusCode());
        assertEquals(before, store.queries.get(), "second miss must not touch the database");
    }

    @Test void expiredLinksAre410Gone() throws Exception {
        String code = location(post("{\"url\":\"https://example.com/sale\",\"ttlSeconds\":60}"));
        assertEquals(302, get(code).statusCode());
        clock.now = clock.now.plusSeconds(61);
        assertEquals(410, get(code).statusCode());
    }

    @Test void fourthCreateInABurstIsRateLimited() throws Exception {
        for (int i = 0; i < 3; i++) assertEquals(201, post("{\"url\":\"https://example.com/" + i + "\"}", "spammer", null).statusCode());
        var res = post("{\"url\":\"https://example.com/4\"}", "spammer", null);
        assertEquals(429, res.statusCode());
        assertTrue(res.headers().firstValue("Retry-After").isPresent());
    }

    @Test void sameIdempotencyKeyReturnsTheSameLink() throws Exception {
        var a = post("{\"url\":\"https://example.com/once\"}", "c1", "k-1");
        var b = post("{\"url\":\"https://example.com/once\"}", "c1", "k-1");
        assertEquals(location(a), location(b));
    }

    @Test void clicksAreCountedAfterTheQueueFlushes() throws Exception {
        String code = location(post("{\"url\":\"https://example.com/c\"}"));
        for (int i = 0; i < 3; i++) get(code);
        server.clicks.flush();                                   // eventual consistency, made deterministic
        assertTrue(get("/api/links" + code + "/stats").body().contains("\"clicks\":3"));
    }

    @Test void aCachedRedirectDoesNotQueryTheDatabase() throws Exception {
        String code = location(post("{\"url\":\"https://example.com/hot\"}"));
        get(code);                                               // miss: loads into the cache
        long before = store.queries.get();
        for (int i = 0; i < 5; i++) assertEquals(302, get(code).statusCode());
        assertEquals(before, store.queries.get());
    }
}
