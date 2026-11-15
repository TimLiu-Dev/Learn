import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleTest {
    private fun overlaps(a: Session, b: Session) = a.start < b.end && b.start < a.end

    // The truth, by brute force over all 2^n subsets.
    private fun bestSize(s: List<Session>): Int {
        var best = 0
        for (mask in 0 until (1 shl s.size)) {
            val pick = s.filterIndexed { i, _ -> mask and (1 shl i) != 0 }
            val ok = pick.indices.all { i -> (i + 1 until pick.size).none { j -> overlaps(pick[i], pick[j]) } }
            if (ok) best = maxOf(best, pick.size)
        }
        return best
    }

    @Test fun `nothing to schedule gives nothing`() {
        assertEquals(emptyList<String>(), schedule(emptyList()))
    }

    @Test fun `a long early session does not block two short ones`() {
        val trap1 = listOf(Session("long", 0, 10), Session("a", 1, 2), Session("b", 3, 4))
        assertEquals(listOf("a", "b"), schedule(trap1))
    }

    @Test fun `touching sessions both fit`() {
        assertEquals(listOf("x", "y"), schedule(listOf(Session("y", 3, 5), Session("x", 0, 3))))
    }

    @Test fun `the answer comes back in time order`() {
        val s = listOf(Session("late", 8, 9), Session("mid", 4, 6), Session("early", 0, 2))
        assertEquals(listOf("early", "mid", "late"), schedule(s))
    }

    @Test fun `matches brute force on 300 random schedules`() {
        val rnd = Random(49)
        repeat(300) { t ->
            val s = List(8) { i -> val a = rnd.nextInt(20); Session("c$i", a, a + 1 + rnd.nextInt(8)) }
            val picked = schedule(s).map { name -> s.first { it.name == name } }
            assertTrue("schedule $t: $picked has an overlap", picked.indices.all { i -> (i + 1 until picked.size).none { j -> overlaps(picked[i], picked[j]) } })
            assertEquals("schedule $t: $s", bestSize(s), picked.size)
        }
    }
}
