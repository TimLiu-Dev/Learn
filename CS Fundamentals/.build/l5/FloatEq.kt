fun same(a: Any, b: Any) = a == b   // static type Any: == calls equals()

fun main() {
    val nan = Double.NaN
    println("${nan == nan} ${nan.isNaN()}")          // IEEE 754: NaN equals nothing, not even itself
    println(0.0 == -0.0)                             // IEEE 754: the two zeros are equal
    val maybe: Double? = nan
    println(maybe == nan)                            // Double? still compares as IEEE 754
    println("${same(nan, nan)} ${same(0.0, -0.0)}")  // equals(): the opposite answers
    println(listOf(nan).contains(nan))               // collections use equals() too
    println(setOf(0.0, -0.0).size)
}
