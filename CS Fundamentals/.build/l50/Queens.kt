import kotlin.time.measureTime

// N-queens: place one queen per row so that no two share a column or a diagonal.
class Queens(private val n: Int) {
    private val cols = BooleanArray(n)
    private val diag1 = BooleanArray(2 * n)              // row + col is constant along one diagonal
    private val diag2 = BooleanArray(2 * n)              // row - col + n along the other
    var nodes = 0L                                       // partial placements tried
        private set

    fun count(row: Int = 0): Long {
        nodes++
        if (row == n) return 1
        var total = 0L
        for (c in 0 until n) {
            if (cols[c] || diag1[row + c] || diag2[row - c + n]) continue    // prune: attacked already
            cols[c] = true; diag1[row + c] = true; diag2[row - c + n] = true   // choose
            total += count(row + 1)                                           // explore
            cols[c] = false; diag1[row + c] = false; diag2[row - c + n] = false // un-choose
        }
        return total
    }
}

// No pruning: try all n^n ways to put one queen in each row, check only at the end.
fun bruteCount(n: Int): Pair<Long, Long> {
    val q = IntArray(n)
    var leaves = 0L
    var ok = 0L
    fun go(row: Int) {
        if (row == n) {
            leaves++
            for (i in 0 until n) for (j in i + 1 until n)
                if (q[i] == q[j] || j - i == Math.abs(q[j] - q[i])) return
            ok++
            return
        }
        for (c in 0 until n) { q[row] = c; go(row + 1) }
    }
    go(0)
    return ok to leaves
}

fun main() {
    val oeis = longArrayOf(1, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712)   // OEIS A000170
    println(" n  solutions  OEIS       nodes       ms")
    for (n in 1..13) {
        val q = Queens(n)
        var sols = 0L
        val ms = measureTime { sols = q.count() }.inWholeMicroseconds / 1000.0
        check(sols == oeis[n])
        println("%2d %10d %5s %11d %8.1f".format(n, sols, "ok", q.nodes, ms))
    }
    var r = 0L to 0L
    val ms = measureTime { r = bruteCount(8) }.inWholeMilliseconds
    println("brute force n=8: ${r.first} solutions after checking ${r.second} complete boards ($ms ms)")
}
