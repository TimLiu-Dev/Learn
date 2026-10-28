package shortener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/** Token bucket per client (L43): `capacity` burst, refilled at `perSecond` tokens/s. */
public class RateLimiter {
    private static final class Bucket {
        double tokens;
        long last;
        Bucket(double tokens, long last) { this.tokens = tokens; this.last = last; }
    }

    private final double capacity;
    private final double perSecond;
    private final LongSupplier nanoTime;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter(double capacity, double perSecond) { this(capacity, perSecond, System::nanoTime); }

    public RateLimiter(double capacity, double perSecond, LongSupplier nanoTime) {
        this.capacity = capacity;
        this.perSecond = perSecond;
        this.nanoTime = nanoTime;
    }

    /** 0 if allowed, otherwise the whole seconds to wait (for Retry-After). */
    public long tryAcquire(String client) {
        long now = nanoTime.getAsLong();
        Bucket b = buckets.computeIfAbsent(client, k -> new Bucket(capacity, now));
        synchronized (b) {
            b.tokens = Math.min(capacity, b.tokens + (now - b.last) / 1e9 * perSecond);
            b.last = now;
            if (b.tokens >= 1) {
                b.tokens -= 1;
                return 0;
            }
            return (long) Math.ceil((1 - b.tokens) / perSecond);
        }
    }
}
