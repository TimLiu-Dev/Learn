import java.sql.Connection
import java.sql.DriverManager

// Exercise: let the database check the fencing token.
// A nightly job rewrites report #1. Each run gets a token from the lock service.

fun reset(db: Connection) = db.createStatement().use {
    it.executeUpdate("DROP TABLE IF EXISTS report")
    it.executeUpdate("CREATE TABLE report(id INTEGER PRIMARY KEY, body TEXT NOT NULL, fence INTEGER NOT NULL)")
    it.executeUpdate("INSERT INTO report VALUES (1, 'v0', 0)")
}

fun body(db: Connection): String =
    db.createStatement().use { it.executeQuery("SELECT body FROM report WHERE id = 1").run { next(); getString(1) } }

// Two statements: read the fence, then write. 'pause' runs in between.
fun checkThenWrite(db: Connection, body: String, token: Long, pause: () -> Unit = {}): Boolean {
    val fence = db.createStatement().use { it.executeQuery("SELECT fence FROM report WHERE id = 1").run { next(); getLong(1) } }
    if (token < fence) return false
    pause()
    db.prepareStatement("UPDATE report SET body = ?, fence = ? WHERE id = 1").use {
        it.setString(1, body); it.setLong(2, token); it.executeUpdate()
    }
    return true
}

// One statement: the check and the write are a single atomic step inside the database.
fun fencedWrite(db: Connection, body: String, token: Long): Boolean =
    db.prepareStatement("UPDATE report SET body = ?, fence = ? WHERE id = 1 AND fence <= ?").use {
        it.setString(1, body); it.setLong(2, token); it.setLong(3, token)
        it.executeUpdate() == 1                   // 0 rows: a newer token got there first
    }

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { db ->
        reset(db)
        println("check, then write:")
        val ok1 = checkThenWrite(db, "client1 (stale)", 33) {
            println("  client1 passed the check, then paused")
            println("  client2 writes: " + checkThenWrite(db, "client2", 34))
        }
        println("  client1 writes: $ok1")
        println("  report #1 = ${body(db)}")

        reset(db)
        println("one conditional UPDATE:")
        println("  client2 writes: " + fencedWrite(db, "client2", 34))
        println("  client1 writes: " + fencedWrite(db, "client1 (stale)", 33))
        println("  report #1 = ${body(db)}")
    }
}
