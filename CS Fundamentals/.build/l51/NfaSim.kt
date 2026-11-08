import kotlin.time.measureTime

// Thompson's NFA, as in Russ Cox's article: every state is one of three kinds.
//   CHAR: read char c, go to out1    SPLIT: go to out1 AND out2 (an ε-choice)    MATCH: accept
const val CHAR = 0; const val SPLIT = 1; const val MATCH = 2

class Nfa {
    val kind = ArrayList<Int>(); val ch = ArrayList<Char>()
    val out1 = ArrayList<Int>(); val out2 = ArrayList<Int>()
    fun add(k: Int, c: Char = ' ', o1: Int = -1, o2: Int = -1): Int {
        kind += k; ch += c; out1 += o1; out2 += o2; return kind.size - 1
    }
}

// a?{n}a{n}, built back to front: a? is SPLIT(CHAR a -> next, next)
fun coxNfa(n: Int): Pair<Nfa, Int> {
    val nfa = Nfa()
    var next = nfa.add(MATCH)
    repeat(n) { next = nfa.add(CHAR, 'a', next) }
    repeat(n) { val a = nfa.add(CHAR, 'a', next); next = nfa.add(SPLIT, o1 = a, o2 = next) }
    return nfa to next
}

// Track the SET of states we could be in. Never more than one copy of each state.
fun simulate(nfa: Nfa, start: Int, input: String): Boolean {
    val mark = IntArray(nfa.kind.size) { -1 }          // mark[s] == step: s already in this step's set
    var current = IntArray(nfa.kind.size); var size = 0
    var next = IntArray(nfa.kind.size)
    val stack = IntArray(2 * nfa.kind.size + 1)       // each state pushes at most 2 others
    fun addState(list: IntArray, count: Int, s0: Int, step: Int): Int {  // add s0 and its ε-closure
        var n = count; var top = 0
        stack[top++] = s0
        while (top > 0) {
            val s = stack[--top]
            if (mark[s] == step) continue
            mark[s] = step
            if (nfa.kind[s] == SPLIT) { stack[top++] = nfa.out2[s]; stack[top++] = nfa.out1[s] }
            else list[n++] = s
        }
        return n
    }
    size = addState(current, 0, start, 0)
    for ((i, c) in input.withIndex()) {
        var nextSize = 0
        for (k in 0 until size) {
            val s = current[k]
            if (nfa.kind[s] == CHAR && nfa.ch[s] == c) nextSize = addState(next, nextSize, nfa.out1[s], i + 1)
        }
        val t = current; current = next; next = t; size = nextSize
        if (size == 0) return false                      // no state survives: reject early
    }
    return (0 until size).any { nfa.kind[current[it]] == MATCH }
}

fun main() {
    repeat(20) { val (nfa, s) = coxNfa(50); simulate(nfa, s, "a".repeat(50)) }   // warm-up
    for (n in listOf(28, 500, 1000, 2000, 4000)) {
        val (nfa, start) = coxNfa(n)
        var ok = false
        val t = measureTime { ok = simulate(nfa, start, "a".repeat(n)) }
        println("a?{n}a{n}  n=%-5d states=%-5d match=%s  %8.3f ms".format(n, nfa.kind.size, ok, t.inWholeMicroseconds / 1000.0))
    }
}
