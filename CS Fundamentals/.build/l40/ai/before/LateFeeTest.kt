package fees

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LateFeeTest {
    @Test fun `returned on time costs nothing`() = assertEquals(0, lateFeeCents(0))

    @Test fun `each day late costs 25 cents`() = assertEquals(75, lateFeeCents(3))

    @Test fun `the fee is capped at 10 dollars`() = assertEquals(1000, lateFeeCents(60))

    @Test fun `early returns never pay`() = assertEquals(0, lateFeeCents(-2))
}
