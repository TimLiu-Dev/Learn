data class Move(val from: Int, val c: Char, val to: Int)

class Nfa(val start: Int, val accept: Int, val moves: List<Move>, val eps: List<Pair<Int, Int>>)

// Only one ε-move from each state: misses chains like 0 -> 1 -> 2
fun closure(nfa: Nfa, states: Set<Int>): Set<Int> =
    states + nfa.eps.filter { it.first in states }.map { it.second }

fun step(nfa: Nfa, states: Set<Int>, c: Char): Set<Int> =
    closure(nfa, nfa.moves.filter { it.from in states && it.c == c }.map { it.to }.toSet())

fun matches(nfa: Nfa, input: String): Boolean =
    nfa.accept in input.fold(closure(nfa, setOf(nfa.start))) { states, c -> step(nfa, states, c) }

fun coxNfa(n: Int) = Nfa(
    start = 0, accept = 2 * n,
    moves = (0 until 2 * n).map { Move(it, 'a', it + 1) },
    eps = (0 until n).map { it to it + 1 },
)
