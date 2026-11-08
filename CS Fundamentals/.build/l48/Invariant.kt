// Invariant: at the top of every pass, best == the largest of a[0 until i].
fun largest(a: IntArray): Int {
    require(a.isNotEmpty())
    var best = a[0]
    var i = 1
    while (i < a.size) {
        check(best == a.copyOfRange(0, i).max()) { "invariant broken at i=$i" }
        if (a[i] > best) best = a[i]
        i++
    }
    // The loop ended, so i == a.size: the invariant now says best is the largest of all of a.
    return best
}

// Same loop with one bug: best starts at 0 instead of a[0].
fun largestBuggy(a: IntArray): Int {
    var best = 0
    var i = 1
    while (i < a.size) {
        check(best == a.copyOfRange(0, i).max()) { "invariant broken at i=$i: best=$best" }
        if (a[i] > best) best = a[i]
        i++
    }
    return best
}

fun main() {
    val temps = intArrayOf(-5, -2, -9)
    println("largest = ${largest(temps)}")
    println("largestBuggy = ${largestBuggy(temps)}")
}
