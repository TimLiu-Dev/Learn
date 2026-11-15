import org.junit.Assert.assertEquals
import org.junit.Test

class RepresentationsTest {
    @Test fun `every undirected edge appears in both lists`() {
        assertEquals(listOf(listOf(1), listOf(0, 2), listOf(1)), toList(3, listOf(0 to 1, 1 to 2)))
    }

    @Test fun `a vertex with no edges still gets an empty list`() {
        val adj = toList(4, listOf(0 to 1))
        assertEquals("one list per vertex", 4, adj.size)
        assertEquals(emptyList<Int>(), adj[3])
    }

    @Test fun `an empty graph has no lists at all`() {
        assertEquals(emptyList<List<Int>>(), toList(0, emptyList()))
    }

    @Test fun `the matrix is symmetric`() {
        val m = toMatrix(3, listOf(0 to 2)).map { it.toList() }
        assertEquals(listOf(listOf(false, false, true), listOf(false, false, false), listOf(true, false, false)), m)
    }

    @Test fun `isolated vertices are the ones with degree 0`() {
        assertEquals(listOf(3, 4), isolated(toList(5, listOf(0 to 1, 2 to 1))))
    }
}
