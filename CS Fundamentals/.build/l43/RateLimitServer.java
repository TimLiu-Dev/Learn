import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class RateLimitServer {
    public static void main(String[] args) throws Exception {
        Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();             // one bucket per client (L27)
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/books", ex -> {
            String who = ex.getRequestHeaders().getFirst("X-Client");
            if (who == null) who = ex.getRemoteAddress().getAddress().getHostAddress();
            TokenBucket bucket = buckets.computeIfAbsent(who, k -> new TokenBucket(5.0, 5.0, Clock.systemUTC()));
            if (bucket.tryAcquire()) {
                ex.sendResponseHeaders(200, -1);
            } else {
                ex.getResponseHeaders().add("Retry-After", Long.toString(bucket.retryAfterSeconds()));
                ex.sendResponseHeaders(429, -1);                                 // 429 Too Many Requests (RFC 6585)
            }
            ex.close();
        });
        server.start();
        URI url = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/books");
        HttpClient client = HttpClient.newHttpClient();

        Map<String, AtomicIntegerArray> ok = Map.of("alice", new AtomicIntegerArray(3), "bob", new AtomicIntegerArray(3));
        Map<String, AtomicIntegerArray> limited = Map.of("alice", new AtomicIntegerArray(3), "bob", new AtomicIntegerArray(3));
        long start = System.nanoTime();
        Thread alice = hammer(client, url, "alice", 20, start, ok, limited);     // ~50 requests a second
        Thread bob = hammer(client, url, "bob", 400, start, ok, limited);        // ~2.5 requests a second
        alice.join(); bob.join();
        System.out.println("second  alice 200/429   bob 200/429");
        for (int s = 0; s <= 2; s++)
            System.out.printf("%5d   %5d / %-5d   %3d / %d%n", s, ok.get("alice").get(s), limited.get("alice").get(s),
                    ok.get("bob").get(s), limited.get("bob").get(s));
        server.stop(0);
    }

    static Thread hammer(HttpClient client, URI url, String who, long pauseMs, long start,
                         Map<String, AtomicIntegerArray> ok, Map<String, AtomicIntegerArray> limited) {
        return Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    int second = (int) ((System.nanoTime() - start) / 1_000_000_000);
                    if (second >= 3) break;
                    var r = client.send(HttpRequest.newBuilder(url).header("X-Client", who).build(), HttpResponse.BodyHandlers.discarding());
                    (r.statusCode() == 200 ? ok : limited).get(who).incrementAndGet(second);
                    if (who.equals("alice") && r.statusCode() == 429 && second == 0 && limited.get(who).get(0) == 1)
                        System.out.println("first 429 for alice: Retry-After: " + r.headers().firstValue("Retry-After").orElse("?"));
                    Thread.sleep(pauseMs);
                }
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}

class TokenBucket {
    private final double capacity, perSecond;
    private final Clock clock;
    private double tokens;
    private long last;
    TokenBucket(double capacity, double perSecond, Clock clock) {
        this.capacity = capacity; this.perSecond = perSecond; this.clock = clock; tokens = capacity; last = clock.millis();
    }
    synchronized boolean tryAcquire() { refill(); if (tokens < 1) return false; tokens -= 1; return true; }
    synchronized long retryAfterSeconds() { refill(); return Math.max(1, (long) Math.ceil((1 - tokens) / perSecond)); }
    private void refill() { long now = clock.millis(); tokens = Math.min(capacity, tokens + (now - last) / 1000.0 * perSecond); last = now; }
}
