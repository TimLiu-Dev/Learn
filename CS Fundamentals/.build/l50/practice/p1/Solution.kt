// The longest substring with at most k distinct characters; the earliest one wins a tie.
fun longestAtMostK(s: String, k: Int): String {
    val counts = HashMap<Char, Int>()                    // char -> how many times it's inside the window
    var left = 0
    var bestStart = 0
    var bestLen = 0
    for ((right, c) in s.withIndex()) {
        counts[c] = (counts[c] ?: 0) + 1                 // grow on the right
        while (counts.size > k) {                        // too many distinct: shrink from the left
            val out = s[left++]
            val n = counts.getValue(out) - 1
            if (n == 0) counts.remove(out) else counts[out] = n   // a zero count must leave the map
        }
        if (right - left + 1 > bestLen) { bestLen = right - left + 1; bestStart = left }   // valid again: now record
    }
    return s.substring(bestStart, bestStart + bestLen)
}
