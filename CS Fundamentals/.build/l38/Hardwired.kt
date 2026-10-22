import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager
import java.time.LocalDate

// A service that builds its own dependencies: a real database file and the real calendar.
class LendingService {
    private val file: Path = Path.of(System.getProperty("java.io.tmpdir"), "l38-library.db")
    private val conn = DriverManager.getConnection("jdbc:sqlite:$file").also { c ->
        c.createStatement().use { it.executeUpdate("CREATE TABLE IF NOT EXISTS loans (member_id INTEGER, loaned TEXT, returned TEXT)") }
    }

    fun recordLoan(memberId: Long, loaned: LocalDate) =
        conn.prepareStatement("INSERT INTO loans VALUES (?, ?, NULL)").use { ps ->
            ps.setLong(1, memberId); ps.setString(2, loaned.toString()); ps.executeUpdate()
        }

    // Blocked if any open loan is older than 14 days, measured from TODAY, whenever "today" is.
    fun mayBorrow(memberId: Long): Boolean {
        val cutoff = LocalDate.now().minusDays(14)
        return conn.prepareStatement("SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned IS NULL AND loaned < ?").use { ps ->
            ps.setLong(1, memberId); ps.setString(2, cutoff.toString())
            ps.executeQuery().use { it.next(); it.getInt(1) == 0 }
        }
    }

    fun close() { conn.close(); Files.deleteIfExists(file) }
}

fun main() {
    // A "test": a member with a loan from 2026-10-10 should still be allowed to borrow.
    val service = LendingService()            // can't choose the file, can't choose the date
    try {
        service.recordLoan(1, LocalDate.parse("2026-10-10"))
        println("today, per the system clock: ${LocalDate.now()}")
        println("member 1 may borrow: ${service.mayBorrow(1)}   (expected true)")
        println("the same check flips to false from 2026-10-25 on, with no code change")
    } finally {
        service.close()
    }
}
