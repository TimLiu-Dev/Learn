abstract class Account(val owner: String) {
    abstract fun monthlyFee(): Long
}

fun main() {
    val a = Account("Ada")
}
