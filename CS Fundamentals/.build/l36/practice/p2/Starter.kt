// One row of the accounts table: its balance and its version number.
class Account(var balance: Int, var version: Int = 0) {
    // UPDATE accounts SET balance_cents = ?, version = version + 1 WHERE id = 1 AND version = ?
    // Returns the update count, like executeUpdate: 1 if the row matched, 0 if it didn't.
    fun updateIfVersion(newBalance: Int, expectedVersion: Int): Int =
        TODO("change the row only if its version is still expectedVersion")
}

// A deposit transaction: read the row, then write balance + amount.
class Deposit(val amount: Int) {
    private var seenBalance = 0
    private var seenVersion = 0

    fun read(acc: Account) {
        seenBalance = acc.balance
        seenVersion = acc.version
    }

    // The lost-update bug: writes what it computed, whatever happened since the read.
    fun writeBlindly(acc: Account): Boolean {
        acc.balance = seenBalance + amount
        return true
    }

    // Optimistic locking: true if the write went through, false if someone got there first.
    fun writeIfUnchanged(acc: Account): Boolean =
        TODO("use updateIfVersion with what you saw at read time")
}

// Runs steps like "T1 read" and "T2 write" in exactly this order, on one thread.
// Returns how many writes had to be retried.
fun runSchedule(acc: Account, txs: Map<String, Deposit>, steps: List<String>, optimistic: Boolean): Int {
    val queue = ArrayDeque(steps)
    var retries = 0
    while (queue.isNotEmpty()) {
        val (name, action) = queue.removeFirst().split(" ")
        val tx = txs.getValue(name)
        when (action) {
            "read" -> tx.read(acc)
            "write" -> {
                val ok = if (optimistic) tx.writeIfUnchanged(acc) else tx.writeBlindly(acc)
                // TODO: a failed write must not be lost. Count it, and schedule this
                // transaction to read again and write again, after everything else.
            }
        }
    }
    return retries
}
