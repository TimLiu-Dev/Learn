import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Throughput {
    public static void main(String[] args) throws Exception {
        List<Backend.Proc> procs = List.of(Backend.start("b1", 0), Backend.start("b2", 0), Backend.start("b3", 0));
        try {
            for (int n = 1; n <= 3; n++) {
                var targets = LbDemo.targetsOf(procs.subList(0, n));
                try (var lb = new LoadBalancer(targets, LoadBalancer.Strategy.ROUND_ROBIN, false)) {
                    load(lb.uri().resolve("/work"), 24, 1000);                     // warm-up
                    double perSecond = load(lb.uri().resolve("/work"), 24, 3000) / 3.0;
                    System.out.printf("%d backend(s): %4.0f hashes/s   %s%n", n, perSecond, targets);
                }
            }
            var big = Backend.start("big", 0, 3);              // scale UP: one server, 3 threads
            try (var lb = new LoadBalancer(LbDemo.targetsOf(List.of(big)), LoadBalancer.Strategy.ROUND_ROBIN, false)) {
                load(lb.uri().resolve("/work"), 24, 1000);
                System.out.printf("1 backend, 3 threads: %4.0f hashes/s%n", load(lb.uri().resolve("/work"), 24, 3000) / 3.0);
            }
            big.process().destroyForcibly();
        } finally {
            procs.forEach(p -> p.process().destroyForcibly());
        }
    }

    // `clients` virtual threads call `uri` in a loop until time is up; returns how many calls succeeded.
    static int load(URI uri, int clients, long millis) {
        var done = new AtomicInteger();
        long deadline = System.nanoTime() + millis * 1_000_000;
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int c = 0; c < clients; c++) {
                pool.submit(() -> {
                    while (System.nanoTime() < deadline) {
                        if (LbDemo.get(uri).statusCode() == 200) done.incrementAndGet();
                    }
                    return null;
                });
            }
        }
        return done.get();
    }
}
