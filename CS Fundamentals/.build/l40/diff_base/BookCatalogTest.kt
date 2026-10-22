package books

import java.sql.DriverManager
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BookCatalogTest {
    private val db = DriverManager.getConnection("jdbc:sqlite::memory:").also { seed(it) }
    private val catalog = BookCatalog(db)

    @AfterTest fun close() = db.close()

    @Test fun `all returns every book`() {
        assertEquals(5, catalog.all().size)
    }
}
