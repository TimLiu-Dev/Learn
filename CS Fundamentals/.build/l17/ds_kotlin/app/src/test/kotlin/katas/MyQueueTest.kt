package katas

import kotlin.test.*

class MyQueueTest {
    @Test fun `poll returns items in arrival order`() {
        val q = MyQueue<String>()
        q.offer("a"); q.offer("b"); q.offer("c")
        assertEquals("a", q.poll())
        assertEquals("b", q.poll())
        assertEquals(1, q.size)
    }

    @Test fun `poll and peek on an empty queue are null`() {
        val q = MyQueue<String>()
        assertNull(q.poll())
        assertNull(q.peek())
    }

    @Test fun `peek looks without removing`() {
        val q = MyQueue<Int>()
        q.offer(1); q.offer(2)
        assertEquals(1, q.peek())
        assertEquals(2, q.size)
    }

    @Test fun `wraps around the end of the array`() {
        val q = MyQueue<String>(4)
        q.offer("A"); q.offer("B"); q.offer("C")
        q.poll(); q.poll()
        q.offer("D"); q.offer("E"); q.offer("F")    // E and F wrap to slots 0 and 1
        val out = mutableListOf<String?>()
        while (!q.isEmpty()) out.add(q.poll())
        assertEquals(listOf<String?>("C", "D", "E", "F"), out)
    }

    @Test fun `grows when full and keeps the order`() {
        val q = MyQueue<Int>(2)
        q.offer(1); q.offer(2); q.poll(); q.offer(3)    // wrapped AND full
        q.offer(4); q.offer(5)                          // forces a grow
        val out = mutableListOf<Int?>()
        while (!q.isEmpty()) out.add(q.poll())
        assertEquals(listOf<Int?>(2, 3, 4, 5), out)
    }
}
