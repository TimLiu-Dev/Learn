data class Book(val title: String, val author: String, val year: Int)

// prints the books
fun bookLines(books: List<Book>, x: Boolean): List<String> {
    val r = mutableListOf<String>()
    if (x) {
        for (b in books.sortedBy { it.author }) r.add(b.author + ": " + b.title)
    } else {
        for (b in books.sortedBy { it.year }) r.add(b.title + " (" + b.year + ")")
    }
    return r
}

/** "Author: Title" lines, sorted by author. */
fun authorLines(books: List<Book>): List<String> = TODO("the x = true mode, on its own")

/** "Title (year)" lines, oldest first. */
fun yearLines(books: List<Book>): List<String> = TODO("the x = false mode, on its own")
