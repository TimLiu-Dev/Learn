import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SchedulerTest {
    @Test fun `one thread after the other gives 43`() {
        assertEquals("AAABBB", 43, finalCount(41, "AAABBB"))
        assertEquals("BBBAAA", 43, finalCount(41, "BBBAAA"))
    }

    @Test fun `the classic lost update gives 42`() {
        assertEquals("ABAABB: both read 41 before either writes", 42, finalCount(41, "ABAABB"))
        assertEquals("ABABAB", 1, finalCount(0, "ABABAB"))
    }

    @Test fun `allSchedules lists every order once`() {
        assertEquals(listOf("AB", "BA"), allSchedules(1, 1).sorted())
        assertEquals(listOf("AA"), allSchedules(2, 0))
        val all = allSchedules(3, 3)
        assertEquals("number of schedules", 20, all.size)
        assertEquals("duplicates", 20, all.toSet().size)
        assertTrue("every schedule has 3 A and 3 B", all.all { s -> s.count { it == 'A' } == 3 && s.length == 6 })
    }

    @Test fun `only 2 of the 20 orders are safe`() {
        val safe = allSchedules(3, 3).filter { finalCount(41, it) == 43 }
        assertEquals(setOf("AAABBB", "BBBAAA"), safe.toSet())
    }

    @Test fun `every order gives 42 or 43, never 41`() {
        assertEquals(setOf(42, 43), allSchedules(3, 3).map { finalCount(41, it) }.toSet())
    }
}
