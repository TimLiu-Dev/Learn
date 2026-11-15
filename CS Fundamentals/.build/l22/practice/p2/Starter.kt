// Visiting order of an n x n grid stored flat: cell (r, c) is at index r * n + c.
fun rowMajor(n: Int): List<Int> = (0 until n).flatMap { r -> (0 until n).map { c -> r * n + c } }

fun columnMajor(n: Int): List<Int> = TODO("outer loop over columns, inner loop over rows")

// Replay the accesses through an LRU cache holding `lines` cache lines of `intsPerLine` ints.
fun misses(indices: List<Int>, intsPerLine: Int, lines: Int): Int =
    TODO("work out each index's line; a line not in the cache is a miss")
