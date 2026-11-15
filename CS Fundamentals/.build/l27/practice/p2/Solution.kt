// Every lock is taken through locked(), so the checks can watch the order.
var onLock: (Account) -> Unit = {}

class Account(val id: Int, var balance: Long) {
    fun <T> locked(body: () -> T): T = synchronized(this) {
        onLock(this)
        body()
    }
}

fun transfer(from: Account, to: Account, amount: Long): Boolean {
    val (first, second) = if (from.id < to.id) from to to else to to from   // one global order: by id
    return first.locked {
        second.locked {
            if (from.balance < amount) false
            else {
                from.balance -= amount
                to.balance += amount
                true
            }
        }
    }
}

fun audit(accounts: List<Account>): Long {
    val sorted = accounts.sortedBy { it.id }
    // Take lock i, and while holding it, take the rest; sum only when all are held.
    fun lockFrom(i: Int): Long =
        if (i == sorted.size) sorted.sumOf { it.balance }
        else sorted[i].locked { lockFrom(i + 1) }
    return lockFrom(0)
}
