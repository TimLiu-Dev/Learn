fun main() {
    for (src in listOf("1 + 2 * 3", "2 ^ 3 ^ 2", "-2 ^ 2", "let x = 3; x * (x + 1)",
                       "let x = 5; let x = x * x; x - 1", "2147483647 + 1", "7 / 2 + -7 % 2")) {
        println(src.padEnd(32) + "= " + interpret(parse(src)))
    }
    for (bad in listOf("let a = 1; a + b", "10 / (5 - 5)", "2 ^ -1")) {
        try { interpret(parse(bad)) } catch (e: CalcError) { println(bad.padEnd(32) + "CalcError: ${e.message}") }
    }
}
