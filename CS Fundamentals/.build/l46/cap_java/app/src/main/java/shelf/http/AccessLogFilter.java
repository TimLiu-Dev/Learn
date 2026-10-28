package shelf.http;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The outermost filter: gives every request an id and a W3C trace context (L43), times it,
 * records metrics, and writes one JSON access-log line, even for requests the rate limiter refused.
 */
public final class AccessLogFilter extends Filter {
    private static final Pattern TRACEPARENT = Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$");
    private final SecureRandom random = new SecureRandom();
    private final Metrics metrics;
    private final Clock clock;
    private final Consumer<String> log;

    public AccessLogFilter(Metrics metrics, Clock clock, Consumer<String> log) {
        this.metrics = metrics;
        this.clock = clock;
        this.log = log;
    }

    private String hex(int bytes) {
        byte[] b = new byte[bytes];
        random.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }

    @Override
    public String description() {
        return "request id, trace context, metrics, JSON access log";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        long start = System.nanoTime();
        String incomingId = exchange.getRequestHeaders().getFirst("X-Request-Id");
        String requestId = incomingId != null ? incomingId : hex(8);
        String callerTrace = parseTraceId(exchange.getRequestHeaders().getFirst("traceparent"));
        String traceId = callerTrace != null ? callerTrace : hex(16);
        exchange.getResponseHeaders().set("X-Request-Id", requestId);
        exchange.getResponseHeaders().set("traceparent", "00-" + traceId + "-" + hex(8) + "-01");
        metrics.inFlight.incrementAndGet();
        try {
            chain.doFilter(exchange);
        } finally {
            metrics.inFlight.decrementAndGet();
            long ms = (System.nanoTime() - start) / 1_000_000;
            int status = exchange.getResponseCode();                  // -1 if nothing was sent
            metrics.record(status, ms);
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("ts", clock.instant());
            line.put("level", status >= 500 ? "ERROR" : "INFO");
            line.put("requestId", requestId);
            line.put("traceId", traceId);
            line.put("method", exchange.getRequestMethod());
            line.put("path", exchange.getRequestURI().getPath());
            line.put("status", status);
            line.put("ms", ms);
            line.put("client", RateLimitFilter.clientOf(exchange));
            log.accept(jsonLine(line));
        }
    }

    /** The caller's trace id, so our log lines join their trace; null if absent or invalid. */
    public static String parseTraceId(String header) {
        if (header == null) return null;
        Matcher m = TRACEPARENT.matcher(header);
        if (!m.matches() || m.group(1).chars().allMatch(c -> c == '0')) return null;
        return m.group(1);
    }

    /** One JSON object per line (L43): easy to grep, easy for a log pipeline to parse. */
    public static String jsonLine(Map<String, ?> fields) {
        StringBuilder sb = new StringBuilder("{");
        fields.forEach((k, v) -> {
            if (sb.length() > 1) sb.append(',');
            sb.append('"').append(k).append("\":");
            if (v == null) sb.append("null");
            else if (v instanceof Number || v instanceof Boolean) sb.append(v);
            else sb.append('"').append(v.toString().replace("\"", "\\\"")).append('"');
        });
        return sb.append('}').toString();
    }
}
