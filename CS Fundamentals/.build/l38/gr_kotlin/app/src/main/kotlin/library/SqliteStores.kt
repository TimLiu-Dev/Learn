package library

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate

// The details. They depend on the abstractions in Model.kt; nothing depends on them except main().
object Schema {
    fun create(url: String) = DriverManager.getConnection(url).use { c ->
        c.createStatement().use { st ->
            st.executeUpdate("CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY, name TEXT NOT NULL)")
            st.executeUpdate("""CREATE TABLE IF NOT EXISTS loans (id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL,
                member_id INTEGER NOT NULL REFERENCES members(id), loaned TEXT NOT NULL, returned TEXT)""")
            st.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL")
        }
    }
}

private fun connect(url: String): Connection = DriverManager.getConnection(url).also { c ->
    c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
}

class SqliteMemberStore(private val url: String) : MemberStore {
    fun add(name: String): Member = connect(url).use { c ->
        c.prepareStatement("INSERT INTO members (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS).use { ps ->
            ps.setString(1, name); ps.executeUpdate()
            ps.generatedKeys.use { it.next(); Member(it.getLong(1), name) }
        }
    }

    override fun find(memberId: Long): Member? = connect(url).use { c ->
        c.prepareStatement("SELECT id, name FROM members WHERE id = ?").use { ps ->
            ps.setLong(1, memberId)
            ps.executeQuery().use { rs -> if (rs.next()) Member(rs.getLong(1), rs.getString(2)) else null }
        }
    }
}

class SqliteLoanStore(private val url: String) : LoanStore {
    private val columns = "id, book_id, member_id, loaned, returned"

    private fun ResultSet.toLoan() =
        Loan(getLong(1), getLong(2), getLong(3), LocalDate.parse(getString(4)), getString(5)?.let(LocalDate::parse))

    private fun query(sql: String, id: Long): List<Loan> = connect(url).use { c ->
        c.prepareStatement(sql).use { ps ->
            ps.setLong(1, id)
            ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.toLoan()) } }
        }
    }

    override fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan = connect(url).use { c ->
        c.prepareStatement("INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS).use { ps ->
            ps.setLong(1, bookId); ps.setLong(2, memberId); ps.setString(3, loaned.toString()); ps.executeUpdate()
            ps.generatedKeys.use { it.next(); Loan(it.getLong(1), bookId, memberId, loaned, null) }
        }
    }

    override fun find(loanId: Long) = query("SELECT $columns FROM loans WHERE id = ?", loanId).firstOrNull()
    override fun openForMember(memberId: Long) =
        query("SELECT $columns FROM loans WHERE member_id = ? AND returned IS NULL ORDER BY id", memberId)
    override fun openForBook(bookId: Long) =
        query("SELECT $columns FROM loans WHERE book_id = ? AND returned IS NULL", bookId).firstOrNull()

    override fun close(loanId: Long, returned: LocalDate): Boolean = connect(url).use { c ->
        c.prepareStatement("UPDATE loans SET returned = ? WHERE id = ? AND returned IS NULL").use { ps ->
            ps.setString(1, returned.toString()); ps.setLong(2, loanId)
            ps.executeUpdate() == 1
        }
    }
}
