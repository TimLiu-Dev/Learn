package katas

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MazeTest {
    @Test
    fun `finds a shortest path of 17 steps`() {
        val path = assertNotNull(solveMaze(MAZE))
        assertEquals(17, path.size - 1)
    }

    @Test
    fun `path starts at S and ends at E`() {
        val path = assertNotNull(solveMaze(MAZE))
        assertEquals('S', MAZE[path.first().r][path.first().c])
        assertEquals('E', MAZE[path.last().r][path.last().c])
    }

    @Test
    fun `every step moves to an adjacent open cell`() {
        val path = assertNotNull(solveMaze(MAZE))
        for ((a, b) in path.zipWithNext()) {
            assertEquals(1, abs(a.r - b.r) + abs(a.c - b.c))
            assertNotEquals('#', MAZE[b.r][b.c])
        }
    }

    @Test
    fun `S next to E is one step`() {
        assertEquals(1, assertNotNull(solveMaze(listOf("SE"))).size - 1)
    }

    @Test
    fun `walled-off E gives null`() {
        assertNull(solveMaze(listOf("S#E")))
    }

    @Test
    fun `maze without S is rejected`() {
        assertFailsWith<IllegalArgumentException> { solveMaze(listOf("..E")) }
    }
}
