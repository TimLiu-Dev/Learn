package shelf.service

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import shelf.store.MemoryBookStore
import shelf.store.MemoryLoanStore

class BookServiceTest {
    private val books = MemoryBookStore()
    private val loans = MemoryLoanStore()
    private val service = BookService(books, loans)

    @Test
    fun `a page reports the total, not just its own size`() {
        repeat(5) { service.create(NewBook("Book $it", if (it % 2 == 0) "Le Guin" else "Herbert", null)) }
        val page = service.list(limit = 2, offset = 0, author = "Le Guin")
        assertEquals(listOf("Book 0", "Book 2"), page.items.map { it.title })
        assertEquals(3, page.total)
    }

    @Test
    fun `a book that was ever lent cannot be deleted`() {
        val lent = service.create(NewBook("Dune", "Herbert", 1965))
        val fresh = service.create(NewBook("Emma", "Austen", 1815))
        loans.add(lent.id, 1, LocalDate.of(2026, 1, 1))
        assertEquals(DeleteResult.HAS_LOANS, service.delete(lent.id))
        assertEquals(DeleteResult.DELETED, service.delete(fresh.id))
        assertEquals(DeleteResult.NOT_FOUND, service.delete(fresh.id))
    }
}
