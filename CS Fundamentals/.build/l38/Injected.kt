import java.sql.Connection
import java.sql.DriverManager
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

interface LoanStore {
    fun openLoanDates(memberId: Long): List<LocalDate>
}

// The service asks for what it needs. It never builds a connection or reads the system clock.
class LendingService(
    private val loans: LoanStore,
    private val clock: Clock = Clock.systemUTC(),     // a production default: convenient, see below
) {
    fun mayBorrow(memberId: Long): Boolean {
        val cutoff = LocalDate.now(clock).minusDays(14)
        return loans.openLoanDates(memberId).none { it < cutoff }
    }
}

class SqliteLoanStore(private val c: Connection) : LoanStore {
    override fun openLoanDates(memberId: Long): List<LocalDate> =
        c.prepareStatement("SELECT loaned FROM loans WHERE member_id = ? AND returned IS NULL").use { ps ->
            ps.setLong(1, memberId)
            ps.executeQuery().use { rs -> buildList { while (rs.next()) add(LocalDate.parse(rs.getString(1))) } }
        }
}

class InMemoryLoanStore(private val open: Map<Long, List<LocalDate>>) : LoanStore {
    override fun openLoanDates(memberId: Long) = open[memberId].orEmpty()
}

// The composition root: the ONE place that knows which concrete classes are used.
fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        c.createStatement().use { it.executeUpdate("CREATE TABLE loans (member_id INTEGER, loaned TEXT, returned TEXT)") }
        c.createStatement().use { it.executeUpdate("INSERT INTO loans VALUES (1, '2026-10-10', NULL)") }
        val production = LendingService(SqliteLoanStore(c), Clock.systemUTC())
        println("production wiring, today ${LocalDate.now(Clock.systemUTC())}: member 1 may borrow = ${production.mayBorrow(1)}")
    }

    // A test wires the same class differently: no file, and a day we choose.
    val oct30 = Clock.fixed(Instant.parse("2026-10-30T09:00:00Z"), ZoneOffset.UTC)
    val store = InMemoryLoanStore(mapOf(1L to listOf(LocalDate.parse("2026-10-10"))))
    println("test wiring, fixed 2026-10-30: member 1 may borrow = ${LendingService(store, oct30).mayBorrow(1)}")
    println("test wiring, fixed 2026-10-24: member 1 may borrow = " +
        LendingService(store, Clock.fixed(Instant.parse("2026-10-24T09:00:00Z"), ZoneOffset.UTC)).mayBorrow(1))
}
