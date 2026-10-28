import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

// Hand-written metrics: what Micrometer or a Prometheus client gives you, in miniature.
public class Metrics {
    public static void main(String[] args) {
        Counter requests = new Counter("http_requests_total", "Requests received.");
        Counter errors = new Counter("http_errors_total", "Requests that failed (5xx).");
        Histogram latency = new Histogram("http_request_duration_ms", "Request latency.", new long[] {5, 10, 25, 50, 100, 250, 500, 1000});
        int inFlight = 0;                                         // a gauge: a value that goes up and down

        Random random = new Random(43);
        for (int i = 0; i < 1000; i++) {
            inFlight++;
            requests.inc();
            long ms = random.nextInt(100) < 4 ? 300 + random.nextInt(700) : 8 + random.nextInt(30);   // 4% slow
            if (random.nextInt(100) < 2) errors.inc();
            latency.observe(ms);
            inFlight--;
        }

        System.out.println("GET /metrics");
        System.out.println("# TYPE " + requests.name + " counter\n" + requests.name + " " + requests.value());
        System.out.println("# TYPE " + errors.name + " counter\n" + errors.name + " " + errors.value());
        System.out.println("# TYPE http_in_flight gauge\nhttp_in_flight " + inFlight);
        System.out.println(latency.render());
        System.out.println();
        System.out.printf("golden signals: traffic=%d requests, errors=%.1f%%, latency p50 %s, p95 %s, p99 %s, saturation in-flight=%d%n",
                requests.value(), 100.0 * errors.value() / requests.value(),
                latency.percentile(0.50), latency.percentile(0.95), latency.percentile(0.99), inFlight);
    }
}

class Counter {
    final String name, help;
    private final AtomicLong n = new AtomicLong();
    Counter(String name, String help) { this.name = name; this.help = help; }
    long inc() { return n.incrementAndGet(); }
    long value() { return n.get(); }
}

class Histogram {
    final String name, help;
    private final long[] bounds;                                   // upper bounds in ms
    private final AtomicLongArray counts;                          // last slot = +Inf
    private final AtomicLong sum = new AtomicLong();

    Histogram(String name, String help, long[] bounds) {
        this.name = name; this.help = help; this.bounds = bounds; this.counts = new AtomicLongArray(bounds.length + 1);
    }

    void observe(long ms) {
        int i = 0;
        while (i < bounds.length && ms > bounds[i]) i++;
        counts.incrementAndGet(i); sum.addAndGet(ms);
    }

    long count() { long c = 0; for (int i = 0; i <= bounds.length; i++) c += counts.get(i); return c; }

    // Estimate a percentile: the upper bound of the bucket where the running total crosses it.
    String percentile(double p) {
        double target = p * count();
        long seen = 0;
        for (int i = 0; i <= bounds.length; i++) {
            seen += counts.get(i);
            if (seen >= target) return i < bounds.length ? "≤ " + bounds[i] + " ms" : "> " + bounds[bounds.length - 1] + " ms";
        }
        return "-";
    }

    String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("# HELP ").append(name).append(' ').append(help).append('\n');
        sb.append("# TYPE ").append(name).append(" histogram\n");
        long cumulative = 0;
        for (int i = 0; i <= bounds.length; i++) {
            cumulative += counts.get(i);
            String le = i < bounds.length ? Long.toString(bounds[i]) : "+Inf";
            sb.append(name).append("_bucket{le=\"").append(le).append("\"} ").append(cumulative).append('\n');
        }
        sb.append(name).append("_sum ").append(sum.get()).append('\n');
        sb.append(name).append("_count ").append(count());
        return sb.toString();
    }
}
