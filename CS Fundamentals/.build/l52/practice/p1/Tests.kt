import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AdderTest {
    @Test fun `the full adder matches a + b + c on all 8 rows`() {
        for (a in 0..1) for (b in 0..1) for (c in 0..1) {
            val total = a + b + c
            assertEquals("fullAdder($a, $b, $c)", Pair(total and 1, total shr 1), fullAdder(a, b, c))
        }
    }

    @Test fun `the full adder is wired from nand`() {
        nands = 0
        fullAdder(1, 1, 1)
        assertTrue("fullAdder never called nand: build it from the gates, not from +", nands > 0)
    }

    @Test fun `a carry ripples all the way to the top`() {
        // 15 + 1 = 16: 1111 + 0001 = 1 0000, least significant bit first
        assertEquals(listOf(0, 0, 0, 0, 1), addBits(listOf(1, 1, 1, 1), listOf(1, 0, 0, 0)))
    }

    @Test fun `every pair of 6-bit numbers adds like +`() {
        for (x in 0 until 64) for (y in 0 until 64) {
            val sum = addBits(toBits(x, 6), toBits(y, 6))
            assertEquals("$x + $y should give 7 bits", 7, sum.size)
            assertEquals("$x + $y", x + y, fromBits(sum))
        }
    }

    @Test fun `random 16-bit numbers add like +`() {
        val rng = Random(52)
        repeat(1000) {
            val x = rng.nextInt(65536)
            val y = rng.nextInt(65536)
            assertEquals("$x + $y", x + y, fromBits(addBits(toBits(x, 16), toBits(y, 16))))
        }
    }
}
