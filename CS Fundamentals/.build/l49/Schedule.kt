import java.util.Random

// Half-open [start, end): a class may start the minute another one ends.
data class Interval(val name: String, val start: Int, val end: Int)

fun overlaps(a: Interval, b: Interval) = a.start < b.end && b.start < a.end

// Every greedy rule here has the same shape: sort by a key, never look back.
fun greedy(classes: List<Interval>, key: (Interval) -> Int): List<Interval> {
    val chosen = mutableListOf<Interval>()
    for (c in classes.sortedBy(key))
        if (chosen.none { overlaps(it, c) }) chosen += c
    return chosen
}

// The truth, by brute force: try all 2^n subsets (bit i of mask = take class i).
fun bestSize(classes: List<Interval>): Int {
    var best = 0
    for (mask in 0 until (1 shl classes.size)) {
        val pick = classes.filterIndexed { i, _ -> mask and (1 shl i) != 0 }
        val clashFree = pick.indices.all { i -> (i + 1 until pick.size).none { j -> overlaps(pick[i], pick[j]) } }
        if (clashFree) best = maxOf(best, pick.size)
    }
    return best
}

val rules: Map<String, (Interval) -> Int> = linkedMapOf(
    "earliest start" to { it.start },
    "shortest first" to { it.end - it.start },
    "earliest finish" to { it.end },
)

fun main() {
    val trap1 = listOf(Interval("long", 0, 10), Interval("a", 1, 2), Interval("b", 3, 4))
    val trap2 = listOf(Interval("x", 0, 5), Interval("short", 4, 7), Interval("y", 6, 11))
    for ((name, key) in rules) {
        println("%-16s trap1 -> %s   trap2 -> %s".format(name,
            greedy(trap1, key).map { it.name }, greedy(trap2, key).map { it.name }))
    }

    val rnd = Random(49)
    val wins = rules.keys.associateWith { 0 }.toMutableMap()
    val trials = 10_000
    repeat(trials) {
        val classes = List(8) { i ->
            val s = rnd.nextInt(20)
            Interval("c$i", s, s + 1 + rnd.nextInt(8))
        }
        val best = bestSize(classes)
        for ((name, key) in rules)
            if (greedy(classes, key).size == best) wins[name] = wins.getValue(name) + 1
    }
    println("random tests: $trials schedules of 8 classes each")
    for ((name, w) in wins) println("%-16s optimal in %5d of %d".format(name, w, trials))
}
