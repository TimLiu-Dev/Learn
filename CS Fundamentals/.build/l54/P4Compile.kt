fun main() {
    val src = "let x = 3; x * (x + 1)"
    val chunk = Compiler().compile(parse(src))
    print(disassemble(chunk))
    println("result = ${execute(chunk)}, interpreter says ${interpret(parse(src))}")

    for (s in listOf("1 + (2 * (3 - 4))", "((1 + 2) * 3) - 4")) {
        println(s.padEnd(20) + "maxStack=" + Compiler().compile(parse(s)).maxStack)
    }
    for (bad in listOf("let a = 1; a + b", "10 / (5 - 5)")) {
        try { execute(Compiler().compile(parse(bad))) }
        catch (e: CalcError) { println(bad.padEnd(20) + "CalcError: ${e.message}") }
    }
}
