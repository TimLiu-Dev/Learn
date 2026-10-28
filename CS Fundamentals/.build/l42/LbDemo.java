import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LbDemo {
    static final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    static HttpResponse<String> get(URI uri) throws Exception {
        return http.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws Exception {
        List<Backend.Proc> procs = new ArrayList<>(List.of(
            Backend.start("b1", 20), Backend.start("b2", 200), Backend.start("b3", 20)));
        System.out.println("3 backend processes: " + procs.stream()
            .map(p -> p.name() + " pid " + p.process().pid()).toList());
        try {
            System.out.println("-- 60 requests to /slow, one every 10 ms (b2 takes 200 ms, b1 and b3 take 20 ms)");
            for (var strategy : LoadBalancer.Strategy.values()) {
                var targets = targetsOf(procs);
                try (var lb = new LoadBalancer(targets, strategy, false)) {
                    long ms = arrivals(lb.uri().resolve("/slow"), 60, 10);
                    System.out.printf("  %-17s all done after %5d ms   %s%n", strategy, ms, targets);
                }
            }
            System.out.println("-- kill b2 mid-run, active health checks only");
            killMidRun(procs, false);
            procs.set(1, Backend.start("b2", 0));               // a fresh b2 for round two
            System.out.println("-- kill b2 again, now with retry on failure");
            killMidRun(procs, true);
        } finally {
            procs.forEach(p -> p.process().destroyForcibly());
        }
    }

    static List<LoadBalancer.Target> targetsOf(List<Backend.Proc> procs) {
        return procs.stream().map(p -> new LoadBalancer.Target(p.name(), p.uri())).toList();
    }

    // Open-loop load: a new request every gapMs, whether or not earlier ones have finished.
    static long arrivals(URI uri, int n, long gapMs) throws InterruptedException {
        long t0 = System.nanoTime();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < n; i++) {
                pool.submit(() -> get(uri));
                Thread.sleep(gapMs);
            }
        }
        return (System.nanoTime() - t0) / 1_000_000;
    }

    static void killMidRun(List<Backend.Proc> procs, boolean retry) throws Exception {
        var targets = targetsOf(procs);
        try (var lb = new LoadBalancer(targets, LoadBalancer.Strategy.ROUND_ROBIN, retry)) {
            int ok = 0, failed = 0;
            for (int i = 0; i < 60; i++) {
                if (i == 20) {
                    procs.get(1).process().destroyForcibly().waitFor();
                    lb.log("killed " + procs.get(1).name() + " (like kill -9)");
                }
                var resp = get(lb.uri().resolve("/name"));
                if (resp.statusCode() == 200) ok++;
                else { failed++; lb.log("request " + i + " -> " + resp.statusCode() + " " + resp.body()); }
                Thread.sleep(25);
            }
            System.out.println("  60 requests: " + ok + " ok, " + failed + " failed   " + targets);
        }
    }
}
