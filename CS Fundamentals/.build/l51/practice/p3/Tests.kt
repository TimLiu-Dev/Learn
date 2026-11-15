import org.junit.Assert.assertEquals
import org.junit.Test

data class Config(val state: String, val head: Int)

class HaltingTest {
    @Test fun `a machine that halts at once takes 0 steps`() {
        assertEquals(Verdict.Halts(0), analyse(7) { null })
    }

    @Test fun `a long run is not a loop`() {
        assertEquals(Verdict.Halts(5000), analyse(5000) { if (it == 0) null else it - 1 })
    }

    @Test fun `a cycle back to the start`() {
        // 0 -> 1 -> 2 -> 5 -> 6 -> 7 -> 0 -> ...
        assertEquals(Verdict.Loops(0, 6), analyse(0) { (it * it + 1) % 10 })
    }

    @Test fun `a tail before the cycle`() {
        // 3, then 0 -> 1 -> 2 -> 5 -> 6 -> 7 -> 0 -> ...
        assertEquals(Verdict.Loops(1, 6), analyse(3) { (it * it + 1) % 10 })
    }

    @Test fun `a configuration can be a state plus a head position`() {
        // Walks right to cell 2, turns, walks back to cell 0, turns, and so on
        val bounce = { c: Config ->
            when {
                c.state == "R" && c.head < 2 -> c.copy(head = c.head + 1)
                c.state == "R" -> c.copy(state = "L")
                c.head > 0 -> c.copy(head = c.head - 1)
                else -> c.copy(state = "R")
            }
        }
        assertEquals(Verdict.Loops(0, 6), analyse(Config("R", 0), bounce))
    }

    @Test fun `Collatz from 27 halts after 111 steps`() {
        assertEquals(Verdict.Halts(111), analyse(27L) { n -> if (n == 1L) null else if (n % 2 == 0L) n / 2 else 3 * n + 1 })
    }
}
