import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

fun Connection.sql(s: String) = createStatement().use { it.executeUpdate(s) }
fun Connection.balance(): Int = createStatement().use { st ->
    st.executeQuery("SELECT balance_cents FROM accounts WHERE id = 1").use { it.next(); it.getInt(1) }
}

fun main() {
    val file = Files.createTempFile("bank", ".db")
    val url = "jdbc:sqlite:$file"
    try {
        DriverManager.getConnection(url).use { a ->
            DriverManager.getConnection(url).use { b ->
                a.sql("CREATE TABLE accounts (id INTEGER PRIMARY KEY, balance_cents INTEGER NOT NULL)")
                a.sql("INSERT INTO accounts VALUES (1, 10000)")
                b.sql("PRAGMA busy_timeout = 200")              // wait at most 0.2 s for the write lock

                a.autoCommit = false
                a.sql("UPDATE accounts SET balance_cents = 9000 WHERE id = 1")   // a now holds the write lock
                println("b reads while a writes: ${b.balance()}")              // sees the committed 10000
                val t0 = System.nanoTime()
                try {
                    b.sql("UPDATE accounts SET balance_cents = 8000 WHERE id = 1")
                } catch (e: SQLException) {
                    println("b writes after ${(System.nanoTime() - t0) / 1_000_000} ms: ${e.message}")
                }
                a.commit()
                b.sql("UPDATE accounts SET balance_cents = balance_cents - 1000 WHERE id = 1")
                println("after a commits, b's write works: ${b.balance()}")
            }
        }
    } finally {
        Files.deleteIfExists(file)
    }
}
