import java.sql.Connection
import java.sql.DriverManager

// ---------- Before: the policy depends on a concrete detail ----------
class SqliteLoans(private val c: Connection) {
    fun openLoanCount(memberId: Long): Int =
        c.prepareStatement("SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned IS NULL").use { ps ->
            ps.setLong(1, memberId)
            ps.executeQuery().use { it.next(); it.getInt(1) }
        }
}

class LendingRulesBefore(c: Connection) {
    private val loans = SqliteLoans(c)                     // policy -> detail: welded to SQLite
    fun mayBorrow(memberId: Long) = loans.openLoanCount(memberId) < 3
}

// ---------- After: both sides depend on an abstraction the POLICY owns ----------
interface LoanStore {
    fun openLoanCount(memberId: Long): Int
}

class LendingRules(private val loans: LoanStore) {         // policy -> abstraction
    fun mayBorrow(memberId: Long) = loans.openLoanCount(memberId) < 3
}

class SqliteLoanStore(private val c: Connection) : LoanStore {     // detail -> abstraction
    override fun openLoanCount(memberId: Long) = SqliteLoans(c).openLoanCount(memberId)
}

class InMemoryLoanStore(private val open: Map<Long, Int>) : LoanStore {
    override fun openLoanCount(memberId: Long) = open[memberId] ?: 0
}

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { c ->
        c.createStatement().use { st ->
            st.executeUpdate("CREATE TABLE loans (member_id INTEGER, returned TEXT)")
            st.executeUpdate("INSERT INTO loans VALUES (1, NULL), (1, NULL), (1, NULL), (2, NULL), (2, '2026-10-02')")
        }
        val rules = listOf(
            "before, SQLite   " to LendingRulesBefore(c)::mayBorrow,
            "after,  SQLite   " to LendingRules(SqliteLoanStore(c))::mayBorrow,
            "after,  in-memory" to LendingRules(InMemoryLoanStore(mapOf(1L to 3, 2L to 1)))::mayBorrow,
        )
        for ((name, mayBorrow) in rules) println("$name  member 1: ${mayBorrow(1)}  member 2: ${mayBorrow(2)}")
    }
}
