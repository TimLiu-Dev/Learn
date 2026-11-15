// One row of accounts. Two transactions, driven step by step on one thread.
class Row(var balance: Int, var version: Int)

// UPDATE accounts SET balance_cents = ?, version = version + 1 WHERE id = 1 AND version = ?
fun Row.updateIfVersion(newBalance: Int, expected: Int): Int {
    if (version != expected) return 0
    balance = newBalance
    version++
    return 1
}

fun main() {
    val row = Row(balance = 100, version = 0)
    val balance = row.balance                               // T1: SELECT balance_cents
    row.updateIfVersion(row.balance + 50, row.version)      // T2: deposits 50, commits
    val version = row.version                               // T1: SELECT version
    val count = row.updateIfVersion(balance + 10, version)  // T1: deposits 10
    println("$count ${row.balance}")
}
