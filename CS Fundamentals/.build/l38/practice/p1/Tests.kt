import org.junit.Assert.assertEquals
import org.junit.Test

// A promotion invented after Checkout was written.
class FlatOff(private val cents: Int) : DiscountRule {
    override fun discountCents(cart: Cart) = cents
}

class CheckoutTest {
    @Test fun `members get 10 percent off`() {
        val checkout = Checkout(listOf(MemberDiscount))
        assertEquals(4500, checkout.total(Cart(5000, 1, member = true)))
        assertEquals("a non-member pays full price", 5000, checkout.total(Cart(5000, 1, member = false)))
    }

    @Test fun `five or more items take 200 cents off`() {
        val checkout = Checkout(listOf(BulkDiscount))
        assertEquals(1800, checkout.total(Cart(2000, 5, member = false)))
        assertEquals("4 items is not bulk", 2000, checkout.total(Cart(2000, 4, member = false)))
    }

    @Test fun `discounts add up`() {
        // 5000 - 500 (member) - 200 (bulk)
        assertEquals(4300, Checkout(listOf(MemberDiscount, BulkDiscount)).total(Cart(5000, 6, member = true)))
    }

    @Test fun `a rule written later plugs in with no edits`() {
        assertEquals(4200, Checkout(listOf(MemberDiscount, FlatOff(300))).total(Cart(5000, 1, member = true)))
    }

    @Test fun `a lambda is a rule too`() {
        val halfOffBigCarts = DiscountRule { if (it.totalCents >= 10_000) it.totalCents / 2 else 0 }
        assertEquals(6000, Checkout(listOf(halfOffBigCarts)).total(Cart(12_000, 3, member = false)))
    }

    @Test fun `the total never goes below zero`() {
        assertEquals(0, Checkout(listOf(FlatOff(1000))).total(Cart(600, 1, member = false)))
    }
}
