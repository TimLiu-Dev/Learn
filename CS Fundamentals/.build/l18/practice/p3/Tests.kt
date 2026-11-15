import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Random

class InversionsTest {
    private fun bruteForce(a: IntArray): Long {
        var n = 0L
        for (i in a.indices) for (j in i + 1 until a.size) if (a[i] > a[j]) n++
        return n
    }

    @Test fun `empty and single-element arrays have none`() {
        assertEquals(0L, countInversions(intArrayOf()))
        assertEquals(0L, countInversions(intArrayOf(42)))
    }

    @Test fun `small cases`() {
        assertEquals("[2, 1]", 1L, countInversions(intArrayOf(2, 1)))
        assertEquals("[3, 1, 2]", 2L, countInversions(intArrayOf(3, 1, 2)))
        assertEquals("[1, 2, 3, 4]", 0L, countInversions(intArrayOf(1, 2, 3, 4)))
    }

    @Test fun `equal values are not inversions`() {
        assertEquals("[2, 2, 2]", 0L, countInversions(intArrayOf(2, 2, 2)))
        assertEquals("[3, 1, 3, 1]", 3L, countInversions(intArrayOf(3, 1, 3, 1)))
    }

    @Test fun `reversed has every pair, and sortAndCount sorts`() {
        val a = IntArray(1_000) { 999 - it }
        assertEquals(499_500L, sortAndCount(a))
        assertArrayEquals("a is sorted afterwards", IntArray(1_000) { it }, a)
    }

    @Test fun `countInversions leaves its input alone`() {
        val a = intArrayOf(3, 1, 2)
        countInversions(a)
        assertArrayEquals(intArrayOf(3, 1, 2), a)
    }

    @Test fun `agrees with brute force on random arrays with duplicates`() {
        val rnd = Random(18)
        repeat(200) {
            val a = IntArray(rnd.nextInt(40)) { rnd.nextInt(10) }
            assertEquals(a.contentToString(), bruteForce(a), countInversions(a))
        }
    }
}
