package paste

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicLong
import shortener.Base62
import shortener.LruCache
import shortener.RateLimiter

/** A clock you can move by hand: demos and tests (L38). */
class SettableClock(var now: Instant) : Clock() {
    override fun instant() = now
    override fun getZone(): ZoneId = ZoneOffset.UTC
    override fun withZone(zone: ZoneId?) = this
}

class PasteStore(dbFile: String) {
    private val url = "jdbc:sqlite:$dbFile?busy_timeout=5000&journal_mode=WAL"
    val queries = AtomicLong()

    init {
        connect().use { c ->
            c.createStatement().use {
                it.executeUpdate("""CREATE TABLE IF NOT EXISTS pastes (
                    id TEXT PRIMARY KEY, content TEXT NOT NULL,
                    created_at INTEGER NOT NULL, expires_at INTEGER)""")
                it.executeUpdate("CREATE INDEX IF NOT EXISTS pastes_expiring ON pastes(expires_at) WHERE expires_at IS NOT NULL")
            }
        }
    }

    private fun connect(): Connection = DriverManager.getConnection(url).also { queries.incrementAndGet() }

    /** false if the id is taken (the caller picks another). */
    fun insert(id: String, content: String, now: Long, expiresAt: Long?): Boolean = try {
        connect().use { c ->
            c.prepareStatement("INSERT INTO pastes VALUES (?, ?, ?, ?)").use {
                it.setString(1, id); it.setString(2, content); it.setLong(3, now)
                if (expiresAt == null) it.setNull(4, java.sql.Types.INTEGER) else it.setLong(4, expiresAt)
                it.executeUpdate()
            }
        }
        true
    } catch (e: SQLException) {
        if (e.message?.contains("UNIQUE") == true) false else throw e
    }

    fun find(id: String): Pair<String, Long?>? = connect().use { c ->
        c.prepareStatement("SELECT content, expires_at FROM pastes WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { rs ->
                if (!rs.next()) null else rs.getString(1) to rs.getLong(2).takeUnless { rs.wasNull() }
            }
        }
    }

    fun deleteExpired(now: Long): Int = connect().use { c ->
        c.prepareStatement("DELETE FROM pastes WHERE expires_at <= ?").use { it.setLong(1, now); it.executeUpdate() }
    }
}

sealed interface Created {
    data class Ok(val id: String) : Created
    data class TooLarge(val limitBytes: Int) : Created
    data class RateLimited(val retryAfterSeconds: Long) : Created
}

sealed interface Read {
    data class Found(val content: String) : Read
    data object Expired : Read
    data object NotFound : Read
}

class PasteService(
    private val store: PasteStore,
    private val clock: Clock,
    cacheSize: Int = 1_000,
    private val maxBytes: Int = 64 * 1024,
) {
    // 5 pastes in a burst, then one every 12 s; the bucket runs on the injected clock
    private val limiter = RateLimiter(5.0, 5 / 60.0) { clock.millis() * 1_000_000 }
    val cache = LruCache<String, Pair<String, Long?>>(cacheSize)

    fun create(client: String, content: String, ttl: Duration?): Created {
        val wait = limiter.tryAcquire(client)
        if (wait > 0) return Created.RateLimited(wait)
        if (content.toByteArray().size > maxBytes) return Created.TooLarge(maxBytes)
        val now = clock.millis()
        repeat(5) {
            val id = Base62.random(8)                         // unguessable: pastes may be private-ish
            if (store.insert(id, content, now, ttl?.let { now + it.toMillis() })) return Created.Ok(id)
        }
        error("5 random ids in a row were taken")
    }

    fun read(id: String): Read {
        val (content, expiresAt) = cache.get(id) ?: store.find(id)?.also { cache.put(id, it) } ?: return Read.NotFound
        if (expiresAt != null && expiresAt <= clock.millis()) {
            cache.remove(id)
            return Read.Expired
        }
        return Read.Found(content)
    }

    /** The cleanup job the design doc schedules hourly. */
    fun purgeExpired(): Int = store.deleteExpired(clock.millis())
}
