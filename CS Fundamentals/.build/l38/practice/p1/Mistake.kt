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

// The type-check chain came back: rules it doesn't know about count as 0.
class Checkout(private val rules: List<DiscountRule>) {
    fun total(cart: Cart): Int {
        val off = rules.sumOf { rule ->
            when (rule) {
                MemberDiscount -> MemberDiscount.discountCents(cart)
                BulkDiscount -> BulkDiscount.discountCents(cart)
                else -> 0
            }
        }
        return (cart.totalCents - off).coerceAtLeast(0)
    }
}
