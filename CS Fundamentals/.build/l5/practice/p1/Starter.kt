// Do the two score sheets hold the same numbers, in the same order?
fun sameScores(a: IntArray, b: IntArray): Boolean = a == b

// Would a change made through `a` show up through `b`?
fun isSameList(a: MutableList<Int>, b: MutableList<Int>): Boolean = a == b

// Where is the name the user typed? -1 if it isn't there.
fun findName(names: List<String>, typed: String): Int = names.indexOfFirst { it === typed }
