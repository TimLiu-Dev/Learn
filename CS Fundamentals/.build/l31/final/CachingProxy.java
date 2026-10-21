import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Client -> caching proxy (port B) -> origin (port A). Everything stops when the script ends.
public class CachingProxy {
    record Entry(String body, String etag, String cacheControl, long freshUntil) {}

    static final Map<String, Entry> cache = new ConcurrentHashMap<>();
    static final Map<String, Integer> originHits = new ConcurrentHashMap<>();
    static final AtomicInteger origin304s = new AtomicInteger();
    static final HttpClient http = HttpClient.newHttpClient();

    static void reply(HttpExchange ex, int status, String body, Map<String, String> headers) throws IOException {
        headers.forEach((k, v) -> ex.getResponseHeaders().set(k, v));
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, status == 304 ? -1 : bytes.length);
        try (var out = ex.getResponseBody()) { if (status != 304) out.write(bytes); }
    }

    static long freshUntil(String cacheControl, long now) {
        if (cacheControl.contains("no-cache")) return now;                    // store, but always revalidate
        for (String d : cacheControl.split(",")) {
            d = d.trim();
            if (d.startsWith("max-age=")) return now + 1000L * Integer.parseInt(d.substring(8));
        }
        return now;
    }

    static HttpServer startOrigin() throws IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        s.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            originHits.merge(path, 1, Integer::sum);
            switch (path) {
                case "/time" -> reply(ex, 200, LocalTime.now().truncatedTo(ChronoUnit.MILLIS).toString(),
                        Map.of("Cache-Control", "max-age=2"));
                case "/report" -> {
                    String etag = "\"r-v1\"";
                    var h = Map.of("ETag", etag, "Cache-Control", "no-cache");
                    if (etag.equals(ex.getRequestHeaders().getFirst("If-None-Match"))) {
                        origin304s.incrementAndGet();
                        reply(ex, 304, "", h);
                    } else reply(ex, 200, "Q3 report: revenue up 4%", h);
                }
                case "/me" -> reply(ex, 200, "hello, Ada", Map.of("Cache-Control", "private, max-age=60"));
                default -> reply(ex, 404, "no such page", Map.of());
            }
        });
        s.start();
        return s;
    }

    static HttpServer startProxy(int originPort) throws IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        s.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath();
            long now = System.currentTimeMillis();
            Entry e = cache.get(path);
            if (e != null && now < e.freshUntil()) {                          // fresh: origin not contacted
                reply(ex, 200, e.body(), Map.of("X-Cache", "HIT", "Cache-Control", e.cacheControl()));
                return;
            }
            var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + originPort + path));
            if (e != null && e.etag() != null) req.header("If-None-Match", e.etag());
            HttpResponse<String> r;
            try { r = http.send(req.build(), HttpResponse.BodyHandlers.ofString()); }
            catch (InterruptedException ie) { throw new IOException(ie); }
            String cc = r.headers().firstValue("Cache-Control").orElse("");
            if (r.statusCode() == 304 && e != null) {                         // still valid: refresh and reuse
                cache.put(path, new Entry(e.body(), e.etag(), cc, freshUntil(cc, now)));
                reply(ex, 200, e.body(), Map.of("X-Cache", "REVALIDATED", "Cache-Control", cc));
                return;
            }
            // a shared cache must not keep no-store or private responses (RFC 9111 §5.2.2.5, §5.2.2.7)
            boolean storable = r.statusCode() == 200 && !cc.contains("no-store") && !cc.contains("private");
            if (storable) cache.put(path, new Entry(r.body(), r.headers().firstValue("ETag").orElse(null),
                    cc, freshUntil(cc, now)));
            reply(ex, r.statusCode(), r.body(), Map.of("X-Cache", storable ? "MISS" : "MISS (not stored)",
                    "Cache-Control", cc));
        });
        s.start();
        return s;
    }

    public static void main(String[] args) throws Exception {
        HttpServer origin = startOrigin();
        HttpServer proxy = startProxy(origin.getAddress().getPort());
        String base = "http://127.0.0.1:" + proxy.getAddress().getPort();
        String[] script = {"/time", "/time", "/time", "sleep", "/time",
                           "/report", "/report", "/report", "/me", "/me", "/nope"};
        try {
            for (String step : script) {
                if (step.equals("sleep")) { Thread.sleep(2500); System.out.println("... 2.5 s later"); continue; }
                HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base + step)).build(),
                        HttpResponse.BodyHandlers.ofString());
                System.out.printf("GET %-8s %d  %-18s %s%n", step, r.statusCode(),
                        r.headers().firstValue("X-Cache").orElse("?"), r.body());
            }
            System.out.println("origin saw " + new TreeMap<>(originHits) + ", of which " + origin304s
                    + " were 304 Not Modified");
        } finally {
            proxy.stop(0);
            origin.stop(0);
        }
    }
}
