// A DFA as data: table[q] maps an input character to the next state.
class Dfa(val table: List<Map<Char, Int>>, val accepting: Set<Int>, val start: Int = 0) {
    fun trace(input: String): List<Int>? {
        val states = mutableListOf(start)                          // n characters visit n + 1 states
        for (c in input) states += table[states.last()][c] ?: return null   // no move: reject
        return states
    }

    fun accepts(input: String): Boolean {
        val states = trace(input) ?: return false
        return states.last() in accepting
    }
}

// Binary numbers divisible by m: state r means "remainder r so far".
fun divisibleBy(m: Int): Dfa =
    Dfa(List(m) { r -> mapOf('0' to 2 * r % m, '1' to (2 * r + 1) % m) }, setOf(0))
