// A lazy backtracking search: the sequence builder pauses at every yield,
// so take(3) builds 3 permutations of 10 items, not all 3,628,800.
var built = 0

fun permutationsOf(items: List<Int>): Sequence<List<Int>> = sequence {
    val current = mutableListOf<Int>()
    val used = BooleanArray(items.size)
    suspend fun SequenceScope<List<Int>>.go() {
        if (current.size == items.size) { built++; yield(current.toList()); return }
        for (i in items.indices) {
            if (used[i]) continue
            used[i] = true; current += items[i]
            go()
            current.removeLast(); used[i] = false
        }
    }
    go()
}

fun main() {
    val first3 = permutationsOf((1..10).toList()).take(3).toList()
    first3.forEach(::println)
    println("permutations built: $built")
    println("first with 10 at the front: ${permutationsOf((1..10).toList()).first { it[0] == 10 }}")
    println("permutations built: $built")
}
