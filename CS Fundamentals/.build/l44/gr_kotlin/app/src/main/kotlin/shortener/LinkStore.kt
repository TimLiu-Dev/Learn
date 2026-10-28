package shortener

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicLong

data class Link(val code: String, val url: String, val expiresAt: Long?)

class AliasTakenException(alias: String) : RuntimeException("alias '$alias' is taken")

/** SQLite storage (L34-L36). One connection per call, like L36. */
class LinkStore(dbFile: String, private val simulatedLatencyMs: Long = 0) {
    private val url = "jdbc:sqlite:$dbFile?transaction_mode=IMMEDIATE&busy_timeout=5000&journal_mode=WAL"
    val queries = AtomicLong()

    init {
        connect().use { c ->
            c.createStatement().use { st ->
                st.executeUpdate("""CREATE TABLE IF NOT EXISTS links (
                    id INTEGER PRIMARY KEY, code TEXT NOT NULL UNIQUE, url TEXT NOT NULL,
                    created_at INTEGER NOT NULL, expires_at INTEGER)""")
                st.executeUpdate("""CREATE TABLE IF NOT EXISTS clicks_daily (
                    code TEXT NOT NULL, day TEXT NOT NULL, clicks INTEGER NOT NULL,
                    PRIMARY KEY (code, day)) WITHOUT ROWID""")
            }
        }
    }

    private fun connect(): Connection {
        queries.incrementAndGet()
        if (simulatedLatencyMs > 0) Thread.sleep(simulatedLatencyMs)   // pretend the DB is across a network
        return DriverManager.getConnection(url)
    }

    /** Random 7-char code, retrying on the UNIQUE constraint; or the caller's alias. */
    fun create(url: String, alias: String?, now: Long, expiresAt: Long?): Link {
        repeat(5) {
            val code = alias ?: Base62.random(7)
            try {
                connect().use { c ->
                    c.prepareStatement("INSERT INTO links(code, url, created_at, expires_at) VALUES (?, ?, ?, ?)").use {
                        it.setString(1, code); it.setString(2, url); it.setLong(3, now)
                        if (expiresAt == null) it.setNull(4, java.sql.Types.INTEGER) else it.setLong(4, expiresAt)
                        it.executeUpdate()
                    }
                }
                return Link(code, url, expiresAt)
            } catch (e: SQLException) {
                if (e.message?.contains("UNIQUE") != true) throw e
                if (alias != null) throw AliasTakenException(alias)   // retrying won't help
            }
        }
        error("5 random codes in a row were taken")
    }

    fun find(code: String): Link? = connect().use { c ->
        c.prepareStatement("SELECT url, expires_at FROM links WHERE code = ?").use { ps ->
            ps.setString(1, code)
            ps.executeQuery().use { rs ->
                if (!rs.next()) null
                else Link(code, rs.getString(1), rs.getLong(2).takeUnless { rs.wasNull() })   // L36: wasNull
            }
        }
    }

    /** One transaction for a whole batch of clicks (L36 batching). */
    fun addClicks(day: String, counts: Map<String, Int>) {
        connect().use { c ->
            c.autoCommit = false
            c.prepareStatement("""INSERT INTO clicks_daily VALUES (?, ?, ?)
                ON CONFLICT(code, day) DO UPDATE SET clicks = clicks + excluded.clicks""").use { ps ->
                for ((code, n) in counts) {
                    ps.setString(1, code); ps.setString(2, day); ps.setInt(3, n); ps.addBatch()
                }
                ps.executeBatch()
            }
            c.commit()
        }
    }

    fun clicks(code: String): Long = connect().use { c ->
        c.prepareStatement("SELECT coalesce(sum(clicks), 0) FROM clicks_daily WHERE code = ?").use { ps ->
            ps.setString(1, code)
            ps.executeQuery().use { rs -> rs.next(); rs.getLong(1) }
        }
    }
}
