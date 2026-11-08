// Sliding windows: a range [left, right] that moves through the input instead of restarting.

// Fixed size k: add the element entering on the right, subtract the one leaving on the left.
fun maxSumOfK(a: IntArray, k: Int): Int {
    require(k in 1..a.size) { "k must be in 1..${a.size}" }
    var sum = 0
    for (i in 0 until k) sum += a[i]                     // the first window
    var best = sum
    for (right in k until a.size) {
        sum += a[right] - a[right - k]                   // slide by one: O(1), not O(k)
        best = maxOf(best, sum)
    }
    return best
}

// Variable size: grow on the right; when the window breaks the rule, shrink from the left.
// Rule here: no character repeats inside the window.
fun longestUnique(s: String, trace: Boolean = false): String {
    val lastSeen = HashMap<Char, Int>()                  // char -> index of its latest occurrence
    var left = 0
    var bestStart = 0
    var bestLen = 0
    for ((right, c) in s.withIndex()) {
        val prev = lastSeen[c]
        if (prev != null && prev >= left) left = prev + 1    // jump past the earlier copy of c
        lastSeen[c] = right
        if (right - left + 1 > bestLen) { bestLen = right - left + 1; bestStart = left }
        if (trace) println("  right=$right '$c'  window=\"${s.substring(left, right + 1)}\"")
    }
    return s.substring(bestStart, bestStart + bestLen)
}

fun main() {
    val temps = intArrayOf(3, 8, 1, 9, 4, 6, 2, 7)
    println("best 3-day total: ${maxSumOfK(temps, 3)}")
    println("longest without repeats:")
    println("-> \"${longestUnique("abcabcbb", trace = true)}\"")
    println("\"${longestUnique("pwwkew")}\" \"${longestUnique("")}\" \"${longestUnique("aaaa")}\"")
}
