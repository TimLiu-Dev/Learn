package katas

// Recursive descent (L18), one function per grammar rule:
//   alt    := concat ('|' concat)*
//   concat := repeat*                       (zero repeats is the empty pattern)
//   repeat := atom ('*' | '+' | '?')?
//   atom   := '(' alt ')' | '.' | '\' char | any other char
class Parser(private val p: String) {
    private var pos = 0

    fun parse(): Node {
        val tree = alt()
        if (pos < p.length) fail("unexpected '${p[pos]}'")    // only a stray ')' can stop us early
        return tree
    }

    private fun alt(): Node {
        var node = concat()
        while (pos < p.length && p[pos] == '|') { pos++; node = Node.Alt(node, concat()) }
        return node
    }

    private fun concat(): Node {
        var node: Node? = null
        while (pos < p.length && p[pos] != '|' && p[pos] != ')') {
            val next = repeat()
            node = if (node == null) next else Node.Cat(node, next)
        }
        return node ?: Node.Empty
    }

    private fun repeat(): Node {
        val atom = atom()
        if (pos == p.length || p[pos] !in "*+?") return atom
        val op = p[pos++]
        if (pos < p.length && p[pos] in "*+?") fail("'${p[pos]}' after '$op' is not supported")  // lazy/possessive
        return when (op) { '*' -> Node.Star(atom); '+' -> Node.Plus(atom); else -> Node.Opt(atom) }
    }

    private fun atom(): Node = when (val c = p[pos]) {
        '(' -> {
            pos++
            val inner = alt()
            if (pos == p.length || p[pos] != ')') fail("missing ')'")
            pos++
            inner
        }
        '.' -> { pos++; Node.AnyChar }
        '\\' -> { pos++; if (pos == p.length) fail("nothing after '\\'"); Node.Lit(p[pos++]) }
        '*', '+', '?' -> fail("nothing to repeat before '$c'")
        '[', ']', '{', '}', '^', '$' -> fail("'$c' is not supported")
        else -> { pos++; Node.Lit(c) }
    }

    private fun fail(message: String): Nothing =
        throw IllegalArgumentException("$message at index $pos in \"$p\"")
}
