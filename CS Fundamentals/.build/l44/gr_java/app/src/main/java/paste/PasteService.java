package paste;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import shortener.Base62;
import shortener.LruCache;
import shortener.RateLimiter;

public class PasteService {
    public sealed interface Created permits Ok, TooLarge, RateLimited {}
    public record Ok(String id) implements Created {}
    public record TooLarge(int limitBytes) implements Created {}
    public record RateLimited(long retryAfterSeconds) implements Created {}

    public sealed interface Read permits Found, Expired, NotFound {}
    public record Found(String content) implements Read {}
    public record Expired() implements Read {}
    public record NotFound() implements Read {}

    private final PasteStore store;
    private final Clock clock;
    private final int maxBytes;
    // 5 pastes in a burst, then one every 12 s; the bucket runs on the injected clock
    private final RateLimiter limiter;
    public final LruCache<String, PasteStore.Row> cache;

    public PasteService(PasteStore store, Clock clock) { this(store, clock, 1_000, 64 * 1024); }

    public PasteService(PasteStore store, Clock clock, int cacheSize, int maxBytes) {
        this.store = store;
        this.clock = clock;
        this.maxBytes = maxBytes;
        this.limiter = new RateLimiter(5.0, 5 / 60.0, () -> clock.millis() * 1_000_000);
        this.cache = new LruCache<>(cacheSize);
    }

    public Created create(String client, String content, Duration ttl) throws SQLException {
        long wait = limiter.tryAcquire(client);
        if (wait > 0) return new RateLimited(wait);
        if (content.getBytes(StandardCharsets.UTF_8).length > maxBytes) return new TooLarge(maxBytes);
        long now = clock.millis();
        Long expiresAt = ttl == null ? null : now + ttl.toMillis();
        for (int attempt = 0; attempt < 5; attempt++) {
            String id = Base62.random(8);                          // unguessable: pastes may be private-ish
            if (store.insert(id, content, now, expiresAt)) return new Ok(id);
        }
        throw new IllegalStateException("5 random ids in a row were taken");
    }

    public Read read(String id) throws SQLException {
        PasteStore.Row row = cache.get(id);
        if (row == null) {
            row = store.find(id);
            if (row == null) return new NotFound();
            cache.put(id, row);
        }
        if (row.expiresAt() != null && row.expiresAt() <= clock.millis()) {
            cache.remove(id);
            return new Expired();
        }
        return new Found(row.content());
    }

    /** The cleanup job the design doc schedules hourly. */
    public int purgeExpired() throws SQLException { return store.deleteExpired(clock.millis()); }
}
