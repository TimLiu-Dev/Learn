import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Five slow queries: print each plan and its average time.
// Usage: java --enable-native-access=ALL-UNNAMED -cp sqlite-jdbc.jar MakeItFast.java big.db
public class MakeItFast {
    record Query(String name, String sql, List<Object> params) {}

    static final List<Query> QUERIES = List.of(
        new Query("Q1 member history", """
            SELECT l.id, b.title, l.loaned FROM loans l JOIN books b ON b.id = l.book_id
            WHERE l.member_id = ? ORDER BY l.loaned DESC""", List.of(42)),
        new Query("Q2 long overdue", """
            SELECT id, member_id, loaned FROM loans
            WHERE returned IS NULL AND loaned < '2024-06-01'""", List.of()),
        new Query("Q3 cheap 2015 books", """
            SELECT title, price_cents FROM books
            WHERE year = ? AND price_cents < 800 ORDER BY price_cents""", List.of(2015)),
        new Query("Q4 login by email", """
            SELECT id, name FROM members WHERE lower(email) = lower(?)""", List.of("M42@Example.org")),
        new Query("Q5 top books in Jan", """
            SELECT book_id, count(*) AS n FROM loans
            WHERE loaned BETWEEN '2026-01-01' AND '2026-01-31'
            GROUP BY book_id ORDER BY n DESC, book_id LIMIT 5""", List.of()));

    static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }

    static String plan(Connection c, Query q) throws SQLException {
        List<String> details = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("EXPLAIN QUERY PLAN " + q.sql())) {
            bind(ps, q.params());
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) details.add(rs.getString("detail")); }
        }
        return String.join(" | ", details);
    }

    // Run until ~300 ms have passed (at least 5 runs); return {rows, µs per run}.
    static double[] time(Connection c, Query q) throws SQLException {
        int rows = 0, runs = 0;
        try (PreparedStatement ps = c.prepareStatement(q.sql())) {
            bind(ps, q.params());
            for (int i = 0; i < 3; i++) try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { } }  // warm-up
            long start = System.nanoTime();
            while (runs < 5 || System.nanoTime() - start < 300_000_000L) {
                rows = 0;
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) rows++; }
                runs++;
            }
            return new double[] { rows, (System.nanoTime() - start) / 1_000.0 / runs };
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            for (Query q : QUERIES) {
                double[] r = time(c, q);
                System.out.printf("%-20s %5d rows %10.1f µs%n", q.name(), (int) r[0], r[1]);
                System.out.println("    " + plan(c, q));
            }
        }
    }
}
