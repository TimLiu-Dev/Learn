import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

const val URL = "jdbc:h2:mem:oncall;DB_CLOSE_DELAY=-1"

fun connect(): Connection = DriverManager.getConnection(URL).apply {
    createStatement().use { it.execute("SET LOCK_TIMEOUT 5000") }
    autoCommit = false
}

fun Connection.onCall(lock: Boolean): Int =
    createStatement().use { st ->
        // FOR UPDATE locks every row it returns until commit, so a second checker must wait its turn.
        st.executeQuery("SELECT name FROM doctors WHERE on_call" + if (lock) " FOR UPDATE" else "").use { rs ->
            var n = 0; while (rs.next()) n++; n
        }
    }

fun goOffCall(name: String, lock: Boolean, bothChecked: CountDownLatch): String = connect().use { c ->
    val seen = c.onCall(lock)
    bothChecked.countDown()
    if (!lock) bothChecked.await()          // unlocked run: make both check before either writes
    val result = if (seen >= 2) {
        c.prepareStatement("UPDATE doctors SET on_call = FALSE WHERE name = ?").use { it.setString(1, name); it.executeUpdate() }
        "$name saw $seen on call, went off"
    } else "$name saw $seen on call, stayed"
    c.commit()
    result
}

fun main() {
    for (lock in listOf(false, true)) {
        connect().use { c ->
            c.createStatement().use {
                it.execute("DROP TABLE IF EXISTS doctors")
                it.execute("CREATE TABLE doctors (name VARCHAR(20) PRIMARY KEY, on_call BOOLEAN)")
                it.execute("INSERT INTO doctors VALUES ('alice', TRUE), ('bob', TRUE)")
            }
            c.commit()
        }
        println(if (lock) "== with FOR UPDATE" else "== plain SELECT")
        val bothChecked = CountDownLatch(2)
        Executors.newFixedThreadPool(2).use { pool ->
            val a = pool.submit<String> { goOffCall("alice", lock, bothChecked) }
            val b = pool.submit<String> { goOffCall("bob", lock, bothChecked) }
            println("  " + a.get()); println("  " + b.get())
        }
        println("  on call now: " + connect().use { it.onCall(false) })
    }
}
