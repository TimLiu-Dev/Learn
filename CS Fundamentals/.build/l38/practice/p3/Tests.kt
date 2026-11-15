import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeReservationStoreTest {
    private val store = FakeReservationStore()
    private fun members(bookId: Long) = store.queue(bookId).map { it.member }

    @Test fun `ids count up across all books`() {
        assertEquals(listOf(1L, 2L, 3L), listOf(store.add(7, "ada").id, store.add(8, "alan").id, store.add(7, "grace").id))
    }

    @Test fun `each book has its own queue, oldest first`() {
        store.add(7, "ada"); store.add(8, "alan"); store.add(7, "grace")
        assertEquals(listOf("ada", "grace"), members(7))
        assertEquals(listOf("alan"), members(8))
        assertEquals("a book nobody reserved", emptyList<String>(), members(99))
    }

    @Test fun `cancel removes a reservation once`() {
        val ada = store.add(7, "ada"); store.add(7, "grace")
        assertTrue(store.cancel(ada.id))
        assertEquals(listOf("grace"), members(7))
        assertFalse("cancelling twice", store.cancel(ada.id))
        assertFalse("an id that never existed", store.cancel(42))
    }

    @Test fun `ids are never reused after a cancel`() {
        store.add(7, "ada"); store.add(7, "alan")
        store.cancel(1)
        assertEquals("ids 1 and 2 were already handed out", 3L, store.add(7, "grace").id)
    }

    @Test fun `a returned queue is a snapshot`() {
        store.add(7, "ada")
        val before = store.queue(7)
        store.add(7, "alan")
        assertEquals("a list you already got must not change", listOf("ada"), before.map { it.member })
    }

    @Test fun `the hold desk works on the fake`() {
        val desk = HoldDesk(store)
        val ada = desk.reserve(7, "ada")
        assertNull("ada can't queue twice for one book", desk.reserve(7, "ada"))
        desk.reserve(7, "alan")
        assertEquals("ada", desk.nextInLine(7))
        store.cancel(ada!!.id)
        assertEquals("alan", desk.nextInLine(7))
        assertNull("nobody waits for book 8", desk.nextInLine(8))
    }
}
