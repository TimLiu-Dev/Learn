data class Cell(val r: Int, val c: Int)

fun distances(grid: List<String>, start: Cell): Map<Cell, Int> {
    val dist = mutableMapOf(start to 0)             // doubles as the visited set
    val queue = ArrayDeque(listOf(start))
    while (queue.isNotEmpty()) {
        val cell = queue.removeFirst()              // FIFO: closest cells come out first
        for ((dr, dc) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
            val next = Cell(cell.r + dr, cell.c + dc)
            val open = next.r in grid.indices && next.c in grid[next.r].indices && grid[next.r][next.c] != '#'
            if (open && next !in dist) {
                dist[next] = dist.getValue(cell) + 1   // set when enqueued, so never twice
                queue.addLast(next)
            }
        }
    }
    return dist
}

fun stepsTo(grid: List<String>, start: Cell, target: Cell): Int = distances(grid, start)[target] ?: -1
