fun main() {
    val a = Double.NaN
    val b: Any = Double.NaN
    val c: Any = Double.NaN
    println("${a == a} ${b == c} ${listOf(0.0).contains(-0.0)}")
}
