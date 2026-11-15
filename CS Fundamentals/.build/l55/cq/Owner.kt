data class Book(val title: String, val owner: String?)    // rows from before logins existed have no owner

fun mayDelete(user: String?, book: Book) = user == book.owner

fun main() {
    val books = listOf(Book("Dune", "ana"), Book("Emma", null))
    val anonymous: String? = null                            // no session cookie
    println(books.filter { mayDelete(anonymous, it) }.map { it.title })
}
