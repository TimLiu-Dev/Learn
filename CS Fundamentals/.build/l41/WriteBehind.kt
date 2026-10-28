import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import java.util.TreeMap
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

// Write-behind: writes land in the cache at once and reach the database later,
// in batches. Run "write" (which crashes on purpose), then "check".
val DB: Path = Path.of(System.getProperty("java.io.tmpdir"), "l41-writebehind.db")

fun main(args: Array<String>) {
    if (args[0] == "check") { check(); return }
    Files.deleteIfExists(DB)
    DriverManager.getConnection("jdbc:sqlite:$DB").use { c ->
        c.createStatement().use { s ->
            s.execute("CREATE TABLE stock(sku TEXT PRIMARY KEY, qty INTEGER NOT NULL)")
            s.execute("INSERT INTO stock VALUES ('dune', 10), ('emma', 10), ('ulysses', 10)")
        }
    }
    val cache = ConcurrentHashMap(mapOf("dune" to 10, "emma" to 10, "ulysses" to 10))
    val dirty = LinkedBlockingQueue<String>()               // keys waiting to be written

    // The flusher: every 500 ms, write all dirty keys to the database in one transaction
    val flusher = Executors.newSingleThreadScheduledExecutor()
    flusher.scheduleAtFixedRate({
        val batch = mutableListOf<String>()
        dirty.drainTo(batch)
        if (batch.isNotEmpty()) {
            DriverManager.getConnection("jdbc:sqlite:$DB").use { c ->
                c.prepareStatement("UPDATE stock SET qty = ? WHERE sku = ?").use { u ->
                    c.autoCommit = false
                    for (sku in batch.toSet()) { u.setInt(1, cache.getValue(sku)); u.setString(2, sku); u.executeUpdate() }
                    c.commit()
                }
            }
            println("  flushed ${batch.size} writes")
        }
    }, 500, 500, TimeUnit.MILLISECONDS)

    // Writes are instant: cache first, database "later"
    sell(cache, dirty, "dune", 3); sell(cache, dirty, "emma", 1)
    Thread.sleep(700)                                       // one flush happens
    sell(cache, dirty, "dune", 2); sell(cache, dirty, "ulysses", 4); sell(cache, dirty, "emma", 5)
    println("cache says: ${TreeMap(cache)}, queued: ${dirty.size}")
    println("power cut!")
    Runtime.getRuntime().halt(1)                            // no shutdown hooks, no final flush
}

fun sell(cache: MutableMap<String, Int>, dirty: BlockingQueue<String>, sku: String, n: Int) {
    cache.merge(sku, -n, Int::plus)
    dirty.add(sku)
}

fun check() {
    val rows = TreeMap<String, Int>()
    DriverManager.getConnection("jdbc:sqlite:$DB").use { c ->
        c.createStatement().executeQuery("SELECT sku, qty FROM stock").use { rs ->
            while (rs.next()) rows[rs.getString(1)] = rs.getInt(2)
        }
    }
    println("database says: $rows")
}
