import kotlin.random.Random

// Stress-test three greedy rules for "most non-overlapping meetings" (L49) against
// a backtracking brute force that tries every subset (Part 2).
data class Meeting(val start: Int, val end: Int)         // half-open [start, end)

fun bruteMax(ms: List<Meeting>): Int {
    var best = 0
    val chosen = mutableListOf<Meeting>()
    fun go(i: Int) {
        if (i == ms.size) { best = maxOf(best, chosen.size); return }
        go(i + 1)                                        // skip ms[i]
        if (chosen.all { it.end <= ms[i].start || ms[i].end <= it.start }) {   // prune: only compatible picks
            chosen += ms[i]; go(i + 1); chosen.removeLast()
        }
    }
    go(0)
    return best
}

fun greedy(ms: List<Meeting>, order: Comparator<Meeting>): Int {
    var count = 0
    var freeFrom = Int.MIN_VALUE
    for (m in ms.sortedWith(order)) if (m.start >= freeFrom) { count++; freeFrom = m.end }
    return count
}

fun greedyAny(ms: List<Meeting>, order: Comparator<Meeting>): Int {   // for rules that don't scan left to right
    val chosen = mutableListOf<Meeting>()
    for (m in ms.sortedWith(order)) if (chosen.all { it.end <= m.start || m.end <= it.start }) chosen += m
    return chosen.size
}

fun main() {
    val rules = linkedMapOf<String, (List<Meeting>) -> Int>(
        "earliest finish" to { ms -> greedy(ms, compareBy { it.end }) },
        "earliest start" to { ms -> greedy(ms, compareBy { it.start }) },
        "shortest first" to { ms -> greedyAny(ms, compareBy { it.end - it.start }) },
    )
    for ((name, rule) in rules) {
        val rnd = Random(50)
        var bad: List<Meeting>? = null
        for (t in 0 until 5_000) {
            val n = rnd.nextInt(1, minOf(8, 2 + t / 500))    // small inputs first
            val ms = List(n) { val s = rnd.nextInt(0, 10); Meeting(s, s + rnd.nextInt(1, 5)) }
            if (rule(ms) != bruteMax(ms)) { bad = ms; break }
        }
        println("%-15s %s".format(name, bad?.let { "WRONG on $it: greedy ${rule(it)}, best ${bruteMax(it)}" } ?: "5000 trials, always optimal"))
    }
}
