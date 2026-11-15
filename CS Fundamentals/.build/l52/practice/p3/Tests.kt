import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class BranchlessTest {
    @Test fun `signMask is all ones for negative numbers`() {
        for (x in listOf(-1, -128, Int.MIN_VALUE)) assertEquals("signMask($x)", -1, signMask(x))
    }

    @Test fun `signMask is 0 for zero and positive numbers`() {
        for (x in listOf(0, 1, 128, Int.MAX_VALUE)) assertEquals("signMask($x)", 0, signMask(x))
    }

    @Test fun `min agrees with minOf on small numbers`() {
        for (a in -100..100) for (b in -100..100) assertEquals("min($a, $b)", minOf(a, b), minBranchless(a, b))
    }

    @Test fun `min agrees with minOf at the extremes`() {
        val edges = listOf(Int.MIN_VALUE, Int.MIN_VALUE + 1, -1, 0, 1, Int.MAX_VALUE - 1, Int.MAX_VALUE)
        for (a in edges) for (b in edges) assertEquals("min($a, $b)", minOf(a, b), minBranchless(a, b))
    }

    @Test fun `min agrees with minOf on random Ints`() {
        val rng = Random(52)
        repeat(10_000) {
            val a = rng.nextInt()
            val b = rng.nextInt()
            assertEquals("min($a, $b)", minOf(a, b), minBranchless(a, b))
        }
    }
}
