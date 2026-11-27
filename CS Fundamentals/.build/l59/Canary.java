import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Canary {
    static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    /** One backend. Release v2 has a bug: every 5th request fails. */
    static class Backend {
        final String version;
        final HttpServer server;
        final URI uri;
        private final AtomicInteger count = new AtomicInteger();

        Backend(String version, int failEvery) throws IOException {
            this.version = version;
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50);
            server.createContext("/", ex -> {
                try (ex) {
                    int n = count.incrementAndGet();
                    if (failEvery > 0 && n % failEvery == 0) reply(ex, 500, "boom"); else reply(ex, 200, version);
                }
            });
            server.start();
            uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
        }
    }

    /** L42's round-robin balancer, plus a canary slice chosen by user. */
    static class CanaryBalancer {
        volatile int canaryPercent = 0;             // the kill switch: set it to 0 and the canary gets nothing
        private final List<Backend> stable;
        private final Backend canary;
        private final AtomicInteger next = new AtomicInteger();
        private final HttpClient client = HttpClient.newHttpClient();
        final Map<String, int[]> stats = new HashMap<>();   // version -> [requests, errors]

        CanaryBalancer(List<Backend> stable, Backend canary) { this.stable = stable; this.canary = canary; }

        Backend pick(String user) {
            if (Math.floorMod(user.hashCode(), 100) < canaryPercent) return canary;
            return stable.get(Math.floorMod(next.getAndIncrement(), stable.size()));
        }

        int handle(String user) throws IOException, InterruptedException {
            Backend b = pick(user);
            int status = client.send(HttpRequest.newBuilder(b.uri).build(), HttpResponse.BodyHandlers.ofString()).statusCode();
            synchronized (stats) {
                int[] s = stats.computeIfAbsent(b.version, k -> new int[2]);
                s[0]++;
                if (status >= 500) s[1]++;
            }
            return status;
        }

        double errorRate(String version) {
            synchronized (stats) {
                int[] s = stats.get(version);
                return s == null || s[0] == 0 ? 0.0 : (double) s[1] / s[0];
            }
        }
    }

    static final List<String> USERS = new ArrayList<>();
    static final Set<String> UNLUCKY = new HashSet<>();

    static void traffic(CanaryBalancer lb, int requests) throws IOException, InterruptedException {
        for (int i = 0; i < requests; i++) {
            String user = USERS.get(i % USERS.size());
            if (lb.handle(user) >= 500) UNLUCKY.add(user);
        }
    }

    public static void main(String[] args) throws Exception {
        List<Backend> stable = List.of(new Backend("v1", 0), new Backend("v1", 0));
        Backend canary = new Backend("v2", 5);
        CanaryBalancer lb = new CanaryBalancer(stable, canary);
        for (int i = 1; i <= 200; i++) USERS.add("user" + i);

        lb.canaryPercent = 10;
        long onCanary = USERS.stream().filter(u -> lb.pick(u) == canary).count();
        System.out.println("canary at " + lb.canaryPercent + "%: " + onCanary + " of " + USERS.size() + " users go to v2");
        traffic(lb, 1000);
        for (String v : List.of("v1", "v2")) {
            int[] s = lb.stats.get(v);
            System.out.println("%s  %4d requests  %3d errors  %5.1f%%".formatted(v, s[0], s[1], 100 * lb.errorRate(v)));
        }
        // The canary analysis: compare against the control, with enough requests to mean something.
        int canaryRequests = lb.stats.get("v2")[0];
        if (canaryRequests >= 50 && lb.errorRate("v2") > lb.errorRate("v1") + 0.01) {
            lb.canaryPercent = 0;
            System.out.println("ROLLBACK: v2 error rate is above v1's, canary set to 0%");
        }
        int[] before = lb.stats.get("v1").clone();
        traffic(lb, 1000);
        int[] after = lb.stats.get("v1");
        System.out.println("next 1000 requests: " + (after[0] - before[0]) + " to v1, " + (after[1] - before[1])
            + " errors, v2 got " + (lb.stats.get("v2")[0] - canaryRequests));
        int failed = lb.stats.values().stream().mapToInt(s -> s[1]).sum();
        System.out.println("in total: %d of 2000 requests failed (%.1f%%), seen by %d of %d users"
            .formatted(failed, 100.0 * failed / 2000, UNLUCKY.size(), USERS.size()));
        for (Backend b : List.of(stable.get(0), stable.get(1), canary)) b.server.stop(0);
    }
}
