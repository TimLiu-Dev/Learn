// The longest substring with at most k distinct characters; the earliest one wins a tie.
fun longestAtMostK(s: String, k: Int): String {
    val counts = HashMap<Char, Int>()                    // char -> how many times it's inside the window
    var left = 0
    var bestStart = 0
    var bestLen = 0
    for ((right, c) in s.withIndex()) {
        TODO("add c to the window, shrink from the left while it has more than k distinct, then update the best")
    }
    return s.substring(bestStart, bestStart + bestLen)
}
