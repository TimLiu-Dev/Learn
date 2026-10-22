data class BookSearch(
    val title: String = "",          // defaults live in the constructor
    val author: String? = null,
    val yearFrom: Int = 1400,
    val yearTo: Int = 2100,
    val sort: String = "title",
    val limit: Int = 20,
    val offset: Int = 0,
) {
    init {                           // the same validation build() did
        check(yearFrom <= yearTo) { "yearFrom $yearFrom > yearTo $yearTo" }
        check(limit in 1..100) { "limit must be 1..100, was $limit" }
    }
}

fun main() {
    val q = BookSearch(title = "dune", yearFrom = 1960, yearTo = 1970, sort = "year")   // the whole builder, one line
    println(q)
    println(q.copy(offset = 20))     // "start from an existing object and change one field" (L7)
    runCatching { BookSearch(yearFrom = 2000, yearTo = 1990) }.onFailure { println("rejected: ${it.message}") }
    runCatching { q.copy(limit = 500) }.onFailure { println("copy rejected: ${it.message}") }   // copy runs init too

    // Builders Kotlin still uses: lambdas with a receiver (L15)
    val line = buildString { append("due: "); append("Dune"); append(" (3 days)") }
    val shelf = buildList { add("Dune"); if (q.limit > 10) add("Emma"); add("Ulysses") }
    val sb = StringBuilder().apply { append("apply "); append("configures in place") }
    println(line)
    println(shelf)
    println(sb)
}
