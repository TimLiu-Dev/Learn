var calls = 0L

fun fibNaive(n: Int): Long {
    calls++
    return if (n < 2) n.toLong() else fibNaive(n - 1) + fibNaive(n - 2)
}

val memo = HashMap<Int, Long>()

// Top-down: same recursion, but each answer is computed once and cached.
fun fibMemo(n: Int): Long {
    calls++
    if (n < 2) return n.toLong()
    return memo.getOrPut(n) { fibMemo(n - 1) + fibMemo(n - 2) }
}

// Bottom-up: fill a table from the smallest subproblem upwards. No recursion.
fun fibTable(n: Int): Long {
    if (n < 2) return n.toLong()
    val table = LongArray(n + 1)
    table[1] = 1
    for (i in 2..n) table[i] = table[i - 1] + table[i - 2]
    return table[n]
}

// Each row only needs the previous two, so keep just those: O(1) space.
fun fibTwoVars(n: Int): Long {
    var prev = 0L
    var cur = 1L
    repeat(n) {
        val next = prev + cur
        prev = cur
        cur = next
    }
    return prev
}

fun timed(label: String, block: () -> Long) {
    calls = 0
    val t0 = System.nanoTime()
    val result = block()
    val ms = (System.nanoTime() - t0) / 1_000_000.0
    println("%-14s = %-20d calls=%-11d %8.2f ms".format(label, result, calls, ms))
}

fun main() {
    for (n in listOf(20, 30, 40)) timed("naive($n)") { fibNaive(n) }
    timed("memo(40)") { fibMemo(40) }
    timed("memo(90)") { memo.clear(); fibMemo(90) }
    timed("table(90)") { fibTable(90) }
    timed("twoVars(90)") { fibTwoVars(90) }
}
