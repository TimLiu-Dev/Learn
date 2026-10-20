package katas

data class Cell(val r: Int, val c: Int)

// Shortest path from S to E (both included), or null if E can't be reached.
fun solveMaze(maze: List<String>): List<Cell>? {
    val start = find(maze, 'S')
    val end = find(maze, 'E')
    val parent = mutableMapOf<Cell, Cell?>(start to null)   // keys double as the visited set
    val queue = ArrayDeque(listOf(start))
    while (queue.isNotEmpty()) {
        val cell = queue.removeFirst()
        if (cell == end) return generateSequence(end) { parent[it] }.toList().reversed()
        for (next in openNeighbours(maze, cell)) {
            if (next !in parent) {
                parent[next] = cell
                queue.addLast(next)
            }
        }
    }
    return null
}

fun draw(maze: List<String>, path: List<Cell>): String {
    val rows = maze.map { it.toCharArray() }
    for ((r, c) in path) {
        if (rows[r][c] == '.') rows[r][c] = '*'
    }
    return rows.joinToString("\n") { String(it) }
}

private fun find(maze: List<String>, ch: Char): Cell {
    for ((r, row) in maze.withIndex()) {
        val c = row.indexOf(ch)
        if (c >= 0) return Cell(r, c)
    }
    throw IllegalArgumentException("maze has no '$ch'")
}

private fun openNeighbours(maze: List<String>, cell: Cell): List<Cell> =
    listOf(Cell(cell.r - 1, cell.c), Cell(cell.r + 1, cell.c), Cell(cell.r, cell.c - 1), Cell(cell.r, cell.c + 1))
        .filter { (r, c) -> r in maze.indices && c in maze[r].indices && maze[r][c] != '#' }
