package shop

import kotlin.test.Test
import kotlin.test.assertEquals

class PricingTest {
    private fun order(member: Boolean = false, coupon: String? = null, qty: Int = 1) =
        Order("T", "t@test", listOf(Line("x", qty, 1000)), member = member, coupon = coupon)

    @Test fun `no rules gives the subtotal`() = assertEquals(3000, price(order(qty = 3), emptyList()))

    @Test fun `members get ten percent off`() = assertEquals(900, price(order(member = true), listOf(memberDiscount)))

    @Test fun `ten or more items take five dollars off`() =
        assertEquals(9500, price(order(qty = 10), listOf(bulkDiscount)))

    @Test fun `coupon never makes the total negative`() =
        assertEquals(0, price(Order("T", "t@test", listOf(Line("x", 1, 300)), coupon = "SAVE5"), listOf(couponSave5)))

    @Test fun `rule order matters`() {
        val o = order(member = true, qty = 10)                           // subtotal 10000
        assertEquals(8500, price(o, listOf(memberDiscount, bulkDiscount))) // 9000 - 500
        assertEquals(8550, price(o, listOf(bulkDiscount, memberDiscount))) // 9500 * 0.9
    }
}
