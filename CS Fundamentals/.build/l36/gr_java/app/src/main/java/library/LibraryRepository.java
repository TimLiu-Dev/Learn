package library;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryRepository {
    public sealed interface LendResult permits Lent, AlreadyOnLoan, NoSuchBook, NoSuchMember {}
    public record Lent(long loanId) implements LendResult {}
    public record AlreadyOnLoan() implements LendResult {}
    public record NoSuchBook() implements LendResult {}
    public record NoSuchMember() implements LendResult {}

    public record Overdue(String title, String member, LocalDate loaned) {}
    public record Borrower(String name, int loans) {}

    @FunctionalInterface interface SqlWork<T> { T run(Connection c) throws SQLException; }
    @FunctionalInterface interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }

    private final String url;

    public LibraryRepository(String dbFile) {
        // IMMEDIATE: every transaction takes the write lock at BEGIN, so two lenders queue up
        // instead of both reading "available" and then failing with SQLITE_BUSY.
        this.url = "jdbc:sqlite:" + dbFile + "?transaction_mode=IMMEDIATE";
    }

    private Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }   // off by default in SQLite
        return c;
    }

    private <T> T transaction(SqlWork<T> work) throws SQLException {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private static <T> List<T> toList(ResultSet rs, RowMapper<T> mapper) throws SQLException {
        List<T> out = new ArrayList<>();
        while (rs.next()) out.add(mapper.map(rs));
        return out;
    }

    private static boolean exists(Connection c, String sql, long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static long insert(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getLong(1); }
        }
    }

    public void createSchema() throws SQLException {
        transaction(c -> {
            try (Statement st = c.createStatement()) { for (String s : Schema.STATEMENTS) st.executeUpdate(s); }
            return null;
        });
    }

    public long addMember(String name, String email) throws SQLException {
        return transaction(c -> insert(c, "INSERT INTO members (name, email) VALUES (?, ?)", name, email));
    }

    public long addBook(String title, Integer year) throws SQLException {
        return transaction(c -> insert(c, "INSERT INTO books (title, year) VALUES (?, ?)", title, year));
    }

    public String memberName(long id) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT name FROM members WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        }
    }

    // Check and insert in ONE transaction: nobody can lend the book between our check and our insert.
    public LendResult lend(long bookId, long memberId, LocalDate today) throws SQLException {
        return transaction(c -> {
            if (!exists(c, "SELECT 1 FROM books WHERE id = ?", bookId)) return new NoSuchBook();
            if (!exists(c, "SELECT 1 FROM members WHERE id = ?", memberId)) return new NoSuchMember();
            if (exists(c, "SELECT 1 FROM loans WHERE book_id = ? AND returned IS NULL", bookId)) return new AlreadyOnLoan();
            return new Lent(insert(c, "INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)",
                                   bookId, memberId, today.toString()));
        });
    }

    public boolean returnBook(long bookId, LocalDate today) throws SQLException {
        return transaction(c -> {
            try (PreparedStatement ps = c.prepareStatement("UPDATE loans SET returned = ? WHERE book_id = ? AND returned IS NULL")) {
                ps.setString(1, today.toString()); ps.setLong(2, bookId);
                return ps.executeUpdate() == 1;
            }
        });
    }

    public List<Overdue> overdueLoans(LocalDate today) throws SQLException { return overdueLoans(today, 14); }

    public List<Overdue> overdueLoans(LocalDate today, long loanDays) throws SQLException {
        String sql = """
            SELECT b.title, m.name, l.loaned FROM loans l
            JOIN books b ON b.id = l.book_id JOIN members m ON m.id = l.member_id
            WHERE l.returned IS NULL AND l.loaned < ? ORDER BY l.loaned, b.title""";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, today.minusDays(loanDays).toString());
            try (ResultSet rs = ps.executeQuery()) {
                return toList(rs, r -> new Overdue(r.getString(1), r.getString(2), LocalDate.parse(r.getString(3))));
            }
        }
    }

    public List<Borrower> topBorrowers(int limit) throws SQLException {
        String sql = """
            SELECT m.name, COUNT(*) AS n FROM loans l JOIN members m ON m.id = l.member_id
            GROUP BY m.id, m.name ORDER BY n DESC, m.name LIMIT ?""";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) { return toList(rs, r -> new Borrower(r.getString(1), r.getInt(2))); }
        }
    }

    public int loanCount() throws SQLException {
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM loans")) {
            rs.next(); return rs.getInt(1);
        }
    }
}
