// The divisible-by-3 DFA as a table: delta[q][bit] is the next state.
val delta = listOf(
    intArrayOf(0, 1),   // R0
    intArrayOf(2, 0),   // R1
    intArrayOf(1, 2),   // R2
)

fun main() {
    val trace = "1011".runningFold(0) { q, c -> delta[q][c - '0'] }
    println("${trace.size} ${trace.last()}")
}
