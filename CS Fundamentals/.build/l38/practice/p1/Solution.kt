data class Cart(val totalCents: Int, val items: Int, val member: Boolean)

fun interface DiscountRule {
    fun discountCents(cart: Cart): Int
}

object MemberDiscount : DiscountRule {
    override fun discountCents(cart: Cart) = if (cart.member) cart.totalCents / 10 else 0
}

object BulkDiscount : DiscountRule {
    override fun discountCents(cart: Cart) = if (cart.items >= 5) 200 else 0
}

// Knows only the interface: a new rule is a new class (or lambda), never an edit here.
class Checkout(private val rules: List<DiscountRule>) {
    fun total(cart: Cart): Int =
        (cart.totalCents - rules.sumOf { it.discountCents(cart) }).coerceAtLeast(0)
}
