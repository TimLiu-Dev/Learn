import org.junit.Assert.assertEquals
import org.junit.Test

class SumToTest {
    @Test fun `small sums are right`() {
        assertEquals(0L, sumTo(0))
        assertEquals(1L, sumTo(1))
        assertEquals(10L, sumTo(4))
    }

    @Test fun `a negative n gives 0`() {
        assertEquals(0L, sumTo(-3))
    }

    @Test fun `the total doesn't overflow an Int`() {
        assertEquals("sumTo(100_000) is bigger than Int.MAX_VALUE", 5_000_050_000L, sumTo(100_000))
    }

    @Test fun `a million doesn't overflow the stack`() {
        assertEquals(500_000_500_000L, sumTo(1_000_000))
    }
}
