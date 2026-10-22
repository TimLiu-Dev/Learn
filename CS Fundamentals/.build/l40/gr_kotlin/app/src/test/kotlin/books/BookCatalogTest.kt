package books

import java.sql.DriverManager
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BookCatalogTest {
    private val db = DriverManager.getConnection("jdbc:sqlite::memory:").also { seed(it) }
    private val catalog = BookCatalog(db)

    @AfterTest fun close() = db.close()

    @Test fun `all returns every book`() {
        assertEquals(5, catalog.all().size)
    }

    @Test fun `page returns books`() {
        val page = catalog.page(1, 2)
        assertNotNull(page)
        assertTrue(page.items.isNotEmpty())
    }
}
