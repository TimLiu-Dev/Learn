class Rect(var width: Int, var height: Int) {
    val storedArea = width * height
    val computedArea: Int
        get() = width * height
}

fun main() {
    val r = Rect(2, 3)
    r.width = 10
    println("${r.storedArea} ${r.computedArea}")
}
