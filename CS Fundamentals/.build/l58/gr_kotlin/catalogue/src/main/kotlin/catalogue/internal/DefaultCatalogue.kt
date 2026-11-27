package catalogue.internal

import catalogue.api.BookSummary
import catalogue.api.Catalogue
import catalogue.api.CatalogueEvent
import platform.EventBus

internal class DefaultCatalogue(private val bus: EventBus) : Catalogue {
    private val books = LinkedHashMap<Isbn, Book>()

    override fun addBook(isbn: String, title: String, author: String) {
        val key = Isbn.parse(isbn)
        require(key !in books) { "already catalogued: $isbn" }
        books[key] = Book(key, title, author)
    }

    override fun addCopy(isbn: String, barcode: String) {
        val book = books[Isbn.parse(isbn)] ?: throw IllegalArgumentException("unknown ISBN: $isbn")
        require(books.values.none { barcode in it.barcodes }) { "barcode in use: $barcode" }
        book.barcodes += barcode
        bus.publish(CatalogueEvent.CopyAdded(barcode, book.isbn.digits, book.title))
    }

    override fun withdrawCopy(barcode: String) {
        val book = books.values.firstOrNull { barcode in it.barcodes } ?: throw IllegalArgumentException("unknown copy: $barcode")
        book.barcodes -= barcode
        bus.publish(CatalogueEvent.CopyWithdrawn(barcode))
    }

    override fun search(text: String): List<BookSummary> = books.values
        .filter { text.lowercase() in it.title.lowercase() || text.lowercase() in it.author.lowercase() }
        .map { BookSummary(it.isbn.digits, it.title, it.author, it.barcodes.size) }
}
