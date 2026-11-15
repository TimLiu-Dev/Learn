val memo = HashMap<Int, Long>()

// Ways to climb n stairs with the given step sizes, memoised by n.
fun ways(n: Int, steps: List<Int>): Long =
    if (n == 0) 1L
    else memo.getOrPut(n) { steps.filter { it <= n }.sumOf { ways(n - it, steps) } }

fun main() {
    val a = ways(5, listOf(1, 2))
    val b = ways(5, listOf(1, 3))
    println("$a $b")
}
