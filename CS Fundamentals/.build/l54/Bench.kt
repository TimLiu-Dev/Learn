import kotlin.time.measureTime

// A long straight-line program: 300 lets, each built from earlier variables.
fun bigProgram(n: Int): String = buildString {
    append("let v0 = 7;\n")
    for (i in 1 until n) append("let v$i = v${i - 1} * 3 + v${i / 2} % 7 - ${i % 10};\n")
    append("v${n - 1}")
}

fun main() {
    val program = parse(bigProgram(300))
    val chunk = Compiler().compile(program)
    println("${chunk.code.size} ints of bytecode, maxStack=${chunk.maxStack}, locals=${chunk.locals.size}")
    var sink = 0L                                       // use every result, so the JIT can't skip the work (L16)
    repeat(3) { round ->
        val tTree = measureTime { repeat(20_000) { sink += interpret(program) } }
        val tVm = measureTime { repeat(20_000) { sink += execute(chunk) } }
        println("round ${round + 1}: tree-walk ${tTree.inWholeMilliseconds} ms, VM ${tVm.inWholeMilliseconds} ms, " +
                "ratio %.1f".format(tTree / tVm))
    }
    println("results agree: ${interpret(program) == execute(chunk)} (sink $sink)")
}
