import java.sql.*;
import java.util.List;

// What indexes cost: slower inserts and more disk.
public class IndexCost {
    static void exec(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) { st.execute(sql); }
    }

    static long longOf(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getLong(1); }
    }

    static double insertMillis(Connection conn, List<String> indexes) throws SQLException {
        exec(conn, "DROP TABLE IF EXISTS loans_copy");
        exec(conn, "CREATE TABLE loans_copy (id INTEGER PRIMARY KEY, book_id INTEGER, member_id INTEGER, loaned TEXT, returned TEXT)");
        for (String col : indexes) exec(conn, "CREATE INDEX idx_copy_" + col + " ON loans_copy(" + col + ")");
        conn.setAutoCommit(false);                         // one transaction for all 50,000 rows
        long start = System.nanoTime();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO loans_copy VALUES (?, ?, ?, ?, NULL)")) {
            for (int i = 1; i <= 50_000; i++) {
                ps.setInt(1, i); ps.setInt(2, (i * 104729) % 50_000 + 1);
                ps.setInt(3, (i * 7907) % 20_000 + 1); ps.setString(4, String.format("2026-%02d-%02d", i % 12 + 1, i % 28 + 1));
                ps.addBatch();
            }
            ps.executeBatch();
        }
        conn.commit();
        conn.setAutoCommit(true);
        return (System.nanoTime() - start) / 1e6;
    }

    public static void main(String[] args) throws SQLException {
        List<String> cols = List.of("book_id", "member_id", "loaned");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            insertMillis(conn, List.of());                                    // warm-up
            System.out.printf("insert 50k, 0 indexes: %5.0f ms%n", insertMillis(conn, List.of()));
            System.out.printf("insert 50k, 3 indexes: %5.0f ms%n", insertMillis(conn, cols));
            exec(conn, "DROP TABLE loans_copy");
            exec(conn, "VACUUM");

            long pageSize = longOf(conn, "PRAGMA page_size");
            long before = longOf(conn, "PRAGMA page_count") * pageSize;
            for (String col : cols) exec(conn, "CREATE INDEX idx_loans_" + col + " ON loans(" + col + ")");
            long after = longOf(conn, "PRAGMA page_count") * pageSize;
            System.out.printf("file before: %.1f MB, after 3 indexes on loans: %.1f MB%n", before / 1e6, after / 1e6);
            for (String col : cols) exec(conn, "DROP INDEX idx_loans_" + col);
            exec(conn, "VACUUM");                                            // leave db as found
        }
    }
}
