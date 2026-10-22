import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Injected {
    interface LoanStore {
        List<LocalDate> openLoanDates(long memberId);
    }

    // The service asks for what it needs. It never builds a connection or reads the system clock.
    static final class LendingService {
        private final LoanStore loans;
        private final Clock clock;

        LendingService(LoanStore loans, Clock clock) {
            this.loans = loans;
            this.clock = clock;
        }

        LendingService(LoanStore loans) { this(loans, Clock.systemUTC()); }   // the production default

        boolean mayBorrow(long memberId) {
            LocalDate cutoff = LocalDate.now(clock).minusDays(14);
            return loans.openLoanDates(memberId).stream().noneMatch(d -> d.isBefore(cutoff));
        }
    }

    static final class SqliteLoanStore implements LoanStore {
        private final Connection c;
        SqliteLoanStore(Connection c) { this.c = c; }
        public List<LocalDate> openLoanDates(long memberId) {
            try (PreparedStatement ps = c.prepareStatement("SELECT loaned FROM loans WHERE member_id = ? AND returned IS NULL")) {
                ps.setLong(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<LocalDate> out = new ArrayList<>();
                    while (rs.next()) out.add(LocalDate.parse(rs.getString(1)));
                    return out;
                }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
    }

    static final class InMemoryLoanStore implements LoanStore {
        private final Map<Long, List<LocalDate>> open;
        InMemoryLoanStore(Map<Long, List<LocalDate>> open) { this.open = open; }
        public List<LocalDate> openLoanDates(long memberId) { return open.getOrDefault(memberId, List.of()); }
    }

    // The composition root: the ONE place that knows which concrete classes are used.
    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:"); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE loans (member_id INTEGER, loaned TEXT, returned TEXT)");
            st.executeUpdate("INSERT INTO loans VALUES (1, '2026-10-10', NULL)");
            LendingService production = new LendingService(new SqliteLoanStore(c), Clock.systemUTC());
            System.out.println("production wiring, today " + LocalDate.now(Clock.systemUTC()) + ": member 1 may borrow = " + production.mayBorrow(1));
        }

        // A test wires the same class differently: no file, and a day we choose.
        LoanStore store = new InMemoryLoanStore(Map.of(1L, List.of(LocalDate.parse("2026-10-10"))));
        Clock oct30 = Clock.fixed(Instant.parse("2026-10-30T09:00:00Z"), ZoneOffset.UTC);
        Clock oct24 = Clock.fixed(Instant.parse("2026-10-24T09:00:00Z"), ZoneOffset.UTC);
        System.out.println("test wiring, fixed 2026-10-30: member 1 may borrow = " + new LendingService(store, oct30).mayBorrow(1));
        System.out.println("test wiring, fixed 2026-10-24: member 1 may borrow = " + new LendingService(store, oct24).mayBorrow(1));
    }
}
