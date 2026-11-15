data class Entry(val author: Int, val year: Int)

fun main() {
    val books = listOf(Entry(2, 1993), Entry(1, 1974), Entry(2, 1979), Entry(1, 1968), Entry(3, 1989))
    // An index on (author, year): sorted by author, then by year within each author.
    val index = books.sortedWith(compareBy({ it.author }, { it.year }))
    println(index.map { it.year })
}
