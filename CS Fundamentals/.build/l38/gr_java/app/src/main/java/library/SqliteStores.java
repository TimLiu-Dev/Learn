package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// The details. They depend on the abstractions; nothing depends on them except App.main.
public final class SqliteStores {
    private SqliteStores() {}

    public static void createSchema(String url) throws SQLException {
        try (Connection c = DriverManager.getConnection(url); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY, name TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS loans (id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, "
                    + "member_id INTEGER NOT NULL REFERENCES members(id), loaned TEXT NOT NULL, returned TEXT)");
            st.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL");
        }
    }

    static Connection connect(String url) throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }

    public static final class SqliteMemberStore implements MemberStore {
        private final String url;
        public SqliteMemberStore(String url) { this.url = url; }

        public Member add(String name) {
            try (Connection c = connect(url);
                 PreparedStatement ps = c.prepareStatement("INSERT INTO members (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return new Member(keys.getLong(1), name); }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }

        public Member find(long memberId) {
            try (Connection c = connect(url); PreparedStatement ps = c.prepareStatement("SELECT id, name FROM members WHERE id = ?")) {
                ps.setLong(1, memberId);
                try (ResultSet rs = ps.executeQuery()) { return rs.next() ? new Member(rs.getLong(1), rs.getString(2)) : null; }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
    }

    public static final class SqliteLoanStore implements LoanStore {
        private static final String COLUMNS = "id, book_id, member_id, loaned, returned";
        private final String url;
        public SqliteLoanStore(String url) { this.url = url; }

        private static Loan toLoan(ResultSet rs) throws SQLException {
            String returned = rs.getString(5);
            return new Loan(rs.getLong(1), rs.getLong(2), rs.getLong(3), LocalDate.parse(rs.getString(4)),
                    returned == null ? null : LocalDate.parse(returned));
        }

        private List<Loan> query(String sql, long id) {
            try (Connection c = connect(url); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Loan> out = new ArrayList<>();
                    while (rs.next()) out.add(toLoan(rs));
                    return out;
                }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }

        public Loan add(long bookId, long memberId, LocalDate loaned) {
            try (Connection c = connect(url); PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, bookId); ps.setLong(2, memberId); ps.setString(3, loaned.toString());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return new Loan(keys.getLong(1), bookId, memberId, loaned, null); }
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }

        public Loan find(long loanId) {
            return query("SELECT " + COLUMNS + " FROM loans WHERE id = ?", loanId).stream().findFirst().orElse(null);
        }

        public List<Loan> openForMember(long memberId) {
            return query("SELECT " + COLUMNS + " FROM loans WHERE member_id = ? AND returned IS NULL ORDER BY id", memberId);
        }

        public Loan openForBook(long bookId) {
            return query("SELECT " + COLUMNS + " FROM loans WHERE book_id = ? AND returned IS NULL", bookId).stream().findFirst().orElse(null);
        }

        public boolean close(long loanId, LocalDate returned) {
            try (Connection c = connect(url);
                 PreparedStatement ps = c.prepareStatement("UPDATE loans SET returned = ? WHERE id = ? AND returned IS NULL")) {
                ps.setString(1, returned.toString()); ps.setLong(2, loanId);
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }
    }
}
