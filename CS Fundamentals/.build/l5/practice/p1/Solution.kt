// Arrays don't override equals, so compare their elements.
fun sameScores(a: IntArray, b: IntArray): Boolean = a.contentEquals(b)

// A change shows through both only if they are one object: identity.
fun isSameList(a: MutableList<Int>, b: MutableList<Int>): Boolean = a === b

// String overrides equals, and Kotlin's == calls it.
fun findName(names: List<String>, typed: String): Int = names.indexOfFirst { it == typed }
