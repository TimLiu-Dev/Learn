package katas

// ./gradlew run --args="let x = 3; x * (x + 1)" shows every stage of the pipeline.
fun main(args: Array<String>) {
    val src = args.joinToString(" ").ifBlank { "let x = 3; x * (x + 1)" }
    println("source:      $src")
    println("tokens:      " + Lexer(src).tokens().joinToString(" ") { if (it.type == T.EOF) "EOF" else it.text })
    println("tree:        " + show(parse(src)).replace("\n", "; "))
    println("bytecode:")
    disassemble(Compiler().compile(parse(src))).lines().filter { it.isNotEmpty() }.forEach { println("  $it") }
    println("interpreted: ${runInterpreted(src)}")
    println("compiled:    ${runCompiled(src)}")
}
