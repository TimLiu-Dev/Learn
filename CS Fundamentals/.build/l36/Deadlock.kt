import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors

const val URL = "jdbc:h2:mem:dl;DB_CLOSE_DELAY=-1"

fun connect(): Connection = DriverManager.getConnection(URL).apply {
    createStatement().use { it.execute("SET LOCK_TIMEOUT 5000") }
    autoCommit = false
}

fun Connection.add(id: Int, cents: Int) =
    prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?").use {
        it.setInt(1, cents); it.setInt(2, id); it.executeUpdate()
    }

// Unordered: lock src then dst, and wait at the barrier so both threads hold their first row.
// Ordered: always lock the lower id first. (No barrier: the second thread waits on the row lock instead.)
fun transfer(src: Int, dst: Int, cents: Int, ordered: Boolean, barrier: CyclicBarrier): String =
    connect().use { c ->
        try {
            val first = if (!ordered || src < dst) src else dst
            val second = if (first == src) dst else src
            c.add(first, if (first == src) -cents else cents)
            if (!ordered) barrier.await()
            c.add(second, if (second == src) -cents else cents)
            c.commit()
            "$src->$dst committed"
        } catch (e: SQLException) {
            c.rollback()
            "$src->$dst [${e.sqlState}] ${e.message!!.substringBefore(" Details")}"
        }
    }

fun main() {
    DriverManager.getConnection(URL).use { c ->
        c.createStatement().use {
            it.execute("CREATE TABLE accounts (id INT PRIMARY KEY, balance_cents INT NOT NULL)")
            it.execute("INSERT INTO accounts VALUES (1, 10000), (2, 5000)")
        }
    }
    for (ordered in listOf(false, true)) {
        println(if (ordered) "== lock in id order" else "== lock in argument order")
        val barrier = CyclicBarrier(2)
        val start = System.nanoTime()
        Executors.newFixedThreadPool(2).use { pool ->
            val a = pool.submit<String> { transfer(1, 2, 100, ordered, barrier) }
            val b = pool.submit<String> { transfer(2, 1, 300, ordered, barrier) }
            println("  " + a.get()); println("  " + b.get())
        }
        println("  took ${(System.nanoTime() - start) / 1_000_000} ms")
    }
}
