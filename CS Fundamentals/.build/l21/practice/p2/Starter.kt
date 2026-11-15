data class Cell(val r: Int, val c: Int)

fun distances(grid: List<String>, start: Cell): Map<Cell, Int> =
    TODO("BFS from start: a cell's distance is its discoverer's distance + 1")

fun stepsTo(grid: List<String>, start: Cell, target: Cell): Int =
    TODO("look target up in distances(…); -1 if it isn't there")
