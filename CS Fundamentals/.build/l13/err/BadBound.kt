fun <T : Comparable<T>> largest(items: List<T>): T = items.max()

class Point(val x: Int, val y: Int)              // not Comparable

fun main() {
    val points = listOf(Point(1, 2), Point(3, 4))
    println(largest<Point>(points))              // T = Point breaks the bound
}
