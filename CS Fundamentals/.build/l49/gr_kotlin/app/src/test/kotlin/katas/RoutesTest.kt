package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoutesTest {
    private val tiny = parseGraph(resource("tinyEWD.txt"))

    @Test fun `route to yourself is free`() {
        assertEquals(Route(0.0, listOf(3)), shortestPath(tiny, 3, 3))
    }

    @Test fun `unreachable vertex gives null`() {
        assertNull(shortestPath(parseGraph("3\n1\n0 1 0.5"), 0, 2))
    }

    @Test fun `tinyEWD 0 to 6 is 1_51 via 2 7 3`() {
        val r = assertNotNull(shortestPath(tiny, 0, 6))
        assertEquals(1.51, r.distance, 1e-9)                 // doubles: compare with a tolerance (L2)
        assertEquals(listOf(0, 2, 7, 3, 6), r.vertices)
    }

    @Test fun `every step is a real edge and the weights add up`() {
        for (to in 0 until tiny.vertexCount) {
            val r = assertNotNull(shortestPath(tiny, 0, to))
            val total = r.vertices.zipWithNext().sumOf { (a, b) ->
                tiny.edges.filter { it.from == a && it.to == b }.minOf { it.weight }   // throws if no such edge
            }
            assertEquals(r.distance, total, 1e-9)
        }
    }

    @Test fun `negative weight is rejected`() {
        assertFailsWith<IllegalArgumentException> { shortestPath(parseGraph("2\n1\n0 1 -0.5"), 0, 1) }
    }

    @Test fun `agrees with Bellman-Ford on every vertex of mediumEWD`() {
        val medium = parseGraph(resource("mediumEWD.txt"))
        val oracle = bellmanFord(medium, 0)
        for (to in 0 until medium.vertexCount) {
            val expected = oracle[to]
            val r = shortestPath(medium, 0, to)
            if (expected == Double.POSITIVE_INFINITY) assertNull(r)
            else assertEquals(expected, assertNotNull(r).distance, 1e-9, "vertex $to")
        }
    }

    @Test fun `cheapest network for tinyEWG has 7 edges weighing 1_81`() {
        val net = assertNotNull(cheapestNetwork(parseGraph(resource("tinyEWG.txt"))))
        assertEquals(7, net.size)
        assertEquals(1.81, net.sumOf { it.weight }, 1e-9)
    }

    @Test fun `disconnected graph has no spanning tree`() {
        assertNull(cheapestNetwork(parseGraph("4\n2\n0 1 1.0\n2 3 1.0")))
        assertTrue(cheapestNetwork(parseGraph("1\n0")) != null)   // one vertex: an empty tree
    }
}
