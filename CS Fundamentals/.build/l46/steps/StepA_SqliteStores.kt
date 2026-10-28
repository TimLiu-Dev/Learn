package shelf.store

import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDate
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException
import shelf.domain.Book
import shelf.domain.Loan
import shelf.domain.Member

class SqliteBookStore(private val db: Db) : BookStore {
    private val select = "SELECT b.id, b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id"

    private fun book(rs: ResultSet) =
        Book(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4).takeUnless { rs.wasNull() })

    // Authors are a separate table (L35); find-or-create the row for this name.
    private fun Connection.authorId(name: String): Long {
        update("INSERT INTO authors (name) VALUES (?) ON CONFLICT (name) DO NOTHING", name)
        return query("SELECT id FROM authors WHERE name = ?", name) { it.getLong(1) }.single()
    }

    override fun add(title: String, author: String, year: Int?): Book = db.transaction { c ->
        val id = c.insert("INSERT INTO books (title, author_id, year) VALUES (?, ?, ?)", title, c.authorId(author), year)
        Book(id, title, author, year)
    }

    override fun find(id: Long): Book? = db.read { c -> c.query("$select WHERE b.id = ?", id) { book(it) }.singleOrNull() }

    override fun list(limit: Int, offset: Int, author: String?): List<Book> = db.read { c ->
        if (author == null) c.query("$select ORDER BY b.id LIMIT ? OFFSET ?", limit, offset) { book(it) }
        else c.query("$select WHERE a.name = ? ORDER BY b.id LIMIT ? OFFSET ?", author, limit, offset) { book(it) }
    }

    override fun count(author: String?): Int = db.read { c ->
        if (author == null) c.query("SELECT COUNT(*) FROM books") { it.getInt(1) }.single()
        else c.query("SELECT COUNT(*) FROM books b JOIN authors a ON a.id = b.author_id WHERE a.name = ?", author) { it.getInt(1) }.single()
    }

    override fun update(id: Long, title: String, author: String, year: Int?): Book? = db.transaction { c ->
        if (c.query("SELECT 1 FROM books WHERE id = ?", id) { true }.isEmpty()) return@transaction null
        c.update("UPDATE books SET title = ?, author_id = ?, year = ? WHERE id = ?", title, c.authorId(author), year, id)
        Book(id, title, author, year)
    }

    override fun delete(id: Long): Boolean = db.transaction { c -> c.update("DELETE FROM books WHERE id = ?", id) == 1 }
}

class SqliteMemberStore(private val db: Db) : MemberStore {
    private fun member(rs: ResultSet) = Member(rs.getLong(1), rs.getString(2), rs.getString(3))

    override fun add(name: String, email: String): Member = db.transaction { c ->
        Member(c.insert("INSERT INTO members (name, email) VALUES (?, ?)", name, email), name, email)
    }

    override fun find(id: Long): Member? =
        db.read { c -> c.query("SELECT id, name, email FROM members WHERE id = ?", id) { member(it) }.singleOrNull() }

    override fun findByEmail(email: String): Member? =
        db.read { c -> c.query("SELECT id, name, email FROM members WHERE email = ?", email) { member(it) }.singleOrNull() }
}

class SqliteLoanStore(private val db: Db) : LoanStore {
    private val select = "SELECT id, book_id, member_id, loaned, returned FROM loans"

    private fun loan(rs: ResultSet) = Loan(
        rs.getLong(1), rs.getLong(2), rs.getLong(3),
        LocalDate.parse(rs.getString(4)), rs.getString(5)?.let(LocalDate::parse),   // ISO dates as TEXT (L34)
    )

    override fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan = try {
        db.transaction { c ->
            val id = c.insert("INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)", bookId, memberId, loaned.toString())
            Loan(id, bookId, memberId, loaned, null)
        }
    } catch (e: SQLiteException) {
        // The partial unique index refused a second open loan: someone else won the race.
        if (e.resultCode == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) throw BookAlreadyOnLoan(bookId) else throw e
    }

    override fun find(id: Long): Loan? = db.read { c -> c.query("$select WHERE id = ?", id) { loan(it) }.singleOrNull() }

    override fun openForBook(bookId: Long): Loan? =
        db.read { c -> c.query("$select WHERE book_id = ? AND returned IS NULL", bookId) { loan(it) }.singleOrNull() }

    override fun forMember(memberId: Long): List<Loan> =
        db.read { c -> c.query("$select WHERE member_id = ? ORDER BY id", memberId) { loan(it) } }

    override fun anyForBook(bookId: Long): Boolean =
        db.read { c -> c.query("SELECT 1 FROM loans WHERE book_id = ? LIMIT 1", bookId) { true }.isNotEmpty() }

    override fun close(id: Long, returned: LocalDate): Boolean = db.transaction { c ->
        c.update("UPDATE loans SET returned = ? WHERE id = ? AND returned IS NULL", returned.toString(), id) == 1
    }
}
