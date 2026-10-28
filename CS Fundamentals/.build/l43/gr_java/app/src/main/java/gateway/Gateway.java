package gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.InstantSource;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static gateway.Telemetry.*;

/**
 * GET /api/price/{id}: rate limit → retry (GETs only) → circuit breaker → 300 ms timeout → pricing.
 * When pricing can't answer, serve the last good price we saw (marked stale).
 */
public final class Gateway {
    private final int pricingPort;
    private final InstantSource clock;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(300)).build();
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final Map<Integer, String> lastGood = new ConcurrentHashMap<>();
    private final Random jitter = new Random(43);
    public final CircuitBreaker breaker;

    // the four golden signals
    public final Counter requests = new Counter(), ok = new Counter(), stale = new Counter(), limited = new Counter(), errors = new Counter();
    public final Histogram latency = new Histogram(5, 10, 25, 50, 100, 250, 500, 1000, 2000);
    public final AtomicInteger inFlight = new AtomicInteger();
    public final HttpServer server;

    public Gateway(int pricingPort, InstantSource clock) throws IOException {
        this.pricingPort = pricingPort;
        this.clock = clock;
        this.breaker = new CircuitBreaker(5, Duration.ofSeconds(1), clock, (from, to) ->
                System.out.println(json("level", "WARN", "service", "gateway", "msg", "circuit " + from + " -> " + to, "t_ms", Tracer.nowMs())));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/api/price", this::handle);
        server.createContext("/metrics", ex -> send(ex, 200, metrics()));
    }

    private void handle(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("GET")) { ex.getResponseHeaders().add("Allow", "GET"); send(ex, 405, "only GET"); return; }
        long begin = Tracer.nowMs();
        requests.inc(); inFlight.incrementAndGet();
        try {
            String who = ex.getRequestHeaders().getFirst("X-Client");
            if (who == null) who = "anonymous";
            TokenBucket bucket = buckets.computeIfAbsent(who, k -> new TokenBucket(10.0, 10.0, clock));
            if (!bucket.tryAcquire()) {
                limited.inc();
                ex.getResponseHeaders().add("Retry-After", Long.toString(bucket.retryAfterSeconds()));
                send(ex, 429, "slow down"); return;
            }
            String path = ex.getRequestURI().getPath();
            int id;
            try { id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1)); } catch (NumberFormatException e) { id = 0; }
            final int bookId = id;
            String traceId = randomHex(16), rootSpan = randomHex(8);
            String result;
            try {
                String price = Retry.retry(3, 50, 400, jitter, e -> e instanceof IOException || e.getCause() instanceof IOException,
                        attempt -> breaker.call(() -> fetch(bookId, attempt, traceId, rootSpan)));   // GET is idempotent: safe to retry
                lastGood.put(id, price); ok.inc();
                send(ex, 200, "{\"id\":" + id + ",\"price\":" + price + "}"); result = "200";
            } catch (Exception e) {                                                  // breaker open or out of retries
                String cached = lastGood.get(id);
                if (cached != null) {
                    stale.inc(); ex.getResponseHeaders().add("X-Fallback", "true");
                    send(ex, 200, "{\"id\":" + id + ",\"price\":" + cached + ",\"stale\":true}"); result = "200 stale";
                } else {
                    errors.inc(); ex.getResponseHeaders().add("Retry-After", "1");
                    send(ex, 503, "pricing unavailable"); result = "503";
                }
            }
            Tracer.record(new Span("gateway", "GET " + ex.getRequestURI(), traceId, rootSpan, null, begin, Tracer.nowMs() - begin, result));
        } finally {
            latency.observe(Tracer.nowMs() - begin); inFlight.decrementAndGet();
        }
    }

    // Supplier can't throw checked exceptions, so IOException travels wrapped in UncheckedIOException
    private String fetch(int id, int attempt, String traceId, String parent) {
        String span = randomHex(8);
        long begin = Tracer.nowMs();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + pricingPort + "/price/" + id))
                .timeout(Duration.ofMillis(300))                                     // never wait forever
                .header("traceparent", traceparent(traceId, span)).build();
        String outcome = "ok";
        try {
            HttpResponse<String> r = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (r.statusCode() >= 500) { outcome = Integer.toString(r.statusCode()); throw new UncheckedIOException(new IOException("pricing " + r.statusCode())); }
            return r.body();
        } catch (HttpTimeoutException e) {
            outcome = "timeout"; throw new UncheckedIOException(e);
        } catch (IOException e) {
            outcome = "io"; throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); throw new IllegalStateException(e);
        } finally {
            Tracer.record(new Span("gateway", "attempt " + attempt, traceId, span, parent, begin, Tracer.nowMs() - begin, outcome));
        }
    }

    public String metrics() {
        return "# TYPE gateway_requests_total counter\ngateway_requests_total " + requests.value() + "\n"
                + "# TYPE gateway_responses_total counter\n"
                + "gateway_responses_total{result=\"ok\"} " + ok.value() + "\n"
                + "gateway_responses_total{result=\"stale\"} " + stale.value() + "\n"
                + "gateway_responses_total{result=\"limited\"} " + limited.value() + "\n"
                + "gateway_responses_total{result=\"error\"} " + errors.value() + "\n"
                + "# TYPE gateway_in_flight gauge\ngateway_in_flight " + inFlight.get() + "\n"
                + "# TYPE gateway_circuit_open gauge\ngateway_circuit_open " + (breaker.state() == CircuitBreaker.State.CLOSED ? 0 : 1) + "\n"
                + latency.render("gateway_request_duration_ms");
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(bytes); }
    }
}
