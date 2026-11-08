fun main() {
    val src = """
        let width = 12;   // a comment
        let area = width * (width - 2);
        area / 4
    """.trimIndent()
    Lexer(src).tokens().forEach(::println)

    for (bad in listOf("let x = 3 # 4", "99999999999 + 1", "a//b\n/ c", "letter + let1")) {
        val shown = bad.replace("\n", "\\n")
        try {
            println("$shown -> ${Lexer(bad).tokens().map { if (it.type == T.EOF) "EOF" else "${it.type} ${it.text}" }}")
        } catch (e: CalcError) {
            println("$shown -> CalcError: ${e.message}")
        }
    }
}
