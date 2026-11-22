fun main() {
    val raw = "/books/".split('/')
    val parts = raw.filter { it.isNotEmpty() }
    println("${raw.size} ${parts.size}")
}
