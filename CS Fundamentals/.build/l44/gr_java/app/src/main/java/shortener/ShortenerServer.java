package shortener;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import shortener.LinkStore.AliasTakenException;
import shortener.LinkStore.Link;
import shortener.Model.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class ShortenerServer {
    private final JsonMapper json = JsonMapper.builder().build();
    private final LinkStore store;
    private final Clock clock;
    private final RateLimiter limiter;
    private final boolean verbose;
    public final LruCache<String, Lookup> cache;
    public final ClickRecorder clicks;
    private final ConcurrentHashMap<String, LinkView> idempotent = new ConcurrentHashMap<>();   // per process
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();         // L28
    private final HttpServer server;

    public ShortenerServer(LinkStore store, Clock clock, int cacheCapacity, RateLimiter limiter,
                           int port, boolean verbose) throws IOException {
        this.store = store;
        this.clock = clock;
        this.limiter = limiter;
        this.verbose = verbose;
        this.cache = new LruCache<>(cacheCapacity);
        this.clicks = new ClickRecorder(store, clock);
        this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/", ex -> { try (ex) { handle(ex); } });
        server.setExecutor(pool);
    }

    public int port() { return server.getAddress().getPort(); }
    public void start() { server.start(); }
    public void stop() throws InterruptedException {
        server.stop(0);
        pool.close();
        clicks.stop();
    }

    private void handle(HttpExchange ex) throws IOException {
        try {
            List<String> parts = List.of(ex.getRequestURI().getPath().replaceAll("^/+|/+$", "").split("/"));
            String method = ex.getRequestMethod();
            if (parts.equals(List.of("api", "links"))) {
                if (method.equals("POST")) create(ex); else notAllowed(ex, "POST");
            } else if (parts.size() == 4 && parts.get(0).equals("api") && parts.get(1).equals("links")
                       && parts.get(3).equals("stats")) {
                if (method.equals("GET")) stats(ex, parts.get(2)); else notAllowed(ex, "GET");
            } else if (parts.size() == 1 && !parts.get(0).isEmpty() && !parts.get(0).equals("api")) {
                if (method.equals("GET")) redirect(ex, parts.get(0)); else notAllowed(ex, "GET");
            } else {
                problem(ex, 404, "Not Found", "No resource at " + ex.getRequestURI().getPath());
            }
        } catch (JacksonException e) {
            problem(ex, 400, "Bad Request", "Malformed JSON body");
        } catch (Exception e) {
            System.err.println("500 on " + ex.getRequestURI() + ": " + e);   // details to the log, not the client (L33)
            problem(ex, 500, "Internal Server Error", "Something went wrong");
        }
    }

    private void create(HttpExchange ex) throws IOException, SQLException {
        String client = ex.getRequestHeaders().getFirst("X-Client-Id");
        if (client == null) client = ex.getRemoteAddress().getAddress().getHostAddress();
        long wait = limiter.tryAcquire(client);
        if (wait > 0) {
            ex.getResponseHeaders().set("Retry-After", Long.toString(wait));     // RFC 9110 §10.2.3
            problem(ex, 429, "Too Many Requests", "Slow down: try again in " + wait + " s");
            return;
        }
        NewLink input = json.readValue(ex.getRequestBody().readAllBytes(), NewLink.class);
        String invalid = validate(input);
        if (invalid != null) {
            problem(ex, 400, "Invalid link", invalid);
            return;
        }
        String key = ex.getRequestHeaders().getFirst("Idempotency-Key");
        LinkView view;
        try {
            view = key == null ? newLink(input) : idempotent.computeIfAbsent(client + ":" + key, k -> newLinkUnchecked(input));
        } catch (AliasTakenException e) {
            problem(ex, 409, "Conflict", e.getMessage());
            return;
        }
        ex.getResponseHeaders().set("Location", "/" + view.code());
        send(ex, 201, json.writeValueAsString(view), "application/json");
    }

    private static String validate(NewLink input) {
        if (input.url() == null) return "url is required";
        URI uri;
        try { uri = new URI(input.url()); } catch (Exception e) { uri = null; }
        if (input.url().length() > 2048 || uri == null || !Set.of("http", "https").contains(String.valueOf(uri.getScheme()))
            || uri.getHost() == null)
            return "url must be an absolute http(s) URL of at most 2048 characters";
        if (input.alias() != null && !input.alias().matches("[A-Za-z0-9-]{4,30}"))
            return "alias must be 4-30 letters, digits or '-'";
        if (input.ttlSeconds() != null && input.ttlSeconds() < 1) return "ttlSeconds must be at least 1";
        return null;
    }

    private LinkView newLink(NewLink input) throws SQLException {
        long now = clock.millis();
        Long expiresAt = input.ttlSeconds() == null ? null : now + input.ttlSeconds() * 1000;
        Link link = store.create(input.url(), input.alias(), now, expiresAt);
        return new LinkView(link.code(), link.url(), "http://127.0.0.1:" + port() + "/" + link.code(), link.expiresAt());
    }

    private LinkView newLinkUnchecked(NewLink input) {          // computeIfAbsent can't throw checked exceptions
        try { return newLink(input); } catch (SQLException e) { throw new IllegalStateException(e); }
    }

    /** The hot path: cache, then database, then remember the answer (cache-aside, L41). */
    private Lookup lookup(String code) throws SQLException {
        Lookup cached = cache.get(code);
        if (cached != null) return cached;
        Link link = store.find(code);
        Lookup fresh = link == null ? Missing.INSTANCE : new Found(link);
        cache.put(code, fresh);
        return fresh;
    }

    private void redirect(HttpExchange ex, String code) throws IOException, SQLException {
        switch (lookup(code)) {
            case Missing m -> problem(ex, 404, "Not Found", "No link '" + code + "'");
            case Found(Link link) -> {
                if (link.expiresAt() != null && link.expiresAt() <= clock.millis()) {
                    problem(ex, 410, "Gone", "Link '" + code + "' has expired");
                    return;
                }
                clicks.record(code);                                     // async: the redirect doesn't wait
                ex.getResponseHeaders().set("Location", link.url());
                send(ex, 302, null, null);
            }
        }
    }

    private void stats(HttpExchange ex, String code) throws IOException, SQLException {
        if (!(lookup(code) instanceof Found(Link link))) {
            problem(ex, 404, "Not Found", "No link '" + code + "'");
            return;
        }
        send(ex, 200, json.writeValueAsString(new Stats(code, link.url(), store.clicks(code))), "application/json");
    }

    private void notAllowed(HttpExchange ex, String allow) throws IOException {
        ex.getResponseHeaders().set("Allow", allow);
        problem(ex, 405, "Method Not Allowed", ex.getRequestMethod() + " is not supported here");
    }

    private void problem(HttpExchange ex, int status, String title, String detail) throws IOException {
        send(ex, status, json.writeValueAsString(new Problem("about:blank", title, status, detail)), "application/problem+json");
    }

    private void send(HttpExchange ex, int status, String body, String type) throws IOException {
        if (verbose) System.out.println(ex.getRequestMethod() + " " + ex.getRequestURI() + " -> " + status);
        if (body == null) {
            ex.sendResponseHeaders(status, -1);
            return;
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }
}
