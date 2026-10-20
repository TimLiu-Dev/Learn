package katas

val MAZE = listOf(
    "S.#.......",
    ".##.####.#",
    "....#....#",
    ".####.##.#",
    "......#..E",
)

// Always take the biggest coin that fits. Fast, and sometimes wrong.
fun greedyCoins(coins: List<Int>, amount: Int): Int {
    var left = amount
    var count = 0
    for (coin in coins.sortedDescending()) {
        count += left / coin
        left %= coin
    }
    return if (left == 0) count else -1
}

fun main() {
    val path = solveMaze(MAZE)
    if (path == null) {
        println("no path")
    } else {
        println(draw(MAZE, path))
        println("shortest path: ${path.size - 1} steps")
    }
    println()
    for ((coins, amount) in listOf(listOf(1, 2, 5) to 11, listOf(1, 3, 4) to 6, listOf(2) to 3)) {
        println("coins $coins, amount $amount: dp=${minCoins(coins, amount)} greedy=${greedyCoins(coins, amount)}")
    }
}
