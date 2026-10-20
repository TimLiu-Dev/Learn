class BankAccount private constructor(val owner: String, openingDeposit: Long) {
    var balance: Long = openingDeposit
        private set
    var transactionCount: Int = 0
        private set

    init {
        require(openingDeposit >= MIN_OPENING_DEPOSIT) {
            "opening deposit must be at least $MIN_OPENING_DEPOSIT"
        }
    }

    fun deposit(amount: Long) {
        require(amount > 0) { "deposit must be positive" }
        balance += amount
        transactionCount++
    }

    fun withdraw(amount: Long) {
        require(amount > 0) { "withdrawal must be positive" }
        require(amount <= balance) { "insufficient funds" }
        balance -= amount
        transactionCount++
    }

    companion object {
        const val MIN_OPENING_DEPOSIT = 1_000L    // cents
        const val OPENING_BONUS = 500L

        fun open(owner: String, deposit: Long): BankAccount = BankAccount(owner, deposit)

        fun withOpeningBonus(owner: String, deposit: Long): BankAccount =
            BankAccount(owner, deposit + OPENING_BONUS)
    }
}

fun report(a: BankAccount) = println("${a.owner}: balance=${a.balance} tx=${a.transactionCount}")

fun main() {
    val acct = BankAccount.withOpeningBonus("Ada", 2_000)
    report(acct)
    acct.deposit(1_000)
    acct.withdraw(700)
    report(acct)
    try {
        acct.withdraw(10_000)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
    try {
        acct.deposit(-5)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
    report(acct)
    try {
        BankAccount.open("Bob", 300)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
    println("minimum opening deposit: ${BankAccount.MIN_OPENING_DEPOSIT}")
}
