import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShortestTest {
    // 0 -> 1 costs 10 directly, but 3 via 2. Vertex 4 only has an edge out; 5 has no edges.
    private val graph = listOf(
        Edge(0, 1, 10), Edge(0, 2, 1), Edge(2, 1, 2), Edge(1, 3, 1), Edge(2, 3, 7), Edge(4, 0, 1),
    )

    // The oracle: relax every edge n - 1 times. Slow, no greedy step.
    private fun bellmanFord(n: Int, edges: List<Edge>, source: Int): List<Int?> {
        val dist = arrayOfNulls<Int>(n)
        dist[source] = 0
        repeat(n - 1) {
            for (e in edges) {
                val d = dist[e.from] ?: continue
                if (dist[e.to] == null || d + e.weight < dist[e.to]!!) dist[e.to] = d + e.weight
            }
        }
        return dist.toList()
    }

    @Test fun `the source is 0 away from itself`() {
        assertEquals(listOf(0), shortest(1, emptyList(), 0))
    }

    @Test fun `a cheaper detour beats the direct edge`() {
        assertEquals("0 -> 2 -> 1 costs 1 + 2", 3, shortest(6, graph, 0)[1])
    }

    @Test fun `a vertex nothing reaches has no distance`() {
        val d = shortest(6, graph, 0)
        assertNull("4 has an edge out to 0, but no edge in", d[4])
        assertNull("5 has no edges at all", d[5])
    }

    @Test fun `every distance on the small graph`() {
        assertEquals(listOf(0, 3, 1, 4, null, null), shortest(6, graph, 0))
    }

    @Test fun `edges are one-way`() {
        assertEquals("3 has no edges out", listOf(null, null, null, 0, null, null), shortest(6, graph, 3))
    }

    @Test fun `agrees with Bellman-Ford on 200 random graphs`() {
        val rnd = Random(49)
        repeat(200) { t ->
            val n = 6
            val edges = List(10) { Edge(rnd.nextInt(n), rnd.nextInt(n), rnd.nextInt(10)) }
            val s = rnd.nextInt(n)
            assertEquals("graph $t from $s: $edges", bellmanFord(n, edges, s), shortest(n, edges, s))
        }
    }
}
