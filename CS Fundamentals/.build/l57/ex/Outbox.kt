import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager

// Exercise: the transactional outbox, on a real SQLite file.
// The order row and its "OrderPlaced" message commit in ONE local transaction;
// a relay publishes messages afterwards, and the consumer ignores duplicates.

class RelayCrash : RuntimeException("relay crashed")

fun placeOrder(db: Connection, item: String) {
    db.autoCommit = false
    db.prepareStatement("INSERT INTO orders(item) VALUES (?)").use { it.setString(1, item); it.executeUpdate() }
    val id = db.createStatement().use { it.executeQuery("SELECT last_insert_rowid()").run { next(); getLong(1) } }
    db.prepareStatement("INSERT INTO outbox(payload) VALUES (?)").use { it.setString(1, "OrderPlaced #$id $item"); it.executeUpdate() }
    db.commit()                                       // both rows, or neither
    db.autoCommit = true
}

fun relay(db: Connection, broker: MutableList<Pair<Long, String>>, crashAfterPublishing: Long? = null) {
    val pending = db.createStatement().use { st ->
        st.executeQuery("SELECT id, payload FROM outbox WHERE sent = 0 ORDER BY id").use { rs ->
            buildList { while (rs.next()) add(rs.getLong(1) to rs.getString(2)) }
        }
    }
    for ((id, payload) in pending) {
        broker += id to payload                       // 1. publish
        println("  relay published outbox #$id")
        if (id == crashAfterPublishing) throw RelayCrash()
        db.prepareStatement("UPDATE outbox SET sent = 1 WHERE id = ?").use { it.setLong(1, id); it.executeUpdate() }   // 2. mark sent
    }
}

class EmailConsumer {
    private val seen = mutableSetOf<Long>()           // in production: a processed_messages table
    var emails = 0
    fun handle(id: Long, payload: String) {
        if (!seen.add(id)) { println("  consumer: #$id again, already handled, skip"); return }
        emails++
        println("  consumer: email for \"$payload\"")
    }
}

fun main() {
    val file = Files.createTempFile("outbox", ".db")
    DriverManager.getConnection("jdbc:sqlite:$file").use { db ->
        db.createStatement().use {
            it.executeUpdate("CREATE TABLE orders(id INTEGER PRIMARY KEY, item TEXT NOT NULL)")
            it.executeUpdate("CREATE TABLE outbox(id INTEGER PRIMARY KEY, payload TEXT NOT NULL, sent INTEGER NOT NULL DEFAULT 0)")
        }
        listOf("lamp", "desk", "chair").forEach { placeOrder(db, it) }
        val broker = mutableListOf<Pair<Long, String>>()
        println("== relay run 1 crashes after publishing #2, before marking it sent")
        try { relay(db, broker, crashAfterPublishing = 2) } catch (e: RelayCrash) { println("  ${e.message}") }
        println("== relay run 2, after a restart")
        relay(db, broker)
        println("== the consumer reads the broker: ${broker.size} messages for 3 orders")
        val consumer = EmailConsumer()
        broker.forEach { (id, p) -> consumer.handle(id, p) }
        println("emails sent: ${consumer.emails}")
    }
    Files.delete(file)
}
