import java.sql.Connection
import java.sql.DriverManager

// Runs block inside one transaction: commit if it returns, roll back if it throws.
inline fun <T> Connection.transaction(block: (Connection) -> T): T {
    autoCommit = false
    try {
        val result = block(this)
        commit()
        return result
    } catch (e: Throwable) {
        rollback()
        throw e
    } finally {
        autoCommit = true
    }
}

fun Connection.exec(sql: String) = createStatement().use { it.executeUpdate(sql) }

fun Connection.balances(): String =
    createStatement().use { st ->
        st.executeQuery("SELECT owner, balance_cents FROM accounts ORDER BY id").use { rs ->
            buildList { while (rs.next()) add("${rs.getString(1)}=${rs.getInt(2)}") }.joinToString(" ")
        }
    }

fun reset(c: Connection) {
    c.exec("DROP TABLE IF EXISTS accounts")
    c.exec("CREATE TABLE accounts (id INTEGER PRIMARY KEY, owner TEXT, " +
           "balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0))")
    c.exec("INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)")
}

fun transfer(c: Connection, from: Int, to: Int, cents: Int, crash: Boolean = false) {
    c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?").use {
        it.setInt(1, cents); it.setInt(2, to); it.executeUpdate()
    }
    if (crash) error("power cut between the two UPDATEs")
    c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents - ? WHERE id = ?").use {
        it.setInt(1, cents); it.setInt(2, from); it.executeUpdate()
    }
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        println("autocommit by default: ${c.autoCommit}")

        reset(c)
        try { transfer(c, 1, 2, 2500, crash = true) } catch (e: IllegalStateException) { println("1. crashed: ${e.message}") }
        println("   autocommit:  ${c.balances()}")

        reset(c)
        try { c.transaction { transfer(it, 1, 2, 2500, crash = true) } } catch (e: IllegalStateException) { println("2. crashed: ${e.message}") }
        println("   transaction: ${c.balances()}")

        reset(c)
        try { c.transaction { transfer(it, 1, 2, 15000) } } catch (e: java.sql.SQLException) { println("3. ${e.message}") }
        println("   transaction: ${c.balances()}")

        c.transaction { transfer(it, 1, 2, 2500) }
        println("4. committed:   ${c.balances()}")
    }
}
