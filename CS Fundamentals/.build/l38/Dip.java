import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Dip {
    // ---------- Before: the policy depends on a concrete detail ----------
    static final class SqliteLoans {
        private final Connection c;
        SqliteLoans(Connection c) { this.c = c; }
        int openLoanCount(long memberId) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned IS NULL")) {
                ps.setLong(1, memberId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
    }

    static final class LendingRulesBefore {
        private final SqliteLoans loans;                       // policy -> detail: welded to SQLite
        LendingRulesBefore(Connection c) { this.loans = new SqliteLoans(c); }
        boolean mayBorrow(long memberId) { return loans.openLoanCount(memberId) < 3; }
    }

    // ---------- After: both sides depend on an abstraction the POLICY owns ----------
    interface LoanStore {
        int openLoanCount(long memberId);
    }

    static final class LendingRules {                          // policy -> abstraction
        private final LoanStore loans;
        LendingRules(LoanStore loans) { this.loans = loans; }
        boolean mayBorrow(long memberId) { return loans.openLoanCount(memberId) < 3; }
    }

    static final class SqliteLoanStore implements LoanStore {  // detail -> abstraction
        private final SqliteLoans loans;
        SqliteLoanStore(Connection c) { this.loans = new SqliteLoans(c); }
        public int openLoanCount(long memberId) { return loans.openLoanCount(memberId); }
    }

    static final class InMemoryLoanStore implements LoanStore {
        private final Map<Long, Integer> open;
        InMemoryLoanStore(Map<Long, Integer> open) { this.open = open; }
        public int openLoanCount(long memberId) { return open.getOrDefault(memberId, 0); }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE loans (member_id INTEGER, returned TEXT)");
                st.executeUpdate("INSERT INTO loans VALUES (1, NULL), (1, NULL), (1, NULL), (2, NULL), (2, '2026-10-02')");
            }
            LendingRulesBefore before = new LendingRulesBefore(c);
            LendingRules sqlite = new LendingRules(new SqliteLoanStore(c));
            LendingRules memory = new LendingRules(new InMemoryLoanStore(Map.of(1L, 3, 2L, 1)));
            System.out.println("before, SQLite     member 1: " + before.mayBorrow(1) + "  member 2: " + before.mayBorrow(2));
            System.out.println("after,  SQLite     member 1: " + sqlite.mayBorrow(1) + "  member 2: " + sqlite.mayBorrow(2));
            System.out.println("after,  in-memory  member 1: " + memory.mayBorrow(1) + "  member 2: " + memory.mayBorrow(2));
        }
    }
}
