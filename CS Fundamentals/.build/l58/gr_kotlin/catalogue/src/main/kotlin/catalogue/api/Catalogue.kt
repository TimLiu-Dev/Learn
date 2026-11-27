package catalogue.api

import catalogue.internal.DefaultCatalogue
import platform.EventBus

// The catalogue's published language: what other modules may know. Plain values only;
// no catalogue.internal type ever appears in these signatures.
sealed interface CatalogueEvent {
    data class CopyAdded(val barcode: String, val isbn: String, val title: String) : CatalogueEvent
    data class CopyWithdrawn(val barcode: String) : CatalogueEvent
}

data class BookSummary(val isbn: String, val title: String, val author: String, val copies: Int)

interface Catalogue {
    fun addBook(isbn: String, title: String, author: String)
    fun addCopy(isbn: String, barcode: String)
    fun withdrawCopy(barcode: String)
    fun search(text: String): List<BookSummary>

    companion object {
        fun create(bus: EventBus): Catalogue = DefaultCatalogue(bus)
    }
}
