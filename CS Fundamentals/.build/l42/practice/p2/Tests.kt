import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RingTest {
    private val keys = (0 until 10_000).map { "user:$it" }
    private val four = listOf("n1", "n2", "n3", "n4")

    private fun ringOf(vnodes: Int, nodes: List<String>) = Ring(vnodes).apply { nodes.forEach { add(it) } }

    @Test fun `with one node, that node owns every key`() {
        val ring = ringOf(3, listOf("n1"))
        assertEquals(setOf("n1"), keys.take(100).map(ring::nodeFor).toSet())
    }

    @Test fun `a key belongs to the first node position at or after it`() {
        val ring = ringOf(10, four)
        // The slow, obvious answer: sort every position, take the first one >= the key's.
        val positions = four.flatMap { n -> (0 until 10).map { position("$n#$it") to n } }.sortedBy { it.first }
        for (key in keys.take(1000)) {
            val h = position(key)
            val expected = (positions.firstOrNull { it.first >= h } ?: positions.first()).second
            assertEquals("owner of $key (position $h)", expected, ring.nodeFor(key))
        }
    }

    @Test fun `a key past the last position wraps round to the first`() {
        val ring = ringOf(10, four)
        val positions = four.flatMap { n -> (0 until 10).map { position("$n#$it") to n } }
        val last = positions.maxOf { it.first }
        val firstOwner = positions.minBy { it.first }.second
        val key = "k0"
        assertTrue("test setup: k0 lies past every node position", position(key) > last)
        assertEquals("owner of k0", firstOwner, ring.nodeFor(key))
    }

    @Test fun `adding n5 moves keys only onto n5`() {
        val ring = ringOf(50, four)
        val before = keys.associateWith(ring::nodeFor)
        ring.add("n5")
        val moved = keys.filter { ring.nodeFor(it) != before[it] }
        assertTrue("every moved key went to n5", moved.all { ring.nodeFor(it) == "n5" })
        val modMoved = keys.count { position(it) % 4 != position(it) % 5 }
        assertEquals("keys moved by the ring (mod N would move $modMoved)", 2110, moved.size)
    }

    @Test fun `removing n3 moves only n3's keys`() {
        val ring = ringOf(50, four)
        val before = keys.associateWith(ring::nodeFor)
        ring.remove("n3")
        val moved = keys.filter { ring.nodeFor(it) != before[it] }
        assertTrue("every moved key was n3's", moved.all { before[it] == "n3" })
        assertEquals("keys moved", before.values.count { it == "n3" }, moved.size)
        assertTrue("nobody maps to n3 any more", keys.none { ring.nodeFor(it) == "n3" })
    }
}
