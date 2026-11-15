data class Book(val title: String, val author: String, val year: Int)

/** Kept for old callers: each mode now has its own function. */
fun bookLines(books: List<Book>, byAuthor: Boolean): List<String> =
    if (byAuthor) authorLines(books) else yearLines(books)

/** "Author: Title" lines, sorted by author. */
fun authorLines(books: List<Book>): List<String> =
    books.sortedBy { it.author }.map { "${it.author}: ${it.title}" }

/** "Title (year)" lines, oldest first. Books from the same year keep their order. */
fun yearLines(books: List<Book>): List<String> =
    books.sortedBy { it.year }.map { "${it.title} (${it.year})" }
