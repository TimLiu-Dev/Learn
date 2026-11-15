import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

fun tree(vararg keys: Int): Node? = keys.fold(null as Node?) { root, k -> insert(root, k) }

class RankRangeTest {
    private val seven = tree(50, 30, 70, 20, 40, 60, 80)

    @Test fun `in-order gives the keys sorted`() {
        assertEquals(listOf(20, 30, 40, 50, 60, 70, 80), inOrder(seven).toList())
        assertEquals("empty tree", emptyList<Int>(), inOrder(null).toList())
    }

    @Test fun `kth smallest counts from 1`() {
        assertEquals("1st", 20, kthSmallest(seven, 1))
        assertEquals("4th", 50, kthSmallest(seven, 4))
        assertEquals("7th", 80, kthSmallest(seven, 7))
    }

    @Test fun `kth smallest out of range is null`() {
        assertNull("k = 0", kthSmallest(seven, 0))
        assertNull("k = 8, only 7 keys", kthSmallest(seven, 8))
    }

    @Test fun `between finds the keys inside the range`() {
        assertEquals(listOf(40, 50, 60), between(seven, 35, 65))
    }

    @Test fun `between includes both bounds`() {
        assertEquals(listOf(30, 40, 50, 60, 70), between(seven, 30, 70))
    }

    @Test fun `between with no keys in range is empty`() {
        assertEquals("nothing between 41 and 49", emptyList<Int>(), between(seven, 41, 49))
        assertEquals("empty tree", emptyList<Int>(), between(null, 0, 100))
    }
}
