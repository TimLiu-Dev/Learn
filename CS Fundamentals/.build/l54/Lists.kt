// Exercise 2: a recursive-descent parser for nested lists of numbers.
//   list := "[" ( item ( "," item )* )? "]"
//   item := NUMBER | list
sealed interface Item
data class Leaf(val value: Int) : Item
data class Nest(val items: List<Item>) : Item

class ListParser(private val src: String) {
    private var i = 0

    fun parse(): Nest {
        val result = list()
        skipSpaces()
        if (i < src.length) throw CalcError("unexpected '${src[i]}' at col ${i + 1}")
        return result
    }

    private fun list(): Nest {
        expect('[')
        val items = mutableListOf<Item>()
        skipSpaces()
        if (peek() != ']') {
            items += item()
            while (peek() == ',') { i++; items += item() }
        }
        expect(']')
        return Nest(items)
    }

    private fun item(): Item {
        skipSpaces()
        return when (peek()) {
            '[' -> list()
            in '0'..'9' -> {
                val start = i
                while (peek() in '0'..'9') i++
                Leaf(src.substring(start, i).toInt())
            }
            else -> throw CalcError("expected a number or '[' at col ${i + 1}")
        }
    }

    private fun expect(c: Char) {
        skipSpaces()
        if (peek() != c) throw CalcError("expected '$c' at col ${i + 1}")
        i++
        skipSpaces()
    }

    private fun skipSpaces() { while (peek() == ' ') i++ }
    private fun peek() = if (i < src.length) src[i] else '\u0000'
}

fun sum(item: Item): Int = when (item) {
    is Leaf -> item.value
    is Nest -> item.items.sumOf { sum(it) }
}

fun depth(item: Item): Int = when (item) {
    is Leaf -> 0
    is Nest -> 1 + (item.items.maxOfOrNull { depth(it) } ?: 0)
}

fun main() {
    for (s in listOf("[1, [2, 3], []]", "[[[[7]]]]", "[]", "[1, 2", "[1,, 2]", "[1] 2")) {
        try {
            val tree = ListParser(s).parse()
            println("%-16s sum=%d depth=%d".format(s, sum(tree), depth(tree)))
        } catch (e: CalcError) {
            println("%-16s CalcError: %s".format(s, e.message))
        }
    }
}
