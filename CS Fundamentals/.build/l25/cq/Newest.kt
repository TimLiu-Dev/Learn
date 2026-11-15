fun main() {
    val requested = listOf("1.9.0", "1.10.0", "1.2.0")
    val numerically = compareBy<String>(
        { it.split(".")[0].toInt() },
        { it.split(".")[1].toInt() },
        { it.split(".")[2].toInt() },
    )
    println("${requested.max()} ${requested.maxWith(numerically)}")
}
