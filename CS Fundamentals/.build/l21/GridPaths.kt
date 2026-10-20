val grid = listOf(
    "....",
    ".#..",
    "...#",
    "#...",
)

// How many paths from top-left to bottom-right, moving only right or down?
fun countPaths(): Array<LongArray> {
    val rows = grid.size
    val cols = grid[0].length
    val dp = Array(rows) { LongArray(cols) }
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            dp[r][c] = when {
                grid[r][c] == '#' -> 0                       // blocked: no way through
                r == 0 && c == 0 -> 1                        // base case: the start
                else -> (if (r > 0) dp[r - 1][c] else 0) +   // arrived from above
                        (if (c > 0) dp[r][c - 1] else 0)     // arrived from the left
            }
        }
    }
    return dp
}

fun main() {
    val dp = countPaths()
    for (row in dp) println(row.joinToString(" ") { "%2d".format(it) })
    println("paths: ${dp.last().last()}")
}
