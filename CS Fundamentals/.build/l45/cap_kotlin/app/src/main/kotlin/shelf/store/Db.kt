package shelf.store

import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class Db(file: Path) {
    // IMMEDIATE: a transaction takes SQLite's write lock at BEGIN, so writers queue
    // instead of failing with SQLITE_BUSY halfway through (L36).
    private val url = "jdbc:sqlite:$file?transaction_mode=IMMEDIATE"

    fun connect(): Connection = DriverManager.getConnection(url).also { c ->
        c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }   // off by default (L34)
    }

    inline fun <T> read(block: (Connection) -> T): T = connect().use(block)

    inline fun <T> transaction(block: (Connection) -> T): T = connect().use { c ->
        c.autoCommit = false
        try {
            block(c).also { c.commit() }
        } catch (e: Throwable) {
            c.rollback()
            throw e
        }
    }

    fun createSchema() = transaction { c -> c.createStatement().use { st -> SCHEMA.forEach(st::executeUpdate) } }

    companion object {
        val SCHEMA = listOf(
            "CREATE TABLE IF NOT EXISTS authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)",
            """CREATE TABLE IF NOT EXISTS books (
                 id        INTEGER PRIMARY KEY,
                 title     TEXT NOT NULL,
                 author_id INTEGER NOT NULL REFERENCES authors(id),
                 year      INTEGER CHECK (year BETWEEN 1400 AND 2100))""",
            "CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE)",
            """CREATE TABLE IF NOT EXISTS loans (
                 id        INTEGER PRIMARY KEY,
                 book_id   INTEGER NOT NULL REFERENCES books(id),
                 member_id INTEGER NOT NULL REFERENCES members(id),
                 loaned    TEXT NOT NULL,
                 returned  TEXT)""",
            // At most one open loan per book: the database itself refuses a double loan (L36).
            "CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL",
            "CREATE INDEX IF NOT EXISTS loans_by_member ON loans(member_id)",
            "CREATE INDEX IF NOT EXISTS books_by_author ON books(author_id)",   // ?author= filter (Part 3)
        )
    }
}

// Small JDBC helpers: every value goes through a ? placeholder, never string concatenation (L33).
fun <T> Connection.query(sql: String, vararg args: Any?, map: (ResultSet) -> T): List<T> =
    prepareStatement(sql).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeQuery().use { rs -> buildList { while (rs.next()) add(map(rs)) } }
    }

fun Connection.update(sql: String, vararg args: Any?): Int =
    prepareStatement(sql).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeUpdate()
    }

fun Connection.insert(sql: String, vararg args: Any?): Long =
    prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { ps ->
        args.forEachIndexed { i, a -> ps.setObject(i + 1, a) }
        ps.executeUpdate()
        ps.generatedKeys.use { it.next(); it.getLong(1) }
    }
