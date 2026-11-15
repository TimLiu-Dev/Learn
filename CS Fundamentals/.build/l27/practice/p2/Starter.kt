// Every lock is taken through locked(), so the checks can watch the order.
var onLock: (Account) -> Unit = {}

class Account(val id: Int, var balance: Long) {
    fun <T> locked(body: () -> T): T = synchronized(this) {
        onLock(this)
        body()
    }
}

// Deadlocks when two transfers run in opposite directions: each locks its own `from` first.
fun transfer(from: Account, to: Account, amount: Long): Boolean =
    from.locked {
        to.locked {
            if (from.balance < amount) false
            else {
                from.balance -= amount
                to.balance += amount
                true
            }
        }
    }

// Locks every account (lowest id first), then sums the balances while all are held.
fun audit(accounts: List<Account>): Long = TODO("sort by id, then nest locked() calls")
