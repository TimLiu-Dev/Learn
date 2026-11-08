fun main() {
    val names = listOf("Ada", null, "Linus")
    var total = 0
    for (n in names) total += n?.length ?: 0
    println(total)
}
