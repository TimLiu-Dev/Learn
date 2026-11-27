package shelf

import java.nio.file.Path
import java.time.Duration

/**
 * Everything that differs between your laptop and a server, read from environment variables
 * (no secrets in code, L33). A setting of 0 switches that feature off.
 */
data class Config(
    val port: Int = 8080,
    val dbFile: Path = Path.of("shelf.db"),
    val poolSize: Int = 8,                         // 0: a new connection per call (v1)
    val busyTimeoutMs: Int = 3000,
    val cacheSize: Int = 1000,                     // 0: no cache
    val cacheTtl: Duration = Duration.ofSeconds(30),
    val rateCapacity: Double = 100.0,              // 0: no rate limit
    val ratePerSecond: Double = 50.0,
    val drainSeconds: Int = 5,                     // graceful shutdown: max wait for in-flight requests
) {
    companion object {
        fun fromEnv(env: Map<String, String> = System.getenv()): Config {
            val d = Config()
            fun int(name: String, default: Int) = env[name]?.toInt() ?: default
            fun dbl(name: String, default: Double) = env[name]?.toDouble() ?: default
            return Config(
                port = int("SHELF_PORT", d.port),
                dbFile = env["SHELF_DB"]?.let(Path::of) ?: d.dbFile,
                poolSize = int("SHELF_DB_POOL", d.poolSize),
                busyTimeoutMs = int("SHELF_DB_BUSY_MS", d.busyTimeoutMs),
                cacheSize = int("SHELF_CACHE_SIZE", d.cacheSize),
                cacheTtl = env["SHELF_CACHE_TTL_S"]?.let { Duration.ofSeconds(it.toLong()) } ?: d.cacheTtl,
                rateCapacity = dbl("SHELF_RATE_BURST", d.rateCapacity),
                ratePerSecond = dbl("SHELF_RATE_PER_S", d.ratePerSecond),
                drainSeconds = int("SHELF_DRAIN_S", d.drainSeconds),
            )
        }
    }
}
