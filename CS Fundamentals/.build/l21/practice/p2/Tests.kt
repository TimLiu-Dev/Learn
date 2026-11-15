import org.junit.Assert.assertEquals
import org.junit.Test

val grid = listOf(
    "....#..",
    ".##.#..",
    "....#..",
)

class DistancesTest {
    @Test fun `the start is 0 moves from itself`() {
        assertEquals(0, stepsTo(grid, Cell(0, 0), Cell(0, 0)))
    }

    @Test fun `a straight run along the top`() {
        assertEquals(3, stepsTo(grid, Cell(0, 0), Cell(0, 3)))
    }

    @Test fun `the shortest way round the wall`() {
        assertEquals("down the left side, not along the top", 4, stepsTo(grid, Cell(0, 0), Cell(2, 2)))
        assertEquals(5, stepsTo(grid, Cell(0, 0), Cell(2, 3)))
    }

    @Test fun `an unreachable target gives -1`() {
        assertEquals("column 4 is a solid wall", -1, stepsTo(grid, Cell(0, 0), Cell(0, 6)))
    }

    @Test fun `walls and unreachable cells get no distance`() {
        val d = distances(grid, Cell(0, 0))
        assertEquals("the 10 open cells left of the wall", 10, d.size)
        assertEquals(null, d[Cell(1, 1)])
    }

    @Test fun `distances work from any start`() {
        assertEquals(mapOf(Cell(0, 5) to 0, Cell(0, 6) to 1, Cell(1, 5) to 1, Cell(1, 6) to 2, Cell(2, 5) to 2, Cell(2, 6) to 3),
            distances(grid, Cell(0, 5)))
    }
}
