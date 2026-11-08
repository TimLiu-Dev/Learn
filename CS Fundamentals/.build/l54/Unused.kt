// Exercise 3: warn about a `let` whose value is never read. A second walk over the same tree.
fun vars(e: Expr): List<String> = when (e) {
    is Num -> emptyList()
    is Var -> listOf(e.name)
    is Neg -> vars(e.operand)
    is Bin -> vars(e.left) + vars(e.right)
}

fun unusedLets(p: Program): List<String> {
    val warnings = mutableListOf<String>()
    val current = HashMap<String, Int>()                // name -> index of the let that set it
    val read = HashSet<Int>()                           // lets whose value somebody read
    fun reads(e: Expr) = vars(e).forEach { name -> current[name]?.let { read += it } }

    for ((i, let) in p.lets.withIndex()) {
        reads(let.value)                                // `let x = x + 1` reads the old x first
        current[let.name]?.let { old -> if (old !in read) warnings += "let #${old + 1}: '${let.name}' is overwritten before it's read" }
        current[let.name] = i
    }
    reads(p.result)
    for ((name, i) in current) if (i !in read) warnings += "let #${i + 1}: '$name' is never read"
    return warnings.sorted()
}

fun main() {
    for (src in listOf("let x = 1; let y = 2; x", "let x = 1; let x = 2; x", "let x = 1; let x = x + 1; x",
                       "let a = 1; let b = a; 7")) {
        println(src.padEnd(28) + unusedLets(parse(src)).ifEmpty { listOf("no warnings") })
    }
}
