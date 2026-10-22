package kv

import java.sql.Connection
import java.sql.DriverManager
import java.util.TreeMap

interface KvStore {
    fun put(key: String, value: String)
    fun get(key: String): String?
    fun delete(key: String): Boolean
    fun keys(): List<String>                    // sorted
}

class MemoryKvStore : KvStore {
    private val map = TreeMap<String, String>()
    override fun put(key: String, value: String) { map[key] = value }
    override fun get(key: String) = map[key]
    override fun delete(key: String) = map.remove(key) != null
    override fun keys() = map.keys.toList()
}

class SqliteKvStore(url: String) : KvStore, AutoCloseable {
    private val c: Connection = DriverManager.getConnection(url).also { c ->
        c.createStatement().use { it.executeUpdate("CREATE TABLE IF NOT EXISTS kv (k TEXT PRIMARY KEY, v TEXT NOT NULL)") }
    }

    override fun put(key: String, value: String) {
        c.prepareStatement("INSERT INTO kv (k, v) VALUES (?, ?) ON CONFLICT(k) DO UPDATE SET v = excluded.v").use { ps ->
            ps.setString(1, key); ps.setString(2, value); ps.executeUpdate()
        }
    }

    override fun get(key: String): String? =
        c.prepareStatement("SELECT v FROM kv WHERE k = ?").use { ps ->
            ps.setString(1, key)
            ps.executeQuery().use { if (it.next()) it.getString(1) else null }
        }

    override fun delete(key: String): Boolean =
        c.prepareStatement("DELETE FROM kv WHERE k = ?").use { ps -> ps.setString(1, key); ps.executeUpdate() == 1 }

    override fun keys(): List<String> =
        c.createStatement().use { st ->
            st.executeQuery("SELECT k FROM kv ORDER BY k").use { rs -> buildList { while (rs.next()) add(rs.getString(1)) } }
        }

    override fun close() = c.close()
}
