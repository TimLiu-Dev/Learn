// Backtracking = choose, explore, un-choose. One growing partial solution, shared by every call.

fun subsets(items: List<Char>): List<String> {
    val out = mutableListOf<String>()
    val chosen = StringBuilder()
    fun go(i: Int) {                                     // a local function: it sees items, out, chosen
        if (i == items.size) { out += "{$chosen}"; return }
        go(i + 1)                                        // branch 1: leave items[i] out
        chosen.append(items[i])                          // choose
        go(i + 1)                                        // branch 2: put it in
        chosen.setLength(chosen.length - 1)              // un-choose: undo before returning
    }
    go(0)
    return out
}

fun permutations(items: List<Int>): List<List<Int>> {
    val out = mutableListOf<List<Int>>()
    val current = mutableListOf<Int>()
    val used = BooleanArray(items.size)
    fun go() {
        if (current.size == items.size) { out += current.toList(); return }   // copy! current keeps changing
        for (i in items.indices) {
            if (used[i]) continue
            used[i] = true; current += items[i]          // choose
            go()                                         // explore
            current.removeLast(); used[i] = false        // un-choose
        }
    }
    go()
    return out
}

fun main() {
    println("subsets of abc: ${subsets(listOf('a', 'b', 'c'))}")
    println("permutations of 123: ${permutations(listOf(1, 2, 3))}")
    for (n in listOf(5, 10, 15, 20)) println("n=$n: ${subsets(List(n) { 'a' + it }).size} subsets")
    for (n in 6..9) println("n=$n: ${permutations(List(n) { it }).size} permutations")
}
