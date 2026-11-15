data class Book(val title: String, val author: String, val year: Int)

fun bookLines(books: List<Book>, byAuthor: Boolean): List<String> =
    if (byAuthor) authorLines(books) else yearLines(books)

fun authorLines(books: List<Book>): List<String> =
    books.sortedBy { it.author }.map { "${it.author}: ${it.title}" }

// "Tidier": sort by year, then by title. Same output for different years, but not for equal ones.
fun yearLines(books: List<Book>): List<String> =
    books.sortedWith(compareBy({ it.year }, { it.title })).map { "${it.title} (${it.year})" }
