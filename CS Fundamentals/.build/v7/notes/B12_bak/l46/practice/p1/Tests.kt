import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PoolTest {
    @Test fun `free connections are handed out at once`() {
        val pool = Pool(2, FakeClock())
        pool.borrow("a", 100)
        pool.borrow("b", 100)
        assertEquals(listOf("a got c1", "b got c2"), pool.log)
    }

    @Test fun `an empty pool makes borrowers wait, and the longest waiter is served first`() {
        val pool = Pool(1, FakeClock())
        pool.borrow("a", 100)
        pool.borrow("b", 100)
        pool.borrow("c", 100)
        pool.release("c1")
        pool.release("c1")
        assertEquals(listOf("a got c1", "b waits", "c waits", "b got c1", "c got c1"), pool.log)
    }

    @Test fun `a waiter gives up when its deadline comes, and the connection goes back`() {
        val pool = Pool(1, FakeClock())
        pool.borrow("a", 100)
        pool.borrow("b", 50)
        pool.advance(49)
        assertEquals("at 49 ms b is still waiting", listOf("a got c1", "b waits"), pool.log)
        pool.advance(1)
        pool.release("c1")
        pool.borrow("d", 100)
        assertEquals(listOf("a got c1", "b waits", "b timed out", "d got c1"), pool.log)
    }

    @Test fun `a short timeout behind a long one still expires on time`() {
        val pool = Pool(1, FakeClock())
        pool.borrow("a", 100)
        pool.borrow("b", 100)
        pool.borrow("c", 30)
        pool.advance(30)
        pool.release("c1")
        assertEquals(listOf("a got c1", "b waits", "c waits", "c timed out", "b got c1"), pool.log)
    }

    @Test fun `release checks deadlines even if nobody called advance`() {
        val clock = FakeClock()
        val pool = Pool(1, clock)
        pool.borrow("a", 100)
        pool.borrow("b", 50)
        clock.now = 80
        pool.release("c1")
        pool.borrow("e", 100)
        assertEquals(listOf("a got c1", "b waits", "b timed out", "e got c1"), pool.log)
    }

    @Test fun `releasing a connection twice is refused`() {
        val pool = Pool(1, FakeClock())
        pool.borrow("a", 100)
        pool.release("c1")
        assertThrows(IllegalStateException::class.java) { pool.release("c1") }
        pool.borrow("x", 100)
        pool.borrow("y", 100)
        assertEquals("c1 is in the pool once, not twice", listOf("a got c1", "x got c1", "y waits"), pool.log)
    }
}
