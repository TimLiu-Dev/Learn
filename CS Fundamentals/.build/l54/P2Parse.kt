fun main() {
    for (src in listOf("1 + 2 * 3", "(1 + 2) * 3", "8 - 3 - 2", "2 ^ 3 ^ 2", "-2 ^ 2",
                       "let x = 3; x * (x + 1)")) {
        println(src.padEnd(24) + show(parse(src)).replace("\n", "; "))
    }
    println(parse("8 - 3 - 2").result)
    for (bad in listOf("(1 + 2", "1 + * 2", "let = 4; 1", "1 2")) {
        try { parse(bad) } catch (e: CalcError) { println(bad.padEnd(12) + "CalcError: ${e.message}") }
    }
}
