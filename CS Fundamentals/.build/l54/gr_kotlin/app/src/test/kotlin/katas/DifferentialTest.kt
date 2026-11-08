package katas

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

// Generates random programs, then checks two independent properties on each one.
class DifferentialTest {
    // One generated expression: its source text, and the tree the parser should build for it.
    private data class Gen(val src: String, val tree: String)

    private val names = listOf("a", "b", "c", "d")
    private val ops = listOf("+", "-", "*", "+", "-", "*", "/", "%", "^")   // the risky three less often

    // Mostly names that are already defined: blind picks made 86% of programs end in an error.
    private fun expr(r: Random, depth: Int, defined: List<String>): Gen =
        when (if (depth == 0) r.nextInt(2) else r.nextInt(5)) {
            0 -> r.nextInt(10).toString().let { Gen(it, it) }
            1 -> when {                                                   // 1 in 40 names may be undefined
                r.nextInt(40) == 0 -> names.random(r)
                defined.isEmpty() -> r.nextInt(10).toString()
                else -> defined.random(r)
            }.let { Gen(it, it) }
            2 -> expr(r, depth - 1, defined).let { Gen("(-${it.src})", "(neg ${it.tree})") }   // parenthesised: -x ^ y is -(x ^ y)
            else -> {
                val (l, rt) = expr(r, depth - 1, defined) to expr(r, depth - 1, defined)
                val op = ops.random(r)
                Gen("(${l.src} $op ${rt.src})", "($op ${l.tree} ${rt.tree})")
            }
        }

    private fun program(r: Random): Pair<String, List<String>> {
        val defined = mutableListOf<String>()
        val lets = List(r.nextInt(4)) {
            val name = names.random(r)
            (name to expr(r, 3, defined.toList())).also { defined += name }
        }
        val result = expr(r, 3, defined)
        val src = lets.joinToString("") { (n, e) -> "let $n = ${e.src}; " } + result.src
        return src to (lets.map { (n, e) -> "let $n = ${e.tree}" } + result.tree)
    }

    @Test fun `parser builds exactly the generated tree`() {
        val r = Random(54)
        repeat(10_000) {
            val (src, expected) = program(r)
            assertEquals(expected.joinToString("\n"), show(parse(src)), src)
        }
    }

    @Test fun `interpreter and compiled code agree on 10,000 programs`() {
        val r = Random(54)
        val kinds = sortedMapOf<String, Int>()
        repeat(10_000) {
            val (src, _) = program(r)
            val expected = runInterpreted(src)
            val kind = if (expected is Outcome.Error) expected.message.substringBefore(" at") else "value"
            kinds.merge(kind.substringBefore(" '"), 1, Int::plus)
            assertEquals(expected, runCompiled(src), src)
        }
        println("10,000 programs agree: $kinds")
    }
}
