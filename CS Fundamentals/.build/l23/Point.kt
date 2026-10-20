data class Point(val x: Int, val y: Int)

fun main() {
    val p = Point(1, 2)
    println("$p ${p == Point(1, 2)} ${p.copy(y = 9)}")
}
