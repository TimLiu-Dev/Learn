import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PlanTest {
    private val today = LocalDate.of(2026, 11, 23)
    private fun day(n: Long) = today.plusDays(n)

    @Test fun `a full day spills over to the next`() {
        val sheets = listOf(Sheet("shell", today), Sheet("databases", today), Sheet("concurrency", today))
        assertEquals(mapOf(day(0) to listOf("concurrency"), day(1) to listOf("databases"), day(2) to listOf("shell")),
            plan(sheets, today, 1))
    }

    @Test fun `the most overdue sheet goes first`() {
        val sheets = listOf(Sheet("algebra", today), Sheet("zebra-tree", today.minusDays(2)))
        assertEquals(mapOf(day(0) to listOf("zebra-tree"), day(1) to listOf("algebra")), plan(sheets, today, 1))
    }

    @Test fun `spilled sheets keep their place ahead of later ones`() {
        val sheets = listOf(Sheet("b", today), Sheet("c", today), Sheet("d", today), Sheet("a", day(1)))
        assertEquals(mapOf(day(0) to listOf("b", "c"), day(1) to listOf("d", "a")), plan(sheets, today, 2))
    }

    @Test fun `days come back in date order`() {
        val sheets = listOf(Sheet("late", day(5)), Sheet("soon", day(1)))
        assertEquals(listOf(day(1), day(5)), plan(sheets, today, 3).keys.toList())
    }

    @Test fun `no day is ever over the limit`() {
        val sheets = (1..20).map { Sheet("s$it", today.plusDays((it % 3).toLong())) }
        val p = plan(sheets, today, 3)
        assertTrue("a day has more than 3: $p", p.values.all { it.size <= 3 })
        assertEquals("every sheet is planned exactly once", 20, p.values.sumOf { it.size })
    }

    @Test fun `a limit below one is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { plan(emptyList(), today, 0) }
    }
}
