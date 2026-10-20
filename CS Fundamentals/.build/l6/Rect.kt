class Rect(val width: Int, val height: Int = width) {   // a default can use an earlier parameter
    init {
        require(width > 0 && height > 0) { "sides must be positive" }
    }
}

fun main() {
    val r = Rect(3, 4)
    val sq = Rect(5)
    println("${r.width}x${r.height}")
    println("${sq.width}x${sq.height}")
    try {
        Rect(-1)
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
}
