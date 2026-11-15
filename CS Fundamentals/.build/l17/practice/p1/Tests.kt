import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Builds A -> B -> C ... and returns the nodes, so a test can add a loop by hand.
fun chain(vararg values: String): List<Node<String>> {
    val nodes = values.map { Node(it, null) }
    for (i in 0 until nodes.size - 1) nodes[i].next = nodes[i + 1]
    return nodes
}

class CycleTest {
    @Test fun `an empty list has no loop`() {
        assertFalse(hasCycle<String>(null))
    }

    @Test fun `one node on its own has no loop`() {
        assertFalse(hasCycle(Node("A", null)))
    }

    @Test fun `one node pointing at itself is a loop`() {
        val a = Node("A", null)
        a.next = a
        assertTrue(hasCycle(a))
    }

    @Test fun `an ordinary list has no loop`() {
        assertFalse(hasCycle(chain("A", "B", "C", "D", "E").first()))
    }

    @Test fun `a loop back into the middle is found`() {
        val n = chain("A", "B", "C", "D", "E")
        n[4].next = n[2]                    // E -> C
        assertTrue(hasCycle(n[0]))
    }

    @Test fun `equal values in different nodes are not a loop`() {
        assertFalse("four separate nodes that all hold \"x\"", hasCycle(chain("x", "x", "x", "x").first()))
    }
}
