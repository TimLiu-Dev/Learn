class Point(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true          // same object: trivially equal
        if (other !is Point) return false        // null or a different type
        return x == other.x && y == other.y      // smart cast: other is a Point here
    }

    override fun hashCode(): Int = 31 * x + y   // equal points -> equal hash codes

    override fun toString(): String = "Point(x=$x, y=$y)"
}

fun main() {
    val a = Point(1, 2)
    val b = Point(1, 2)
    println(a == b)
    println(a === b)
    println(a.hashCode() == b.hashCode())
    println(a)
    println(hashSetOf(a).contains(b))
}
