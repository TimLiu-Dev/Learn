// longestUnique's length, without the `prev >= left` check
fun longest(s: String): Int {
    val lastSeen = HashMap<Char, Int>()
    var left = 0
    var best = 0
    for ((right, c) in s.withIndex()) {
        val prev = lastSeen[c]
        if (prev != null) left = prev + 1
        lastSeen[c] = right
        best = maxOf(best, right - left + 1)
    }
    return best
}

fun main() {
    println(longest("abba"))
}
