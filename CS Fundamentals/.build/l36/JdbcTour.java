import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTour {
    record Book(int id, String title, Integer year) {}

    @FunctionalInterface interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }

    // Read every row with mapper, then return a List. The ResultSet is still open while we read it.
    static <T> List<T> toList(ResultSet rs, RowMapper<T> mapper) throws SQLException {
        List<T> out = new ArrayList<>();
        while (rs.next()) out.add(mapper.map(rs));
        return out;
    }

    // getInt returns 0 for SQL NULL; wasNull() tells you which it really was.
    static Integer intOrNull(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            // 1. A query with parameters, mapped to a record.
            String sql = "SELECT b.id, b.title, b.year FROM books b JOIN authors a ON a.id = b.author_id "
                       + "WHERE a.name = ? ORDER BY b.year";
            List<Book> books;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "Anonymous");
                try (ResultSet rs = ps.executeQuery()) {
                    books = toList(rs, r -> new Book(r.getInt("id"), r.getString("title"), intOrNull(r, "year")));
                }
            }
            books.forEach(System.out::println);

            c.setAutoCommit(false);                              // everything below gets rolled back
            // 2. INSERT and read back the generated key; setNull for an unknown value.
            int newId;
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO members (name, email, joined) VALUES (?, ?, ?)",
                                                           Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Frances Allen"); ps.setString(2, "frances@example.com"); ps.setNull(3, Types.VARCHAR);
                System.out.println("rows inserted: " + ps.executeUpdate());
                try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); newId = keys.getInt(1); }
            }
            System.out.println("new member id: " + newId);

            // 3. executeUpdate returns how many rows changed.
            try (PreparedStatement ps = c.prepareStatement("UPDATE loans SET returned = ? WHERE returned IS NULL AND member_id = ?")) {
                ps.setString(1, "2026-10-21"); ps.setInt(2, 1);
                System.out.println("loans returned for member 1: " + ps.executeUpdate());
            }

            // 4. Streaming: read only as many rows as you need, while the ResultSet is open.
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT title FROM books ORDER BY price_cents DESC, title")) {
                List<String> titles = new ArrayList<>();
                while (titles.size() < 3 && rs.next()) titles.add(rs.getString(1));
                System.out.println("priciest: " + String.join(", ", titles));
            }
            c.rollback();
            c.setAutoCommit(true);
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM members")) {
                rs.next(); System.out.println("members after rollback: " + rs.getInt(1));
            }
        }
    }
}
