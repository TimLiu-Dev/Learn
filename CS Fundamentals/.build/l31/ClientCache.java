import java.net.URI;
import java.net.http.*;
import java.util.*;

// A private HTTP cache in front of HttpClient. Start EtagServer first (port 18132).
public class ClientCache {
    record Entry(String body, String etag, long freshUntil) {}

    static final HttpClient http = HttpClient.newHttpClient();
    static final Map<String, Entry> cache = new LinkedHashMap<>(16, 0.75f, true) {   // L19's LRU
        protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) { return size() > 100; }
    };
    static int originCalls = 0, hits = 0, revalidated = 0;

    // Cache-Control: max-age=2  ->  fresh for 2 s;  no-cache  ->  stale at once;  no-store  ->  never stored
    static long freshUntil(HttpResponse<?> r, long now) {
        String cc = r.headers().firstValue("Cache-Control").orElse("");
        if (cc.contains("no-cache")) return now;
        for (String d : cc.split(",")) {
            d = d.trim();
            if (d.startsWith("max-age=")) return now + 1000L * Integer.parseInt(d.substring(8));
        }
        return now;
    }

    static String get(String path) throws Exception {
        long now = System.currentTimeMillis();
        Entry e = cache.get(path);
        if (e != null && now < e.freshUntil()) {          // fresh: no network at all
            hits++;
            return "HIT          " + e.body().strip();
        }
        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:18132" + path));
        if (e != null && e.etag() != null) req.header("If-None-Match", e.etag());   // stale: ask "changed?"
        HttpResponse<String> r = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
        originCalls++;
        if (r.statusCode() == 304 && e != null) {          // "not modified": reuse our copy
            revalidated++;
            cache.put(path, new Entry(e.body(), e.etag(), freshUntil(r, now)));
            return "REVALIDATED  " + e.body().strip();
        }
        String cc = r.headers().firstValue("Cache-Control").orElse("");
        if (cc.contains("no-store")) return "MISS         " + r.body().strip() + "   (no-store: not kept)";
        cache.put(path, new Entry(r.body(), r.headers().firstValue("ETag").orElse(null), freshUntil(r, now)));
        return "MISS         " + r.body().strip();
    }

    public static void main(String[] args) throws Exception {
        String[] script = {"/time", "/time", "/time", "sleep", "/time",
                           "/report", "/report", "/report", "/secret", "/secret"};
        for (String step : script) {
            if (step.equals("sleep")) { Thread.sleep(2100); System.out.println("... 2.1 s later"); continue; }
            System.out.printf("GET %-8s %s%n", step, get(step));
        }
        System.out.printf("origin calls: %d   served from cache with no call: %d   revalidated (304): %d%n",
                originCalls, hits, revalidated);
    }
}
