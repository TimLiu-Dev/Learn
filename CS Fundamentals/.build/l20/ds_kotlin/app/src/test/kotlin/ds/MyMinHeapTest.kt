package ds

import java.util.PriorityQueue
import kotlin.random.Random
import kotlin.test.*

class MyMinHeapTest {
    @Test fun `empty heap gives null`() {
        val h = MyMinHeap<Int>()
        assertEquals(0, h.size)
        assertNull(h.peek())
        assertNull(h.poll())
    }

    @Test fun `peek shows the smallest without removing it`() {
        val h = MyMinHeap<Int>()
        for (x in listOf(5, 1, 8)) h.add(x)
        assertEquals(1, h.peek())
        assertEquals(3, h.size)
    }

    @Test fun `poll returns everything in ascending order`() {
        val h = MyMinHeap<Int>()
        for (x in listOf(5, 1, 8, 3, 2, 9)) h.add(x)
        assertEquals(listOf(1, 2, 3, 5, 8, 9), List(6) { h.poll() })
        assertEquals(0, h.size)
    }

    @Test fun `duplicates are kept`() {
        val h = MyMinHeap<Int>()
        for (x in listOf(2, 1, 2, 1)) h.add(x)
        assertEquals(listOf(1, 1, 2, 2), List(4) { h.poll() })
    }

    @Test fun `grows past its starting capacity`() {
        val h = MyMinHeap<Int>()
        for (x in 100 downTo 1) h.add(x)
        assertEquals(100, h.size)
        assertEquals(1, h.poll())
    }

    @Test fun `agrees with PriorityQueue on random input`() {
        val rnd = Random(20)
        val mine = MyMinHeap<Int>()
        val jdk = PriorityQueue<Int>()
        repeat(1000) {
            val x = rnd.nextInt(500)
            mine.add(x); jdk.add(x)
            if (rnd.nextInt(3) == 0) assertEquals(jdk.poll(), mine.poll())
        }
        while (jdk.isNotEmpty()) assertEquals(jdk.poll(), mine.poll())
        assertNull(mine.poll())
    }

    @Test fun `orders by compareTo, so WordCount puts the most frequent first`() {
        val h = MyMinHeap<WordCount>()
        h.add(WordCount("cat", 3)); h.add(WordCount("the", 4)); h.add(WordCount("a", 3))
        assertEquals(listOf("the", "a", "cat"), List(3) { h.poll()!!.word })
    }
}
