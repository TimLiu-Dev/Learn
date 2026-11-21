val books = listOf("Dune", "Neuromancer", "Hyperion", "Foundation", "Kindred")
val cache = HashMap<String, List<String>>()

// "Pages are 1-based." Cached for speed.
fun page(page: Int, size: Int): List<String> =
    cache.getOrPut("books") { books.drop(page * size).take(size) }

fun main() {
    println("${page(1, 2)} ${page(2, 2)}")
}
