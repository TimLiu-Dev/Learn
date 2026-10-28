package gateway;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Telemetry {
    private Telemetry() {}

    /** One JSON object per line: easy to grep, easy for a log pipeline to parse. Pairs: key, value, key, value… */
    public static String json(Object... kv) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < kv.length; i += 2) {
            if (i > 0) sb.append(',');
            Object v = kv[i + 1];
            sb.append('"').append(kv[i]).append("\":");
            sb.append(v == null ? "null" : (v instanceof Number || v instanceof Boolean) ? v.toString() : "\"" + v + "\"");
        }
        return sb.append('}').toString();
    }

    public static final class Counter {
        private final AtomicLong n = new AtomicLong();
        public long inc() { return n.incrementAndGet(); }
        public long value() { return n.get(); }
    }

    public static final class Histogram {                          // upper bounds in ms; last slot = +Inf
        private final long[] bounds;
        private final AtomicLongArray counts;
        private final AtomicLong sum = new AtomicLong();
        public Histogram(long... bounds) { this.bounds = bounds; this.counts = new AtomicLongArray(bounds.length + 1); }

        public void observe(long ms) {
            int i = 0;
            while (i < bounds.length && ms > bounds[i]) i++;
            counts.incrementAndGet(i); sum.addAndGet(ms);
        }
        public long count() { long c = 0; for (int i = 0; i <= bounds.length; i++) c += counts.get(i); return c; }
        public String percentileBound(double p) {
            double target = p * count(); long seen = 0;
            for (int i = 0; i <= bounds.length; i++) { seen += counts.get(i); if (seen >= target) return i < bounds.length ? Long.toString(bounds[i]) : "+Inf"; }
            return "+Inf";
        }
        public String render(String name) {
            StringBuilder sb = new StringBuilder("# TYPE " + name + " histogram\n");
            long cumulative = 0;
            for (int i = 0; i <= bounds.length; i++) {
                cumulative += counts.get(i);
                sb.append(name).append("_bucket{le=\"").append(i < bounds.length ? Long.toString(bounds[i]) : "+Inf").append("\"} ").append(cumulative).append('\n');
            }
            return sb.append(name).append("_sum ").append(sum.get()).append('\n').append(name).append("_count ").append(count()).toString();
        }
    }

    // ---- W3C Trace Context: traceparent = 00-<32 hex trace-id>-<16 hex parent-id>-<2 hex flags>
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern TRACEPARENT = Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$");

    public static String randomHex(int bytes) { byte[] b = new byte[bytes]; RANDOM.nextBytes(b); return HexFormat.of().formatHex(b); }
    public static String traceparent(String traceId, String spanId) { return "00-" + traceId + "-" + spanId + "-01"; }

    /** {traceId, parentId}, or null if the header is missing or invalid (all-zero ids are invalid). */
    public static String[] parseTraceparent(String header) {
        if (header == null) return null;
        Matcher m = TRACEPARENT.matcher(header);
        if (!m.matches()) return null;
        if (m.group(1).chars().allMatch(c -> c == '0') || m.group(2).chars().allMatch(c -> c == '0')) return null;
        return new String[] { m.group(1), m.group(2) };
    }

    public record Span(String service, String name, String traceId, String spanId, String parentId, long startMs, long durMs, String outcome) {}

    /** Stands in for a trace collector such as an OpenTelemetry Collector. */
    public static final class Tracer {
        private static final long T0 = System.nanoTime();
        public static final Queue<Span> spans = new ConcurrentLinkedQueue<>();
        public static long nowMs() { return (System.nanoTime() - T0) / 1_000_000; }
        public static void record(Span s) { spans.add(s); }
    }
}
