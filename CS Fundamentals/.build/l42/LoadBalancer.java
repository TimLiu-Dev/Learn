import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer implements AutoCloseable {
    public static final class Target {
        final String name;
        final URI uri;
        volatile boolean healthy = true;
        final AtomicInteger active = new AtomicInteger();   // requests in flight right now
        final AtomicInteger served = new AtomicInteger();   // requests answered so far
        Target(String name, URI uri) { this.name = name; this.uri = uri; }
        @Override public String toString() { return name + "=" + served; }
    }

    public enum Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

    // Headers that describe one connection, not the request: each hop sets its own.
    static final Set<String> HOP_HEADERS =
        Set.of("connection", "content-length", "host", "transfer-encoding", "upgrade", "expect", "date");

    private final List<Target> targets;
    private final Strategy strategy;
    private final boolean retryOnFailure;
    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofMillis(300)).build();
    private final AtomicInteger next = new AtomicInteger();
    private final long started = System.nanoTime();
    private volatile boolean running = true;
    private final HttpServer server;

    public LoadBalancer(List<Target> targets, Strategy strategy, boolean retryOnFailure) throws IOException {
        this.targets = targets;
        this.strategy = strategy;
        this.retryOnFailure = retryOnFailure;
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 200);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", this::forward);
        server.start();
        Thread.ofVirtual().start(this::healthLoop);
    }

    public URI uri() { return URI.create("http://127.0.0.1:" + server.getAddress().getPort()); }

    private Target pick(Target skip) {
        List<Target> up = targets.stream().filter(t -> t.healthy && t != skip).toList();
        if (up.isEmpty()) return null;
        return switch (strategy) {
            case ROUND_ROBIN -> up.get(Math.floorMod(next.getAndIncrement(), up.size()));
            case LEAST_CONNECTIONS -> up.stream().min(Comparator.comparingInt(t -> t.active.get())).orElseThrow();
        };
    }

    private void forward(HttpExchange ex) throws IOException {
        byte[] body = ex.getRequestBody().readAllBytes();   // read once, so a retry can resend it
        Target t = pick(null);
        while (t != null) {
            t.active.incrementAndGet();
            try {
                var req = HttpRequest.newBuilder(t.uri.resolve(ex.getRequestURI())).timeout(Duration.ofSeconds(5))
                    .method(ex.getRequestMethod(), HttpRequest.BodyPublishers.ofByteArray(body));
                ex.getRequestHeaders().forEach((k, vs) -> {
                    if (!HOP_HEADERS.contains(k.toLowerCase())) vs.forEach(v -> req.header(k, v));
                });
                var resp = client.send(req.build(), HttpResponse.BodyHandlers.ofString());
                resp.headers().map().forEach((k, vs) -> {
                    if (!HOP_HEADERS.contains(k.toLowerCase())) ex.getResponseHeaders().put(k, vs);
                });
                t.served.incrementAndGet();
                Backend.reply(ex, resp.statusCode(), resp.body());
                return;
            } catch (IOException e) {
                // Only GET is safe to send twice (L32): a retried POST could create the book twice.
                if (!retryOnFailure || !ex.getRequestMethod().equals("GET")) { Backend.reply(ex, 502, "bad gateway: " + t.name); return; }
                t.healthy = false;                          // passive check: stop using it right away
                log("passive: " + t.name + " failed a request, marked DOWN, retrying elsewhere");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                t.active.decrementAndGet();
            }
            t = pick(t);                                    // only reached after a failure, with retry on
        }
        Backend.reply(ex, 503, "no healthy backend");
    }

    // Active health check: ask every backend for /health every 500 ms.
    private void healthLoop() {
        while (running) {
            for (Target t : targets) {
                boolean ok;
                try {
                    var req = HttpRequest.newBuilder(t.uri.resolve("/health")).timeout(Duration.ofMillis(300)).build();
                    ok = client.send(req, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
                } catch (IOException e) {
                    ok = false;
                } catch (InterruptedException e) {
                    return;
                }
                if (ok != t.healthy) {
                    t.healthy = ok;
                    log("health check: " + t.name + " is " + (ok ? "UP" : "DOWN"));
                }
            }
            try { Thread.sleep(500); } catch (InterruptedException e) { return; }
        }
    }

    public void log(String msg) {
        System.out.printf("  [%4d ms] %s%n", (System.nanoTime() - started) / 1_000_000, msg);
    }

    @Override public void close() { running = false; server.stop(0); }
}
