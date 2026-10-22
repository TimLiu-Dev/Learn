import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

const val URL = "jdbc:h2:mem:counter;DB_CLOSE_DELAY=-1"
const val THREADS = 4
const val DEPOSITS = 250                       // each thread adds 1 cent this many times: expect 1000

fun connect(): Connection = DriverManager.getConnection(URL).apply {
    createStatement().use { it.execute("SET LOCK_TIMEOUT 10000") }   // READ COMMITTED: H2's default
    autoCommit = false
}

fun Connection.int(sql: String, vararg args: Int): Int = prepareStatement(sql).use { ps ->
    args.forEachIndexed { i, a -> ps.setInt(i + 1, a) }
    ps.executeQuery().use { it.next(); it.getInt(1) }
}
fun Connection.update(sql: String, vararg args: Int): Int = prepareStatement(sql).use { ps ->
    args.forEachIndexed { i, a -> ps.setInt(i + 1, a) }
    ps.executeUpdate()
}

val retries = AtomicInteger()

// Four ways to add one cent. Each call is one transaction.
val strategies: Map<String, (Connection) -> Unit> = linkedMapOf(
    "read, then write" to { c ->
        val b = c.int("SELECT balance_cents FROM accounts WHERE id = 1")
        c.update("UPDATE accounts SET balance_cents = ? WHERE id = 1", b + 1)
        c.commit()
    },
    "atomic UPDATE" to { c ->
        c.update("UPDATE accounts SET balance_cents = balance_cents + 1 WHERE id = 1")
        c.commit()
    },
    "SELECT FOR UPDATE" to { c ->
        val b = c.int("SELECT balance_cents FROM accounts WHERE id = 1 FOR UPDATE")   // row locked until commit
        c.update("UPDATE accounts SET balance_cents = ? WHERE id = 1", b + 1)
        c.commit()
    },
    "optimistic version" to { c ->
        while (true) {
            val (b, v) = c.prepareStatement("SELECT balance_cents, version FROM accounts WHERE id = 1").use { ps ->
                ps.executeQuery().use { it.next(); it.getInt(1) to it.getInt(2) }   // ONE read: a consistent pair
            }
            val changed = c.update("UPDATE accounts SET balance_cents = ?, version = version + 1 " +
                                   "WHERE id = 1 AND version = ?", b + 1, v)
            c.commit()
            if (changed == 1) break                   // nobody else wrote since we read
            retries.incrementAndGet()                 // someone did: read again and retry
        }
    },
)

fun main() {
    for ((name, deposit) in strategies) {
        repeat(if (name == "read, then write") 3 else 1) {
            connect().use { c ->
                c.update("DROP TABLE IF EXISTS accounts")
                c.update("CREATE TABLE accounts (id INT PRIMARY KEY, balance_cents INT NOT NULL, version INT NOT NULL)")
                c.update("INSERT INTO accounts VALUES (1, 0, 0)"); c.commit()
            }
            retries.set(0)
            val gate = CountDownLatch(1)
            val start = System.nanoTime()
            Executors.newFixedThreadPool(THREADS).use { pool ->
                repeat(THREADS) {
                    pool.submit { connect().use { c -> gate.await(); repeat(DEPOSITS) { deposit(c) } } }
                }
                gate.countDown()
            }                                               // close() waits for every task
            val ms = (System.nanoTime() - start) / 1_000_000
            val total = connect().use { it.int("SELECT balance_cents FROM accounts WHERE id = 1") }
            println("%-19s balance %4d of %d  (%d ms%s)".format(name, total, THREADS * DEPOSITS, ms,
                if (name.startsWith("optimistic")) ", ${retries.get()} retries" else ""))
        }
    }
}
