import java.time.Instant;
import java.time.InstantSource;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimit {
    public static void main(String[] args) {
        AtomicLong now = new AtomicLong(0);
        InstantSource clock = () -> Instant.ofEpochMilli(now.get());
        TokenBucket bucket = new TokenBucket(5.0, 5.0, clock);
        SlidingWindowLog window = new SlidingWindowLog(5, 1000, clock);
        long[][] schedule = { {0, 7}, {500, 3}, {1000, 4}, {1200, 3}, {2000, 6} };   // {time, requests}

        System.out.println("time   asked  token bucket          sliding window log");
        for (long[] s : schedule) {
            long t = s[0]; int n = (int) s[1];
            now.set(t);
            double before = bucket.tokens();
            int b = 0, w = 0;
            for (int i = 0; i < n; i++) if (bucket.tryAcquire()) b++;
            for (int i = 0; i < n; i++) if (window.tryAcquire()) w++;
            System.out.printf("%4d ms  %2d    %d ok, %d rejected (%.1f→%.1f tokens)   %d ok, %d rejected%n",
                    t, n, b, n - b, before, bucket.tokens(), w, n - w);
        }
    }
}

// Token bucket: holds up to `capacity` tokens and refills at `perSecond`.
// Each request takes one token. Bursts up to `capacity` pass at once.
class TokenBucket {
    private final double capacity, perSecond;
    private final InstantSource clock;
    private double tokens;
    private long last;

    TokenBucket(double capacity, double perSecond, InstantSource clock) {
        this.capacity = capacity; this.perSecond = perSecond; this.clock = clock;
        this.tokens = capacity; this.last = clock.millis();
    }

    synchronized boolean tryAcquire() {
        refill();
        if (tokens < 1) return false;
        tokens -= 1;
        return true;
    }

    synchronized long retryAfterSeconds() { refill(); return tokens >= 1 ? 0 : (long) Math.ceil((1 - tokens) / perSecond); }
    synchronized double tokens() { refill(); return tokens; }

    private void refill() {
        long now = clock.millis();
        tokens = Math.min(capacity, tokens + (now - last) / 1000.0 * perSecond);
        last = now;
    }
}

// Sliding window log: remember when each accepted request happened;
// allow a new one only if fewer than `limit` happened in the last `windowMs`.
class SlidingWindowLog {
    private final int limit;
    private final long windowMs;
    private final InstantSource clock;
    private final ArrayDeque<Long> log = new ArrayDeque<>();

    SlidingWindowLog(int limit, long windowMs, InstantSource clock) { this.limit = limit; this.windowMs = windowMs; this.clock = clock; }

    synchronized boolean tryAcquire() {
        long now = clock.millis();
        while (!log.isEmpty() && log.peekFirst() <= now - windowMs) log.removeFirst();
        if (log.size() >= limit) return false;
        log.addLast(now);
        return true;
    }
}
