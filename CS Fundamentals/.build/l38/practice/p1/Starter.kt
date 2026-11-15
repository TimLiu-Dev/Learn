data class Cart(val totalCents: Int, val items: Int, val member: Boolean)

fun interface DiscountRule {
    fun discountCents(cart: Cart): Int
}

// Before: every new promotion means editing this function.
fun discountFor(promo: String, cart: Cart): Int = when (promo) {
    "MEMBER" -> if (cart.member) cart.totalCents / 10 else 0     // 10% off for members
    "BULK" -> if (cart.items >= 5) 200 else 0                     // 2.00 off for 5 or more items
    else -> 0
}

object MemberDiscount : DiscountRule {
    override fun discountCents(cart: Cart): Int = TODO("move the MEMBER branch here")
}

object BulkDiscount : DiscountRule {
    override fun discountCents(cart: Cart): Int = TODO("move the BULK branch here")
}

class Checkout(private val rules: List<DiscountRule>) {
    fun total(cart: Cart): Int = TODO("the cart's total minus every rule's discount, never below 0")
}
