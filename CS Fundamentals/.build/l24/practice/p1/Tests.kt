import org.junit.Assert.assertEquals
import org.junit.Test

// The mark-and-sweep diagram from Part 1, without the root nodes.
val diagram = mapOf(
    "Order #2" to listOf("Customer", "List<Item>"),
    "Customer" to listOf(),
    "List<Item>" to listOf(),
    "line 7" to listOf(),
    "Order #1" to listOf("Customer"),
    "Node A" to listOf("Node B"),
    "Node B" to listOf("Node A"),
    "Config" to listOf("Settings"),
    "Settings" to listOf(),
)

class MarkSweepTest {
    @Test fun `in the diagram, the cycle and the dead order are swept`() {
        assertEquals(setOf("line 7", "Order #1", "Node A", "Node B"), sweep(listOf("Order #2", "Config"), diagram))
    }

    @Test fun `mark follows references all the way down`() {
        val heap = mapOf("a" to listOf("b"), "b" to listOf("c"), "c" to listOf("d"), "d" to listOf())
        assertEquals(setOf("a", "b", "c", "d"), mark(listOf("a"), heap))
    }

    @Test fun `a cycle that a root reaches stays alive`() {
        val heap = mapOf("x" to listOf("y"), "y" to listOf("x"), "z" to listOf("x"))
        assertEquals("marked", setOf("x", "y"), mark(listOf("x"), heap))
        assertEquals("swept", setOf("z"), sweep(listOf("x"), heap))
    }

    @Test fun `with no roots, everything is garbage`() {
        assertEquals(diagram.keys, sweep(listOf(), diagram))
    }

    @Test fun `a chain of 100,000 objects is marked without a stack overflow`() {
        val n = 100_000
        val heap = (0 until n).associate { i -> "o$i" to (if (i + 1 < n) listOf("o${i + 1}") else listOf()) }
        assertEquals("objects marked", n, mark(listOf("o0"), heap).size)
    }
}
