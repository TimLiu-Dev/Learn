// How many times does the loop run in the worst case (target not there)?
fun steps(n: Int, target: Int): Int {
    var lo = 0
    var hi = n - 1
    var count = 0
    while (lo <= hi) {
        count++
        val mid = lo + (hi - lo) / 2
        if (mid < target) lo = mid + 1 else hi = mid - 1   // a[i] == i, target never equal
    }
    return count
}

fun main() {
    for (n in listOf(1_000, 1_000_000, 1_000_000_000)) {
        println("n = %,13d   steps = %d".format(n, steps(n, Int.MAX_VALUE)))
    }
}
