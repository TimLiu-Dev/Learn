abstract class Account(val owner: String, balance: Long) {   // shared state + constructor
    var balance: Long = balance
        protected set                                         // subclasses may change it

    abstract fun monthlyFee(): Long                           // each kind of account decides

    fun applyFee() {                                          // shared logic using the abstract part
        balance -= monthlyFee()
    }

    override fun toString() = "$owner: $balance"
}

class Checking(owner: String, balance: Long) : Account(owner, balance) {
    override fun monthlyFee() = if (balance < 1000) 5L else 0L
}

class Savings(owner: String, balance: Long) : Account(owner, balance) {
    override fun monthlyFee() = 0L
}

fun main() {
    val accounts: List<Account> = listOf(Checking("Ada", 500), Checking("Grace", 5000), Savings("Linus", 2000))
    for (a in accounts) a.applyFee()
    println(accounts)
}
