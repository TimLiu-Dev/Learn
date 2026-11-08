// Any DFA over {0, 1}: states are 0 until states, start is 0.
class Dfa(val states: Int, val accepting: Set<Int>, val step: (Int, Char) -> Int) {
    fun accepts(s: String) = s.fold(0, step) in accepting
}

// A DFA that tries to accept 0^n 1^n by counting zeros, but can only count up to `max`.
// States: 0..max = "zeros seen so far" (saturating), max+1..2max+1 = "ones still needed", 2max+2 = dead.
fun countingDfa(max: Int): Dfa {
    val need = { k: Int -> max + 1 + k }
    val dead = 2 * max + 2
    return Dfa(2 * max + 3, setOf(0, need(0))) { q, c ->
        when {
            q == dead -> dead
            q <= max && c == '0' -> minOf(q + 1, max)            // zeros phase: count (saturating)
            q <= max -> if (q == 0) dead else need(q - 1)         // first 1: that many ones to go
            c == '1' && q > need(0) -> q - 1                      // ones phase: one fewer to go
            else -> dead
        }
    }
}

// Pigeonhole (L48): the k+1 prefixes 0^0 .. 0^k can't all land in different states of a k-state DFA.
fun fool(dfa: Dfa): String {
    val firstSeen = HashMap<Int, Int>()                  // state -> i of the first 0^i that reached it
    for (j in 0..dfa.states) {
        val q = "0".repeat(j).fold(0, dfa.step)
        val i = firstSeen.putIfAbsent(q, j) ?: continue
        // 0^i and 0^j end in the same state, so 0^i 1^i and 0^j 1^i get the same verdict.
        // Exactly one of them is in the language, so the DFA is wrong about one.
        val right = "0".repeat(i) + "1".repeat(i)
        return if (dfa.accepts(right)) "0".repeat(j) + "1".repeat(i) else right
    }
    error("impossible: more prefixes than states")
}

fun main() {
    for (max in listOf(3, 14)) {
        val dfa = countingDfa(max)
        val s = fool(dfa)
        val zeros = s.count { it == '0' }
        val ones = s.length - zeros
        println("${dfa.states}-state DFA: fooled by 0^$zeros 1^$ones, says ${dfa.accepts(s)}, truth ${zeros == ones}")
    }
}
