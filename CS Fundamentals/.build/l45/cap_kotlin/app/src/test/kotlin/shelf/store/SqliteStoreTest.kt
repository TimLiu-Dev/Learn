package shelf.store

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest

class SqliteStoreTest : StoreContract() {
    private val file: Path = Files.createTempFile("shelf-test", ".db")

    override fun newStores(): Triple<BookStore, MemberStore, LoanStore> {
        val db = Db(file).also { it.createSchema() }
        return Triple(SqliteBookStore(db), SqliteMemberStore(db), SqliteLoanStore(db))
    }

    @AfterTest
    fun deleteFile() {
        Files.deleteIfExists(file)
    }
}
