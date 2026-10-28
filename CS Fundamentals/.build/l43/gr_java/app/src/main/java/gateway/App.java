package gateway;

import java.net.URI;
import java.net.http.*;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import static gateway.Telemetry.*;

/** One scripted run: 3 polite clients (~8 req/s each) and 1 greedy one (~60 req/s) for 10 s. */
public final class App {
    static final int SECONDS = 10;
    record Result(int second, int status, boolean stale, long ms) {}

    public static void main(String[] args) throws Exception {
        PricingService pricing = new PricingService(43, 4000, 6000);
        pricing.server.start();
        Gateway gateway = new Gateway(pricing.server.getAddress().getPort(), Clock.systemUTC());
        gateway.server.start();
        String base = "http://127.0.0.1:" + gateway.server.getAddress().getPort();
        HttpClient http = HttpClient.newHttpClient();

        Queue<Result> results = new ConcurrentLinkedQueue<>();
        Map<Integer, Set<String>> states = new ConcurrentHashMap<>();
        long start = System.nanoTime();

        Thread sampler = Thread.ofVirtual().start(() -> {                    // breaker state, 10 times a second
            while (second(start) < SECONDS) {
                states.computeIfAbsent(second(start), k -> ConcurrentHashMap.newKeySet()).add(gateway.breaker.state().name());
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
            }
        });
        List<Thread> clients = List.of(client(http, base, "alice", 120, start, results), client(http, base, "bob", 120, start, results),
                client(http, base, "carol", 120, start, results), client(http, base, "greedy", 15, start, results));
        for (Thread t : clients) t.join();
        sampler.join();

        System.out.println("\nsec  req  fresh stale  429  503   p95 ms  circuit");
        for (int s = 0; s < SECONDS; s++) {
            final int sec = s;
            List<Result> rs = results.stream().filter(r -> r.second() == sec).toList();
            List<Long> served = rs.stream().filter(r -> r.status() != 429).map(Result::ms).sorted().toList();
            long p95 = served.isEmpty() ? 0 : served.get((int) ((served.size() - 1) * 0.95));
            String seen = states.getOrDefault(s, Set.of()).stream()
                    .sorted(Comparator.comparingInt(n -> CircuitBreaker.State.valueOf(n).ordinal())).reduce((a, b) -> a + "/" + b).orElse("");
            System.out.printf("%3d %4d %5d %5d %4d %4d %8d  %s%n", s, rs.size(),
                    rs.stream().filter(r -> r.status() == 200 && !r.stale()).count(), rs.stream().filter(Result::stale).count(),
                    rs.stream().filter(r -> r.status() == 429).count(), rs.stream().filter(r -> r.status() == 503).count(), p95, seen);
        }

        // One request that timed out and was retried, drawn as a waterfall
        List<Span> spans = new ArrayList<>(Tracer.spans);
        List<Span> roots = spans.stream().filter(sp -> sp.service().equals("gateway") && sp.parentId() == null).toList();
        Span retried = roots.stream().filter(r -> children(spans, r).size() >= 2 && children(spans, r).stream().anyMatch(c -> c.outcome().equals("timeout")))
                .findFirst().orElse(roots.stream().filter(r -> children(spans, r).size() >= 2).findFirst().orElse(null));
        if (retried != null) {
            System.out.println("\ntrace " + retried.traceId() + ": " + retried.name() + " -> " + retried.outcome() + " in " + retried.durMs() + " ms");
            List<Map.Entry<Span, Integer>> tree = new ArrayList<>(List.of(Map.entry(retried, 0)));
            for (Span a : children(spans, retried)) {
                tree.add(Map.entry(a, 1));
                for (Span p : children(spans, a)) tree.add(Map.entry(p, 2));
            }
            long scale = Math.max(retried.durMs(), tree.stream().mapToLong(e -> e.getKey().startMs() + e.getKey().durMs() - retried.startMs()).max().orElse(1));
            for (var e : tree) {
                Span sp = e.getKey(); int depth = e.getValue();
                int from = (int) ((sp.startMs() - retried.startMs()) * 32 / scale);
                int width = Math.max(1, (int) (sp.durMs() * 32 / scale));
                System.out.printf("%-20s %s%s%s %5d ms  %s%n", "  ".repeat(depth) + (depth == 1 ? sp.name() : sp.service()),
                        " ".repeat(from), "█".repeat(width), " ".repeat(Math.max(0, 32 - from - width)), sp.durMs(), sp.outcome());
            }
        }

        System.out.println("\nGET /metrics");
        System.out.println(http.send(HttpRequest.newBuilder(URI.create(base + "/metrics")).build(), HttpResponse.BodyHandlers.ofString()).body());
        System.out.println("p95 from the histogram: ≤ " + gateway.latency.percentileBound(0.95) + " ms");
        gateway.server.stop(0); pricing.server.stop(0);
        System.exit(0);                                                       // hung 2 s pricing handlers may still be sleeping
    }

    static int second(long start) { return (int) ((System.nanoTime() - start) / 1_000_000_000); }

    static List<Span> children(List<Span> spans, Span parent) {
        return spans.stream().filter(s -> parent.spanId().equals(s.parentId())).sorted(Comparator.comparingLong(Span::startMs)).toList();
    }

    static Thread client(HttpClient http, String base, String name, long pauseMs, long start, Queue<Result> results) {
        return Thread.ofVirtual().start(() -> {
            int n = 0;
            try {
                while (second(start) < SECONDS) {
                    int sec = second(start); long t = System.nanoTime();
                    HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base + "/api/price/" + (n++ % 5))).header("X-Client", name).build(),
                            HttpResponse.BodyHandlers.ofString());
                    results.add(new Result(sec, r.statusCode(), r.headers().firstValue("X-Fallback").isPresent(), (System.nanoTime() - t) / 1_000_000));
                    Thread.sleep(pauseMs);
                }
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}
