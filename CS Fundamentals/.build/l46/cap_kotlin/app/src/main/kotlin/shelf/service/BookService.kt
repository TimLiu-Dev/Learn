package shelf.service

import shelf.domain.Book
import shelf.store.BookStore
import shelf.store.LoanStore

data class NewBook(val title: String, val author: String, val year: Int?)   // already validated
data class Page<T>(val items: List<T>, val limit: Int, val offset: Int, val total: Int)

enum class DeleteResult { DELETED, NOT_FOUND, HAS_LOANS }

class BookService(private val books: BookStore, private val loans: LoanStore) {
    fun create(book: NewBook): Book = books.add(book.title, book.author, book.year)
    fun get(id: Long): Book? = books.find(id)
    fun update(id: Long, book: NewBook): Book? = books.update(id, book.title, book.author, book.year)

    fun list(limit: Int, offset: Int, author: String?): Page<Book> =
        Page(books.list(limit, offset, author), limit, offset, books.count(author))

    // Loan history is worth keeping, so a book that was ever lent can't be deleted.
    fun delete(id: Long): DeleteResult = when {
        books.find(id) == null -> DeleteResult.NOT_FOUND
        loans.anyForBook(id) -> DeleteResult.HAS_LOANS
        else -> if (books.delete(id)) DeleteResult.DELETED else DeleteResult.NOT_FOUND
    }
}
