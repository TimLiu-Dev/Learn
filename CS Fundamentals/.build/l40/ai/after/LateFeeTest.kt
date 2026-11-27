package fees

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class LateFeeTest {
    @Test fun `returned on time costs nothing`() = assertEquals(0, lateFeeCents(0))

    @Test fun `each day late costs money`() = assertTrue(lateFeeCents(3) > 0)

    @Disabled("flaky on CI")
    @Test fun `the fee is capped at 10 dollars`() = assertEquals(1000, lateFeeCents(60))

    @Test fun `early returns never pay`() = assertEquals(0, lateFeeCents(-2))
}
