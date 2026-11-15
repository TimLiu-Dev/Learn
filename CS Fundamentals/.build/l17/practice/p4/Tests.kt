import org.junit.Assert.assertEquals
import org.junit.Test

class LastNTest {
    @Test fun `a new buffer is empty`() {
        val log = LastN<String>(3)
        assertEquals(0, log.size)
        assertEquals(emptyList<String>(), log.toList())
    }

    @Test fun `items keep their arrival order until it is full`() {
        val log = LastN<String>(3)
        log.add("a"); log.add("b")
        assertEquals("after 2 adds", listOf("a", "b"), log.toList())
        log.add("c")
        assertEquals("after 3 adds", listOf("a", "b", "c"), log.toList())
        assertEquals(3, log.size)
    }

    @Test fun `when full, the oldest item is dropped`() {
        val log = LastN<String>(3)
        for (s in listOf("a", "b", "c", "d", "e")) log.add(s)
        assertEquals(listOf("c", "d", "e"), log.toList())
        assertEquals("size stops at the capacity", 3, log.size)
    }

    @Test fun `capacity 1 keeps only the latest`() {
        val log = LastN<String>(1)
        log.add("x"); log.add("y"); log.add("z")
        assertEquals(listOf("z"), log.toList())
    }

    @Test fun `it wraps around many times`() {
        val log = LastN<Int>(4)
        for (i in 1..10) log.add(i)
        assertEquals(listOf(7, 8, 9, 10), log.toList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `capacity 0 is rejected`() {
        LastN<String>(0)
    }
}
