// Every balanced string of n pairs of brackets, pruned as it is built:
// an opener is allowed while fewer than n are used; a closer only if it has an opener to match.
fun balanced(n: Int): List<String> {
    val out = mutableListOf<String>()
    val sb = StringBuilder()
    fun go(open: Int, close: Int) {
        if (sb.length == 2 * n) { out += sb.toString(); return }
        if (open < n) { sb.append('('); go(open + 1, close); sb.setLength(sb.length - 1) }
        if (close < open) { sb.append(')'); go(open, close + 1); sb.setLength(sb.length - 1) }
    }
    go(0, 0)
    return out
}

fun main() {
    println(balanced(3))
    println((1..10).map { balanced(it).size })          // OEIS A000108: the Catalan numbers
}
