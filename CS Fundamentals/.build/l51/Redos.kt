import kotlin.time.measureTime

// One evil input per pattern: a run of n letters that ends in a character that can't match.
class Case(val label: String, val regex: (Int) -> Regex, val input: (Int) -> String)

val cases = listOf(
    Case("""(\w+\s?)*""", { Regex("""(\w+\s?)*""") }, { "a".repeat(it) + "!" }),
    Case("""(['"])(\w+\s?)*\1""", { Regex("""(['"])(\w+\s?)*\1""") }, { "\"" + "a".repeat(it) + "!" }),
    Case("""(\w+\s?){1,30}""", { Regex("""(\w+\s?){1,30}""") }, { "a".repeat(it) + "!" }),
    Case("a?{n}a{n}", { n -> Regex("a?".repeat(n) + "a".repeat(n)) }, { "a".repeat(it) }),
)

fun main() {
    repeat(3) { for (c in cases) c.regex(12).matches(c.input(12)) }   // warm up the JIT (L16)
    for (c in cases) {
        print("%-20s".format(c.label))
        for (n in 16..28 step 4) {
            val re = c.regex(n)
            val input = c.input(n)
            var result = false
            val t = measureTime { result = re.matches(input) }      // whole-string match
            print("  n=%d %s %6d ms".format(n, if (result) "yes" else "no ", t.inWholeMilliseconds))
        }
        println()
    }
}
