fun scan(xs: List<Int>): String {
    var seen = ""
    xs.forEach {
        if (it == 0) return@forEach
        if (it < 0) return "stop at $it after $seen"
        seen += it
    }
    return "done: $seen"
}

fun main() {
    println(scan(listOf(1, 0, 2, -3, 4)))
}
