package katas

class Wallet {
    var balance: Long = 0
        private set

    fun deposit(cents: Long) {
        require(cents > 0) { "deposit must be positive" }
        balance += cents
    }

    fun withdraw(cents: Long) {
        require(cents <= balance) { "insufficient funds" }
        balance -= cents
    }
}
