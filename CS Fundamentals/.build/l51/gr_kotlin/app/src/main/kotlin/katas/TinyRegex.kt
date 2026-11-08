package katas

// Thompson's construction: the syntax tree becomes an NFA with at most two states per node,
// and matching tracks the SET of states the NFA could be in (Part 2). Time O(pattern × input).
class TinyRegex(val pattern: String) {
    private companion object {
        const val CHAR = 0; const val ANY = 1; const val EPS = 2; const val SPLIT = 3; const val MATCH = 4
    }

    private val kind = ArrayList<Int>(); private val ch = ArrayList<Char>()
    private val out1 = ArrayList<Int>(); private val out2 = ArrayList<Int>()
    private val start: Int

    // A piece of NFA with one way in and one EPS state whose exit is still unconnected
    private class Frag(val start: Int, val end: Int)

    private fun add(k: Int, c: Char = ' ', o1: Int = -1, o2: Int = -1): Int {
        kind += k; ch += c; out1 += o1; out2 += o2; return kind.size - 1
    }

    private fun build(node: Node): Frag = when (node) {    // exhaustive: the compiler checks every kind
        Node.Empty -> add(EPS).let { Frag(it, it) }
        is Node.Lit -> { val e = add(EPS); Frag(add(CHAR, node.c, e), e) }
        Node.AnyChar -> { val e = add(EPS); Frag(add(ANY, o1 = e), e) }
        is Node.Cat -> { val l = build(node.left); val r = build(node.right); out1[l.end] = r.start; Frag(l.start, r.end) }
        is Node.Alt -> {
            val l = build(node.left); val r = build(node.right); val e = add(EPS)
            out1[l.end] = e; out1[r.end] = e
            Frag(add(SPLIT, o1 = l.start, o2 = r.start), e)
        }
        is Node.Star -> { val b = build(node.body); val e = add(EPS); val s = add(SPLIT, o1 = b.start, o2 = e); out1[b.end] = s; Frag(s, e) }
        is Node.Plus -> { val b = build(node.body); val e = add(EPS); out1[b.end] = add(SPLIT, o1 = b.start, o2 = e); Frag(b.start, e) }
        is Node.Opt -> { val b = build(node.body); val e = add(EPS); out1[b.end] = e; Frag(add(SPLIT, o1 = b.start, o2 = e), e) }
    }

    init {
        val whole = build(Parser(pattern).parse())
        out1[whole.end] = add(MATCH)
        start = whole.start
    }

    val stateCount get() = kind.size

    fun matches(input: CharSequence): Boolean {
        val n = kind.size
        val mark = IntArray(n) { -1 }                     // mark[s] == step: s is already in this step's set
        val stack = IntArray(2 * n + 1)                   // each state pushes at most 2 others
        var current = IntArray(n); var next = IntArray(n)

        fun addWithClosure(list: IntArray, count: Int, s0: Int, step: Int): Int {   // follow ε-moves, no recursion
            var size = count; var top = 0
            stack[top++] = s0
            while (top > 0) {
                val s = stack[--top]
                if (mark[s] == step) continue
                mark[s] = step
                when (kind[s]) {
                    EPS -> stack[top++] = out1[s]
                    SPLIT -> { stack[top++] = out2[s]; stack[top++] = out1[s] }
                    else -> list[size++] = s                  // CHAR, ANY, MATCH: states that wait for input
                }
            }
            return size
        }

        var size = addWithClosure(current, 0, start, 0)
        for (i in input.indices) {
            val c = input[i]
            var nextSize = 0
            for (k in 0 until size) {
                val s = current[k]
                if ((kind[s] == CHAR && ch[s] == c) || kind[s] == ANY) nextSize = addWithClosure(next, nextSize, out1[s], i + 1)
            }
            val t = current; current = next; next = t; size = nextSize
            if (size == 0) return false
        }
        return (0 until size).any { kind[current[it]] == MATCH }
    }
}
