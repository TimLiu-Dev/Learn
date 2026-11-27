package shelf.store

import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.concurrent.ArrayBlockingQueue

class Db(file: Path, poolSize: Int = 0, busyTimeoutMs: Int = 3000) : AutoCloseable {
    // IMMEDIATE: a transaction takes SQLite's write lock at BEGIN, so writers queue
    // instead of failing with SQLITE_BUSY halfway through (L36).
    // busy_timeout: how long a queued writer waits for that lock before giving up with SQLITE_BUSY.
    private val url = "jdbc:sqlite:$file?transaction_mode=IMMEDIATE&busy_timeout=$busyTimeoutMs"

    // poolSize 0 = a fresh connection per call (v1). Otherwise a fixed set of open connections,
    // handed out through a BlockingQueue (L27): take() waits when all are busy, so it also caps concurrency.
    private val pool: ArrayBlockingQueue<Connection>? =
        if (poolSize > 0) ArrayBlockingQueue<Connection>(poolSize).also { q -> repeat(poolSize) { q.add(connect()) } }
        else null

    fun connect(): Connection = DriverManager.getConnection(url).also { c ->
        c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }   // off by default (L34)
    }

    private fun borrow(): Connection = pool?.take() ?: connect()

    private fun release(c: Connection) {
        if (pool == null) return c.close()
        if (!c.autoCommit) c.autoCommit = true          // hand it back clean
        pool.put(c)
    }

    fun <T> read(block: (Connection) -> T): T {
        val c = borrow()
        try { return block(c) } finally { release(c) }
    }

    fun <T> transaction(block: (Connection) -> T): T {
        val c = borrow()
        try {
            c.autoCommit = false
            try {
                return block(c).also { c.commit() }
            } catch (e: Throwable) {
                // If BEGIN itself failed (e.g. SQLITE_BUSY), rollback() throws "no transaction is active"
                // and would hide the real error. Keep the real one; attach the other as suppressed (L10).
                runCatching { c.rollback() }.onFailure(e::addSuppressed)
                throw e
            }
        } finally {
            release(c)
        }
    }

    /** For /health: can we still run a query? */
    fun ping(): Boolean = runCatching { read { c -> c.query("SELECT 1") { it.getInt(1) } == listOf(1) } }.getOrDefault(false)

    /** For /metrics: idle pooled connections (null without a pool). */
    val idleConnections: Int? get() = pool?.size

    override fun close() {
        pool?.forEach { it.close() }
    }

    /** Brings the schema up to date: applies Shelf's pending migrations, in order (L59). */
    fun migrate(): List<Migration> = read { c -> Migrator.migrate(c, SHELF_MIGRATIONS) }
}

// Small JDBC helpers: every value goes through a ? placeholder, never string concatenation (L33).
fun <T> Connection.query(sql: String, vararg args: Any?, map: (ResultSet) -> T): List<T> =
    prepareStatement(sql).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeQuery().use { rs -> buildList { while (rs.next()) add(map(rs)) } }
    }

fun Connection.update(sql: String, vararg args: Any?): Int =
    prepareStatement(sql).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeUpdate()
    }

fun Connection.insert(sql: String, vararg args: Any?): Long =
    prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeUpdate()
        ps.generatedKeys.use { it.next(); it.getLong(1) }
    }
