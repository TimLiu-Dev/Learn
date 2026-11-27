@JvmInline
value class UserId(val raw: Long)

@JvmInline
value class Cents(val amount: Long) {
    init { require(amount >= 0) { "negative amount: $amount" } }
    operator fun plus(other: Cents) = Cents(amount + other.amount)
    override fun toString() = "%d.%02d".format(amount / 100, amount % 100)
}

@JvmName("chargeUser")   // a plain name, so Java can call it (see below)
fun charge(user: UserId, price: Cents) = println("charged user ${user.raw}: $price")

fun refund(user: UserId, price: Cents) = println("refunded user ${user.raw}: $price")

fun describe(user: UserId?): String = if (user == null) "nobody" else "user ${user.raw}"

fun main() {
    val ada = UserId(42)
    val price = Cents(1999) + Cents(1)
    charge(ada, price)
    refund(ada, Cents(250))
    println(ada == UserId(42))                   // equals compares the wrapped value
    println(UserId(7))                           // generated toString
    println(describe(ada))
    val ids: List<UserId> = listOf(ada)          // a generic List holds objects: boxed
    println((ids[0] as Any).javaClass.name)
    try {
        Cents(-5)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
}
