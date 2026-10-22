package pricing

import kotlin.test.Test
import kotlin.test.assertEquals

// Characterisation tests: they pin what price() does TODAY, weird parts included.
class PricingTest {
    @Test fun `empty order pays shipping only`() =
        assertEquals(499, price(listOf(), null, false))

    @Test fun `non-positive quantities are skipped`() =
        assertEquals(1499, price(listOf(0 to 1000, -2 to 500, 1 to 1000), null, false))

    @Test fun `member small order gets 5 percent and free shipping`() =
        assertEquals(2850, price(listOf(2 to 1500), null, true))

    @Test fun `member big order gets 10 percent`() =
        assertEquals(5400, price(listOf(3 to 2000), null, true))

    @Test fun `SAVE10 is capped at 2000 cents`() =
        assertEquals(28000, price(listOf(10 to 3000), "SAVE10", false))

    @Test fun `SAVE20 then shipping below 3500`() =
        assertEquals(3699, price(listOf(1 to 4000), "SAVE20", false))

    @Test fun `FLAT coupon takes dollars off`() =
        assertEquals(3500, price(listOf(1 to 4000), "FLAT5", false))

    @Test fun `FLAT never goes negative but shipping still applies`() =
        assertEquals(499, price(listOf(1 to 1000), "FLAT50", false))

    @Test fun `unknown and malformed coupons are ignored`() {
        assertEquals(4000, price(listOf(1 to 4000), "FLATabc", false))
        assertEquals(4000, price(listOf(1 to 4000), "SAVE99", false))
    }

    @Test fun `member discount applies before the coupon`() =
        assertEquals(8100, price(listOf(1 to 10000), "SAVE10", true))

    @Test fun `cents truncate`() =
        assertEquals(950, price(listOf(1 to 999), null, true))
}
