package shelf;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

/**
 * Everything that differs between your laptop and a server, read from environment variables
 * (no secrets in code, L33). A setting of 0 switches that feature off.
 */
public record Config(
    int port,
    Path dbFile,
    int poolSize,                    // 0: a new connection per call (v1)
    int busyTimeoutMs,
    int cacheSize,                   // 0: no cache
    Duration cacheTtl,
    double rateCapacity,             // 0: no rate limit
    double ratePerSecond,
    int drainSeconds) {              // graceful shutdown: max wait for in-flight requests

    public static Config defaults() {
        return new Config(8080, Path.of("shelf.db"), 8, 3000, 1000, Duration.ofSeconds(30), 100, 50, 5);
    }

    public static Config fromEnv(Map<String, String> env) {
        Config d = defaults();
        return new Config(
            intOr(env, "SHELF_PORT", d.port),
            env.containsKey("SHELF_DB") ? Path.of(env.get("SHELF_DB")) : d.dbFile,
            intOr(env, "SHELF_DB_POOL", d.poolSize),
            intOr(env, "SHELF_DB_BUSY_MS", d.busyTimeoutMs),
            intOr(env, "SHELF_CACHE_SIZE", d.cacheSize),
            env.containsKey("SHELF_CACHE_TTL_S") ? Duration.ofSeconds(Long.parseLong(env.get("SHELF_CACHE_TTL_S"))) : d.cacheTtl,
            env.containsKey("SHELF_RATE_BURST") ? Double.parseDouble(env.get("SHELF_RATE_BURST")) : d.rateCapacity,
            env.containsKey("SHELF_RATE_PER_S") ? Double.parseDouble(env.get("SHELF_RATE_PER_S")) : d.ratePerSecond,
            intOr(env, "SHELF_DRAIN_S", d.drainSeconds));
    }

    private static int intOr(Map<String, String> env, String name, int dflt) {
        return env.containsKey(name) ? Integer.parseInt(env.get(name)) : dflt;
    }

    // Records have no copy(); these "withers" return a changed copy (L7).
    public Config withPort(int p) { return new Config(p, dbFile, poolSize, busyTimeoutMs, cacheSize, cacheTtl, rateCapacity, ratePerSecond, drainSeconds); }
    public Config withDbFile(Path f) { return new Config(port, f, poolSize, busyTimeoutMs, cacheSize, cacheTtl, rateCapacity, ratePerSecond, drainSeconds); }
    public Config withPool(int n) { return new Config(port, dbFile, n, busyTimeoutMs, cacheSize, cacheTtl, rateCapacity, ratePerSecond, drainSeconds); }
    public Config withCache(int n) { return new Config(port, dbFile, poolSize, busyTimeoutMs, n, cacheTtl, rateCapacity, ratePerSecond, drainSeconds); }
    public Config withRate(double burst, double perS) { return new Config(port, dbFile, poolSize, busyTimeoutMs, cacheSize, cacheTtl, burst, perS, drainSeconds); }
    public Config withDrain(int s) { return new Config(port, dbFile, poolSize, busyTimeoutMs, cacheSize, cacheTtl, rateCapacity, ratePerSecond, s); }
}
