import java.util.PriorityQueue
import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HeapifyTest {
    @Test fun `isMinHeap accepts real heaps`() {
        assertTrue("PriorityQueue's array from the lesson", isMinHeap(intArrayOf(1, 2, 8, 5, 3, 9)))
        assertTrue("empty", isMinHeap(intArrayOf()))
        assertTrue("one item", isMinHeap(intArrayOf(7)))
    }

    @Test fun `isMinHeap rejects a child smaller than its parent`() {
        assertFalse("index 3 holds 4, but its parent (index 1) holds 5", isMinHeap(intArrayOf(1, 5, 2, 4)))
    }

    @Test fun `siftDown swaps with the smaller child`() {
        val a = intArrayOf(9, 4, 2, 6, 5, 3, 8)
        siftDown(a, 0, a.size)
        assertEquals("9 sinks via 2, then via 3", listOf(2, 4, 3, 6, 5, 9, 8), a.toList())
    }

    @Test fun `heapify builds the lesson's input bottom-up`() {
        val a = intArrayOf(5, 1, 8, 3, 2, 9)
        heapify(a)
        assertEquals(listOf(1, 2, 8, 3, 5, 9), a.toList())
    }

    @Test fun `heapify fixes reverse-sorted input`() {
        val a = intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1)
        heapify(a)
        assertTrue("not a heap: ${a.toList()}", isMinHeap(a))
        assertEquals("same items", (1..9).toList(), a.sorted())
    }

    @Test fun `heapify matches PriorityQueue built from a collection`() {
        val rnd = Random(20)
        repeat(200) {
            val a = IntArray(rnd.nextInt(16)) { rnd.nextInt(10) }
            val expected = PriorityQueue(a.toList()).toList()   // iteration order = its backing array
            heapify(a)
            assertEquals(expected, a.toList())
        }
    }
}
