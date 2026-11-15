data class DataPoint(val x: Int, val y: Int)
class PlainPoint(val x: Int, val y: Int)

fun main() {
    val a = DataPoint(1, 2) == DataPoint(1, 2)
    val b = PlainPoint(1, 2) == PlainPoint(1, 2)
    val c = listOf(DataPoint(1, 2)) == listOf(DataPoint(1, 2))
    val d = 0.1 + 0.2 == 0.3
    println("$a $b $c $d")
}
