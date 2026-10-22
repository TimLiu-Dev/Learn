package library

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate

sealed interface LendResult {
    data class Lent(val loanId: Long) : LendResult
    data object AlreadyOnLoan : LendResult
    data object NoSuchBook : LendResult
    data object NoSuchMember : LendResult
}

data class Overdue(val title: String, val member: String, val loaned: LocalDate)
data class Borrower(val name: String, val loans: Int)

class LibraryRepository(dbFile: String) {
    // IMMEDIATE: every transaction takes the write lock at BEGIN, so two lenders queue up
    // instead of both reading "available" and then failing with SQLITE_BUSY.
    private val url = "jdbc:sqlite:$dbFile?transaction_mode=IMMEDIATE"

    private fun connect(): Connection = DriverManager.getConnection(url).also { c ->
        c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }      // off by default in SQLite
    }

    private inline fun <T> transaction(block: (Connection) -> T): T = connect().use { c ->
        c.autoCommit = false
        try { block(c).also { c.commit() } } catch (e: Throwable) { c.rollback(); throw e }
    }

    private fun <T> ResultSet.toList(map: (ResultSet) -> T): List<T> = buildList { while (next()) add(map(this@toList)) }

    private fun Connection.exists(sql: String, id: Long): Boolean = prepareStatement(sql).use { ps ->
        ps.setLong(1, id)
        ps.executeQuery().use { it.next() }
    }

    private fun Connection.insert(sql: String, vararg args: Any?): Long =
        prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { ps ->
            args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
            ps.executeUpdate()
            ps.generatedKeys.use { it.next(); it.getLong(1) }
        }

    fun createSchema() = transaction { c -> c.createStatement().use { st -> SCHEMA.forEach(st::executeUpdate) } }

    fun addMember(name: String, email: String): Long =
        transaction { it.insert("INSERT INTO members (name, email) VALUES (?, ?)", name, email) }

    fun addBook(title: String, year: Int?): Long =
        transaction { it.insert("INSERT INTO books (title, year) VALUES (?, ?)", title, year) }

    fun memberName(id: Long): String? = connect().use { c ->
        c.prepareStatement("SELECT name FROM members WHERE id = ?").use { ps ->
            ps.setLong(1, id)
            ps.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }
    }

    // Check and insert in ONE transaction: nobody can lend the book between our check and our insert.
    fun lend(bookId: Long, memberId: Long, today: LocalDate): LendResult = transaction { c ->
        when {
            !c.exists("SELECT 1 FROM books WHERE id = ?", bookId) -> LendResult.NoSuchBook
            !c.exists("SELECT 1 FROM members WHERE id = ?", memberId) -> LendResult.NoSuchMember
            c.exists("SELECT 1 FROM loans WHERE book_id = ? AND returned IS NULL", bookId) -> LendResult.AlreadyOnLoan
            else -> LendResult.Lent(c.insert("INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)",
                                             bookId, memberId, today.toString()))
        }
    }

    fun returnBook(bookId: Long, today: LocalDate): Boolean = transaction { c ->
        c.prepareStatement("UPDATE loans SET returned = ? WHERE book_id = ? AND returned IS NULL").use { ps ->
            ps.setString(1, today.toString()); ps.setLong(2, bookId)
            ps.executeUpdate() == 1
        }
    }

    fun overdueLoans(today: LocalDate, loanDays: Long = 14): List<Overdue> = connect().use { c ->
        c.prepareStatement(
            """SELECT b.title, m.name, l.loaned FROM loans l
               JOIN books b ON b.id = l.book_id JOIN members m ON m.id = l.member_id
               WHERE l.returned IS NULL AND l.loaned < ? ORDER BY l.loaned, b.title""").use { ps ->
            ps.setString(1, today.minusDays(loanDays).toString())
            ps.executeQuery().use { rs -> rs.toList { Overdue(it.getString(1), it.getString(2), LocalDate.parse(it.getString(3))) } }
        }
    }

    fun topBorrowers(limit: Int): List<Borrower> = connect().use { c ->
        c.prepareStatement(
            """SELECT m.name, COUNT(*) AS n FROM loans l JOIN members m ON m.id = l.member_id
               GROUP BY m.id, m.name ORDER BY n DESC, m.name LIMIT ?""").use { ps ->
            ps.setInt(1, limit)
            ps.executeQuery().use { rs -> rs.toList { Borrower(it.getString(1), it.getInt(2)) } }
        }
    }

    fun loanCount(): Int = connect().use { c ->
        c.createStatement().use { st -> st.executeQuery("SELECT COUNT(*) FROM loans").use { it.next(); it.getInt(1) } }
    }
}
