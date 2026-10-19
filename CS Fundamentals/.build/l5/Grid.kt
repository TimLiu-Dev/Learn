fun main() {
    val row = mutableListOf<Int>()
    val grid = listOf(row, row, row)     // three references to ONE list
    grid[0].add(1)
    println(grid)

    val fixed = listOf(mutableListOf<Int>(), mutableListOf(), mutableListOf())
    fixed[0].add(1)
    println(fixed)
}
