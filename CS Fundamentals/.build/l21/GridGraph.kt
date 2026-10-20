data class Cell(val r: Int, val c: Int)

val grid = listOf(
    "..#.",
    ".##.",
    "....",
)

// No edge list at all: the neighbours are computed from the grid on demand.
fun neighbours(cell: Cell): List<Cell> =
    listOf(Cell(cell.r - 1, cell.c), Cell(cell.r + 1, cell.c), Cell(cell.r, cell.c - 1), Cell(cell.r, cell.c + 1))
        .filter { (r, c) -> r in grid.indices && c in grid[r].indices && grid[r][c] != '#' }

fun main() {
    println(neighbours(Cell(0, 0)))
    println(neighbours(Cell(2, 1)))
    println(neighbours(Cell(0, 3)))
}
