package shelf.http;

import java.time.InstantSource;

/** L43's token bucket, unchanged. Up to {@code capacity} tokens, refilled at {@code perSecond}; each request takes one. */
public final class TokenBucket {
    private final double capacity, perSecond;
    private final InstantSource clock;
    private double tokens;
    private long last;

    public TokenBucket(double capacity, double perSecond, InstantSource clock) {
        this.capacity = capacity; this.perSecond = perSecond; this.clock = clock;
        this.tokens = capacity; this.last = clock.millis();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens < 1) return false;
        tokens -= 1;
        return true;
    }

    /** Whole seconds until a token is free: the value for a Retry-After header. */
    public synchronized long retryAfterSeconds() { refill(); return Math.max(1, (long) Math.ceil((1 - tokens) / perSecond)); }

    private void refill() {
        long now = clock.millis();
        tokens = Math.min(capacity, tokens + (now - last) / 1000.0 * perSecond);
        last = now;
    }
}
