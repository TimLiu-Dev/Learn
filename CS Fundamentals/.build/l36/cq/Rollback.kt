// A stand-in for a JDBC Connection that only records what happens to it.
class FakeConnection {
    val log = mutableListOf<String>()
    var autoCommit = true
        set(value) { log += "auto=$value"; field = value }
    fun commit() { log += "commit" }
    fun rollback() { log += "rollback" }
}

// Part 1's helper, unchanged except for the receiver type.
inline fun <T> FakeConnection.transaction(block: (FakeConnection) -> T): T {
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

fun main() {
    val c = FakeConnection()
    try {
        c.transaction {
            it.log += "debit"
            val newBalance = 10000 - 15000
            check(newBalance >= 0) { "overdraft" }
        }
    } catch (e: IllegalStateException) {
        c.log += "caught ${e.message}"
    }
    println(c.log.joinToString(" "))
}
