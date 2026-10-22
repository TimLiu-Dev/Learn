import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

inline fun <T> Connection.transaction(block: (Connection) -> T): T {
    autoCommit = false
    try { return block(this).also { commit() } }
    catch (e: Throwable) { rollback(); throw e }
    finally { autoCommit = true }
}

sealed interface TransferResult {
    data object Done : TransferResult
    data class Rejected(val reason: String) : TransferResult
}

fun Connection.move(id: Int, delta: Int): Int =
    prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?").use {
        it.setInt(1, delta); it.setInt(2, id); it.executeUpdate()   // number of rows changed
    }

fun transfer(c: Connection, from: Int, to: Int, cents: Int): TransferResult =
    try {
        c.transaction {
            require(cents > 0) { "amount must be positive" }
            check(it.move(to, cents) == 1) { "no account $to" }
            check(it.move(from, -cents) == 1) { "no account $from" }
            TransferResult.Done
        }
    } catch (e: IllegalArgumentException) { TransferResult.Rejected(e.message!!) }
      catch (e: IllegalStateException) { TransferResult.Rejected(e.message!!) }
      catch (e: SQLException) { TransferResult.Rejected("overdraft") }

fun Connection.balances(): String = createStatement().use { st ->
    st.executeQuery("SELECT owner, balance_cents FROM accounts ORDER BY id").use { rs ->
        buildList { while (rs.next()) add("${rs.getString(1)}=${rs.getInt(2)}") }.joinToString(" ")
    }
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        c.createStatement().use {
            it.executeUpdate("CREATE TABLE accounts (id INTEGER PRIMARY KEY, owner TEXT, balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0))")
            it.executeUpdate("INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)")
        }
        for ((from, to, cents) in listOf(Triple(1, 2, 2500), Triple(1, 99, 1000), Triple(99, 2, 1000),
                                         Triple(2, 1, 99999), Triple(1, 2, -5))) {
            println("$from -> $to  $cents: ${transfer(c, from, to, cents)}  | ${c.balances()}")
        }
    }
}
