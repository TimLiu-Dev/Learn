// Every lock is taken through locked(), so the checks can watch the order.
var onLock: (Account) -> Unit = {}

class Account(val id: Int, var balance: Long) {
    fun <T> locked(body: () -> T): T = synchronized(this) {
        onLock(this)
        body()
    }
}

fun transfer(from: Account, to: Account, amount: Long): Boolean {
    val (first, second) = if (from.id < to.id) from to to else to to from
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

// Right order, but one lock at a time: each balance is read safely, the total isn't consistent.
fun audit(accounts: List<Account>): Long =
    accounts.sortedBy { it.id }.sumOf { a -> a.locked { a.balance } }
