package shelf.http;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.LongAdder;

/** The four golden signals (L43): traffic and errors (by status), latency (histogram), saturation (in flight). */
public final class Metrics {
    private final ConcurrentHashMap<Integer, LongAdder> byStatus = new ConcurrentHashMap<>();
    private final long[] bounds = {1, 2, 5, 10, 25, 50, 100, 250, 500, 1000};     // ms; one more slot = +Inf
    private final AtomicLongArray buckets = new AtomicLongArray(bounds.length + 1);
    private final AtomicLong sumMs = new AtomicLong();
    final AtomicInteger inFlight = new AtomicInteger();

    public void record(int status, long ms) {
        byStatus.computeIfAbsent(status, s -> new LongAdder()).increment();
        int i = 0;
        while (i < bounds.length && ms > bounds[i]) i++;
        buckets.incrementAndGet(i);
        sumMs.addAndGet(ms);
    }

    /** Plain text in the shape of Prometheus' exposition format; extra gauges come from the caller. */
    public String render(Map<String, Number> gauges) {
        StringBuilder sb = new StringBuilder("# TYPE shelf_requests_total counter\n");
        new TreeMap<>(byStatus).forEach((code, n) -> sb.append("shelf_requests_total{code=\"").append(code).append("\"} ").append(n.sum()).append('\n'));
        sb.append("# TYPE shelf_request_ms histogram\n");
        long cumulative = 0;
        for (int i = 0; i <= bounds.length; i++) {
            cumulative += buckets.get(i);
            sb.append("shelf_request_ms_bucket{le=\"").append(i < bounds.length ? String.valueOf(bounds[i]) : "+Inf")
              .append("\"} ").append(cumulative).append('\n');
        }
        sb.append("shelf_request_ms_sum ").append(sumMs.get()).append('\n');
        sb.append("shelf_request_ms_count ").append(cumulative).append('\n');
        sb.append("shelf_in_flight ").append(inFlight.get()).append('\n');
        gauges.forEach((name, value) -> sb.append(name).append(' ').append(value).append('\n'));
        return sb.toString();
    }
}
