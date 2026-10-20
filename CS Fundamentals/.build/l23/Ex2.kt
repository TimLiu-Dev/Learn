object Limits {
    init { println("  [Limits initialised]") }
    const val MAX = 50                 // compile-time constant
    val label = "limits"               // ordinary property
}

fun main() {
    println("A: ${Limits.MAX}")
    println("B: ${Limits.label}")
    println("C: ${Limits.MAX}")
}
