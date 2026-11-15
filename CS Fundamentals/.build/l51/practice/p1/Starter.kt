// A DFA as data: table[q] maps an input character to the next state.
class Dfa(val table: List<Map<Char, Int>>, val accepting: Set<Int>, val start: Int = 0) {
    // Every state visited, from `start` to the end of the input,
    // or null if some character has no entry in the table (the DFA rejects).
    fun trace(input: String): List<Int>? = TODO("start at `start`, then look up each next state in the table")

    fun accepts(input: String): Boolean = TODO("true if the trace ends in an accepting state")
}

// Binary numbers divisible by m: state r means "remainder r so far".
fun divisibleBy(m: Int): Dfa = TODO("one map per remainder r: '0' and '1' to their next remainders")
