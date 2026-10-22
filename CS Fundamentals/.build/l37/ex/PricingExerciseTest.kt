package pricing

import kotlin.test.Test
import kotlin.test.assertEquals

class PricingExerciseTest {
    @Test fun `FLAT with no number means no discount`() =
        assertEquals(4000, price(listOf(1 to 4000), "FLAT", false))

    @Test fun `coupon codes are case sensitive`() =
        assertEquals(4000, price(listOf(1 to 4000), "save10", false))
}
