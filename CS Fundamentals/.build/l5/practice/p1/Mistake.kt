fun sameScores(a: IntArray, b: IntArray): Boolean = a.contentEquals(b)

fun isSameList(a: MutableList<Int>, b: MutableList<Int>): Boolean = a == b

fun findName(names: List<String>, typed: String): Int = names.indexOfFirst { it == typed }
