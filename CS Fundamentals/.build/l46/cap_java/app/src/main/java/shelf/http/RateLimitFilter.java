package shelf.http;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.InstantSource;
import java.util.Map;
import java.util.Set;
import shelf.cache.LruCache;

/**
 * One bucket per client, checked before the router runs. A com.sun.net.httpserver.Filter
 * wraps every request on its context: call chain.doFilter to continue, or answer and stop.
 */
public final class RateLimitFilter extends Filter {
    private static final Set<String> EXEMPT = Set.of("/health", "/metrics");
    private final double capacity, perSecond;
    private final InstantSource clock;
    // Bounded, so a flood of made-up client ids can't grow memory forever (the L24 leak).
    private final LruCache<String, TokenBucket> buckets = new LruCache<>(10_000, Duration.ofMinutes(10), Clock.systemUTC());

    public RateLimitFilter(double capacity, double perSecond, InstantSource clock) {
        this.capacity = capacity;
        this.perSecond = perSecond;
        this.clock = clock;
    }

    @Override
    public String description() {
        return "per-client token bucket";
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        if (EXEMPT.contains(exchange.getRequestURI().getPath())) {
            chain.doFilter(exchange);
            return;
        }
        TokenBucket bucket = buckets.getOrPut(clientOf(exchange), () -> new TokenBucket(capacity, perSecond, clock));
        if (bucket.tryAcquire()) {
            chain.doFilter(exchange);
            return;
        }
        long wait = bucket.retryAfterSeconds();
        try (exchange) {
            Router.send(exchange, Json.problem(new ApiError(429, "Too Many Requests", "Rate limit exceeded; retry in " + wait + " s"),
                                               Map.of("Retry-After", String.valueOf(wait))));        // RFC 6585 §4
        }
    }

    /** Who is asking: an API client id if sent, otherwise the caller's IP address. */
    public static String clientOf(HttpExchange exchange) {
        String id = exchange.getRequestHeaders().getFirst("X-Client-Id");
        return id != null ? id : exchange.getRemoteAddress().getAddress().getHostAddress();
    }
}
