import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Fail fast if a query's plan scans a whole table or index.
// Use it in a test so a dropped index breaks the build, not production.
public class PlanGuard {
    static void requireNoScan(Connection c, String sql) throws SQLException {
        List<String> plan = new ArrayList<>();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("EXPLAIN QUERY PLAN " + sql)) {
            while (rs.next()) plan.add(rs.getString("detail"));
        }
        List<String> scans = plan.stream().filter(d -> d.startsWith("SCAN")).toList();
        if (!scans.isEmpty()) throw new IllegalStateException("full scan in plan: " + scans);
        System.out.println("ok: " + plan);
    }

    public static void main(String[] args) throws SQLException {
        String q = "SELECT title, year FROM books WHERE author_id = 7 AND year BETWEEN 1990 AND 2000";
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            try {
                requireNoScan(c, q);
            } catch (IllegalStateException e) {
                System.out.println("caught: " + e.getMessage());
            }
            try (Statement st = c.createStatement()) { st.execute("CREATE INDEX idx_books_author_year ON books(author_id, year)"); }
            requireNoScan(c, q);
            try (Statement st = c.createStatement()) { st.execute("DROP INDEX idx_books_author_year"); }
        }
    }
}
