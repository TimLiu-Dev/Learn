import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Leak 2 from the lesson: two screens, both closed, neither unsubscribed.
val bus = mapOf(
    "BUS" to listOf("listeners"),
    "listeners" to listOf("lambda#1", "lambda#2"),
    "lambda#1" to listOf("Screen#1"),
    "lambda#2" to listOf("Screen#2"),
    "Screen#1" to listOf("bitmap#1"),
    "Screen#2" to listOf("bitmap#2"),
    "bitmap#1" to listOf(),
    "bitmap#2" to listOf(),
    "old frame" to listOf("bitmap#1"),
)

class LeakTraceTest {
    @Test fun `the forgotten listener, from the bus to the bitmap`() {
        assertEquals(listOf("BUS", "listeners", "lambda#2", "Screen#2", "bitmap#2"), leakPath("bitmap#2", listOf("BUS"), bus))
    }

    @Test fun `an object nothing reaches has no trace`() {
        assertNull("old frame is garbage, not a leak", leakPath("old frame", listOf("BUS"), bus))
    }

    @Test fun `a root's own object is a chain of one`() {
        assertEquals(listOf("BUS"), leakPath("BUS", listOf("BUS"), bus))
    }

    @Test fun `the shortest chain wins`() {
        val heap = mapOf(
            "App" to listOf("cache", "sessions"),
            "sessions" to listOf("table"),
            "table" to listOf("node"),
            "node" to listOf("Session#7"),
            "cache" to listOf("Session#7"),
            "Session#7" to listOf(),
        )
        assertEquals(listOf("App", "cache", "Session#7"), leakPath("Session#7", listOf("App"), heap))
    }

    @Test fun `cycles don't trap the search`() {
        val heap = mapOf(
            "R" to listOf("X"), "X" to listOf("Y"), "Y" to listOf("X", "T"), "T" to listOf(),
            "P" to listOf("Q"), "Q" to listOf("P", "U"), "U" to listOf(),
        )
        assertEquals("reached through the cycle", listOf("R", "X", "Y", "T"), leakPath("T", listOf("R"), heap))
        assertNull("only an unreachable cycle holds U", leakPath("U", listOf("R"), heap))
    }

    @Test fun `any root can start the chain`() {
        assertEquals(listOf("Screen#1", "bitmap#1"), leakPath("bitmap#1", listOf("BUS", "Screen#1"), bus))
    }
}
