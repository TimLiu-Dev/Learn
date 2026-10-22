import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Constraints are business rules the database enforces for you.
fun Connection.tryUpdate(label: String, sql: String) {
    try {
        val n = createStatement().use { it.executeUpdate(sql) }
        println("$label: OK, $n row(s)")
    } catch (e: SQLException) {
        println("$label: ${e.message}")
    }
}

fun main(args: Array<String>) {
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { c ->
        val badLoan = "INSERT INTO loans (book_id, member_id, loaned) VALUES (999, 1, '2026-10-21')"
        c.tryUpdate("FK off (the default)", badLoan)                // book 999 doesn't exist!
        c.createStatement().use { it.executeUpdate("DELETE FROM loans WHERE book_id = 999") }

        c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }   // per connection
        c.tryUpdate("FK on", badLoan)
        c.tryUpdate("UNIQUE", "INSERT INTO members (name, email) VALUES ('Ada 2', 'ada@example.org')")
        c.tryUpdate("CHECK", "INSERT INTO books (title, author_id, year, price_cents) VALUES ('Time Ship', 1, 3000, 999)")
        c.tryUpdate("NOT NULL", "INSERT INTO books (title, author_id, year) VALUES ('Free Book', 1, 2020)")
    }
}
