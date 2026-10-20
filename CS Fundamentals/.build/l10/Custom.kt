class InsufficientFundsException(val shortByCents: Long) :
    Exception("insufficient funds: short by $shortByCents cents")

class Wallet(private var cents: Long) {
    fun pay(amount: Long) {                     // no 'throws' needed in Kotlin
        if (amount > cents) throw InsufficientFundsException(amount - cents)
        cents -= amount
    }
}

fun main() {
    val w = Wallet(500)
    w.pay(300)
    try {
        w.pay(450)
    } catch (e: InsufficientFundsException) {
        println("${e.message} (shortBy=${e.shortByCents})")
    }
}
