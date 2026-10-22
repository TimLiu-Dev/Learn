import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Print query plans from code: SQLite's EXPLAIN QUERY PLAN and H2's EXPLAIN.
// YEAR is a reserved word in standard SQL (and in H2), so it is quoted: "year".
public class Plans {
    static List<String> sqlitePlan(Connection conn, String sql) throws SQLException {
        List<String> details = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("EXPLAIN QUERY PLAN " + sql)) {
            while (rs.next()) details.add(rs.getString("detail"));
        }
        return details;
    }

    static String h2Plan(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("EXPLAIN " + sql)) {
            rs.next();
            return rs.getString(1);
        }
    }

    static void exec(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) { st.execute(sql); }
    }

    public static void main(String[] args) throws SQLException {
        String q = "SELECT title FROM books WHERE author_id = 7 AND \"year\" > 2000";
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            System.out.println("SQLite, no index:   " + sqlitePlan(c, q));
            exec(c, "CREATE INDEX idx_books_author_year ON books(author_id, year)");
            System.out.println("SQLite, with index: " + sqlitePlan(c, q));
            exec(c, "DROP INDEX idx_books_author_year");
        }
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:plans")) {
            exec(c, "CREATE TABLE books(id INT PRIMARY KEY, title VARCHAR(100), author_id INT, \"year\" INT)");
            exec(c, "INSERT INTO books SELECT x, 'Book ' || x, MOD(x * 7919, 2000) + 1, 1950 + MOD(x * 31, 76) FROM SYSTEM_RANGE(1, 50000)");
            System.out.println("H2, no index:\n" + h2Plan(c, q));
            exec(c, "CREATE INDEX idx_books_author_year ON books(author_id, \"year\")");
            System.out.println("H2, with index:\n" + h2Plan(c, q));
        }
    }
}
