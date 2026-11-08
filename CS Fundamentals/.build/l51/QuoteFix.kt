import kotlin.time.measureTime

// A quoted phrase: the same quote at both ends, words separated by single spaces.
val evil = Regex("""(['"])(\w+\s?)*\1""")
val possessive = Regex("""(['"])(\w+\s?)*+\1""")      // *+ : never give back what the loop took
val unambiguous = Regex("""(['"])(\w+(\s\w+)*\s?)?\1""")  // only one way to split any input

fun main() {
    // 1. Same language? Compare on every string of length <= 7 over a small alphabet.
    val alphabet = "a \"'!"
    var all = listOf("")
    var checked = 0
    repeat(7) {
        all = all.flatMap { p -> alphabet.map { p + it } }
        for (s in all) {
            checked++
            val e = evil.matches(s)
            check(e == possessive.matches(s) && e == unambiguous.matches(s)) { "differ on [$s]" }
        }
    }
    println("all three agree on $checked strings")
    // 2. Same speed? The evil input: an opening quote, n letters, no closing quote.
    repeat(3) { for (r in listOf(evil, possessive, unambiguous)) r.matches("\"" + "a".repeat(12) + "!") }
    for (r in listOf(evil, possessive, unambiguous)) {
        print("%-28s".format(r.pattern))
        for (n in listOf(20, 24, 28)) {
            val t = measureTime { r.matches("\"" + "a".repeat(n) + "!") }
            print("  n=%d %6d ms".format(n, t.inWholeMilliseconds))
        }
        println()
    }
}
