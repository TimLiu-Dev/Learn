data class Move(val from: Int, val c: Char, val to: Int)

// An NFA: character moves, plus ε-moves (from, to) taken without reading anything.
class Nfa(val start: Int, val accept: Int, val moves: List<Move>, val eps: List<Pair<Int, Int>>)

// The states themselves, plus everything reachable from them by ε-moves alone.
fun closure(nfa: Nfa, states: Set<Int>): Set<Int> {
    val seen = states.toMutableSet()
    val todo = ArrayDeque(states)
    while (todo.isNotEmpty()) {
        val s = todo.removeLast()
        for ((from, to) in nfa.eps) if (from == s && seen.add(to)) todo.addLast(to)   // add() is false if seen
    }
    return seen
}

// Read c from every state in the set at once, then take the closure.
fun step(nfa: Nfa, states: Set<Int>, c: Char): Set<Int> =
    closure(nfa, nfa.moves.filter { it.from in states && it.c == c }.map { it.to }.toSet())

fun matches(nfa: Nfa, input: String): Boolean =
    nfa.accept in input.fold(closure(nfa, setOf(nfa.start))) { states, c -> step(nfa, states, c) }

// a?{n}a{n} (Cox's pattern from Part 2): states 0..2n, and each of the first n a's may be skipped.
fun coxNfa(n: Int) = Nfa(
    start = 0, accept = 2 * n,
    moves = (0 until 2 * n).map { Move(it, 'a', it + 1) },
    eps = (0 until n).map { it to it + 1 },
)
