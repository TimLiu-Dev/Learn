package shelf

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import shelf.store.Db
import shelf.store.Migration
import shelf.store.MigrationException
import shelf.store.Migrator
import shelf.store.SHELF_MIGRATIONS
import shelf.store.query

// v2.1: numbered migrations instead of CREATE IF NOT EXISTS, and separate liveness and readiness probes.
class OpsTest {
    private val file = Files.createTempFile("shelf-ops", ".db").also { Files.delete(it) }

    @Test
    fun `migrations run once, in order, and are recorded`() {
        Db(file).use { db ->
            assertEquals(listOf(1, 2), db.migrate().map { it.version })
            assertEquals(emptyList(), db.migrate())                               // every restart runs it again
            val recorded = db.read { c -> c.query("SELECT version, description FROM schema_version ORDER BY version") { "${it.getInt(1)} ${it.getString(2)}" } }
            assertEquals(listOf("1 initial schema", "2 books by author"), recorded)
        }
        Files.deleteIfExists(file)
    }

    @Test
    fun `an edited migration stops the app instead of running`() {
        Db(file).use { db ->
            db.migrate()
            val edited = SHELF_MIGRATIONS.map { if (it.version == 2) Migration(2, it.description, it.sql.replace("author_id", "author_id, id")) else it }
            val e = assertFailsWith<MigrationException> { db.read { c -> Migrator.migrate(c, edited) } }
            assertTrue("was edited after it ran" in e.message!!, e.message)
        }
        Files.deleteIfExists(file)
    }

    @Test
    fun `a database made before migrations is adopted, data intact`() {
        Db(file).use { db ->
            db.transaction { c ->                                              // what v2's createSchema left behind
                c.createStatement().use { st ->
                    st.executeUpdate("CREATE TABLE authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)")
                    st.executeUpdate("CREATE TABLE books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, author_id INTEGER NOT NULL REFERENCES authors(id), year INTEGER)")
                    st.executeUpdate("INSERT INTO authors VALUES (1, 'Frank Herbert')")
                    st.executeUpdate("INSERT INTO books VALUES (1, 'Dune', 1, 1965)")
                }
            }
            assertEquals(listOf(1, 2), db.migrate().map { it.version })
            assertEquals(listOf("Dune"), db.read { c -> c.query("SELECT title FROM books") { it.getString(1) } })
        }
        Files.deleteIfExists(file)
    }

    @Test
    fun `a dead database fails readiness, not liveness`() {
        val clock = Clock.fixed(Instant.parse("2026-11-26T10:00:00Z"), ZoneOffset.UTC)
        val shelf = Shelf(Config(port = 0, dbFile = file, poolSize = 0, drainSeconds = 0), clock, log = {}).start()
        val client = HttpClient.newHttpClient()
        fun status(path: String) = client.send(HttpRequest.newBuilder(URI("http://127.0.0.1:${shelf.port}$path")).build(),
            HttpResponse.BodyHandlers.ofString()).statusCode()
        try {
            assertEquals(200, status("/live"))
            assertEquals(200, status("/ready"))
            Files.delete(file)
            Files.createDirectory(file)                     // the next connection can't open the database
            assertEquals(503, status("/ready"))             // take me out of the load balancer...
            assertEquals(200, status("/live"))              // ...but don't restart me
        } finally {
            shelf.close()
            Files.deleteIfExists(file)
        }
    }
}
