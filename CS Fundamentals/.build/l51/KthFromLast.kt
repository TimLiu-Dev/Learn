// NFA for "the k-th symbol from the end is 'a'", alphabet {a, b}.
// States 0..k. State 0 loops on a and b, and on 'a' it may also guess "this is the one"
// and move to 1. States 1..k-1 move on any symbol. State k accepts.
fun nfaStep(states: Int, c: Char, k: Int): Int {    // a set of states, as bits of an Int (L48)
    var next = 0
    for (q in 0..k) {
        if (states and (1 shl q) == 0) continue
        when {
            q == 0 -> { next = next or 1; if (c == 'a') next = next or (1 shl 1) }
            q < k -> next = next or (1 shl (q + 1))
        }                                           // state k has no moves: it just accepts
    }
    return next
}

fun nfaAccepts(s: String, k: Int): Boolean {
    var states = 1                                  // {0}
    for (c in s) states = nfaStep(states, c, k)
    return states and (1 shl k) != 0
}

fun showSet(states: Int, k: Int) = (0..k).filter { states and (1 shl it) != 0 }.joinToString(",", "{", "}")

// Subset construction: each DFA state is a SET of NFA states. Count the reachable ones.
fun dfaStatesNeeded(k: Int): Int {
    val seen = HashSet<Int>()
    val todo = ArrayDeque<Int>()
    seen.add(1); todo.addLast(1)
    while (todo.isNotEmpty()) {
        val set = todo.removeFirst()
        for (c in "ab") { val next = nfaStep(set, c, k); if (seen.add(next)) todo.addLast(next) }
    }
    return seen.size
}

fun main() {
    val k = 3
    for (s in listOf("babb", "abab")) {
        var states = 1
        val trace = StringBuilder(showSet(states, k))
        for (c in s) { states = nfaStep(states, c, k); trace.append(" -$c-> ").append(showSet(states, k)) }
        println("$s: $trace  accept=${nfaAccepts(s, k)}")
    }
    println(" k  NFA states  DFA states")
    for (kk in 1..12) println("%2d  %10d  %10d".format(kk, kk + 1, dfaStatesNeeded(kk)))
}
