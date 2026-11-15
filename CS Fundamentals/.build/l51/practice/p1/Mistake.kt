class Dfa(val table: List<Map<Char, Int>>, val accepting: Set<Int>, val start: Int = 0) {
    fun trace(input: String): List<Int>? {
        val states = mutableListOf<Int>()
        var q = start
        for (c in input) { q = table[q][c] ?: return null; states += q }
        return states
    }

    fun accepts(input: String): Boolean {
        val states = trace(input) ?: return false
        return (states.lastOrNull() ?: start) in accepting
    }
}

fun divisibleBy(m: Int): Dfa =
    Dfa(List(m) { r -> mapOf('0' to 2 * r % m, '1' to (2 * r + 1) % m) }, setOf(0))
