class OverdraftException(val requested: Long, val available: Long) :
    Exception("cannot withdraw $requested, only $available available")

class Account(private var balance: Long) {
    fun withdraw(amount: Long) {
        require(amount > 0) { "amount must be positive, was $amount" }   // caller's bug
        if (amount > balance) throw OverdraftException(amount, balance)  // expected, recoverable
        balance -= amount
    }

    fun balance() = balance
}

fun main() {
    val acct = Account(1_000)
    for (amount in listOf(400L, 900L, -5L)) {
        try {
            acct.withdraw(amount)
            println("withdrew $amount, balance ${acct.balance()}")
        } catch (e: OverdraftException) {
            println("declined: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("bug in caller: ${e.message}")
        }
    }
}
