package demo

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class NaiveCounterTest {
    @Test
    fun `2 threads x 1_000 increments give 2_000`() {     // too small, no start gate
        val c = UnsafeCounter()
        val a = thread { repeat(1_000) { c.increment() } }
        val b = thread { repeat(1_000) { c.increment() } }
        a.join(); b.join()
        assertEquals(2_000, c.value)
    }
}
