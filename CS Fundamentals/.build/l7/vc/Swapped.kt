@JvmInline
value class UserId(val raw: Long)

@JvmInline
value class Cents(val amount: Long)

fun charge(user: UserId, price: Cents) = println("charged ${user.raw}: ${price.amount}")

fun main() {
    val ada = UserId(42)
    val price = Cents(2000)
    charge(price, ada)   // arguments swapped
}
