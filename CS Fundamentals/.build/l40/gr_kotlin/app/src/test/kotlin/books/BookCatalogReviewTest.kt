package books

import java.sql.DriverManager
import java.sql.SQLException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

// Written by the reviewer: one test per suspected bug.
class BookCatalogReviewTest {
    private val db = DriverManager.getConnection("jdbc:sqlite::memory:").also { seed(it) }
    private val catalog = BookCatalog(db)

    @AfterTest fun close() = db.close()

    @Test fun `bug 1 - page 1 starts at the first book`() {
        assertEquals(listOf("Dune", "Neuromancer"), catalog.page(1, 2).items.map { it.title })
    }

    @Test fun `bug 2 - page 2 is not page 1`() {
        assertNotEquals(catalog.page(1, 2).items, catalog.page(2, 2).items)
    }

    @Test fun `bug 3 - database errors are not hidden`() {
        db.createStatement().use { it.execute("DROP TABLE books") }
        assertFailsWith<SQLException> { catalog.page(1, 2) }
    }

    @Test fun `bug 4 - page leaves no statement open`() {
        catalog.page(1, 2)
        db.createStatement().use { it.execute("DROP TABLE books") }   // SQLite refuses while a statement is open
    }
}
