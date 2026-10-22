import java.sql.*;

// Time one query before and after adding an index.
// Usage: java --enable-native-access=ALL-UNNAMED -cp sqlite-jdbc.jar IndexTiming.java big.db
public class IndexTiming {
    static final String SQL = "SELECT id, book_id, loaned FROM loans WHERE member_id = ?";

    static double timeQueries(Connection conn, int runs) throws SQLException {
        int rows = 0;
        long start = System.nanoTime();
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            for (int i = 0; i < runs; i++) {
                ps.setInt(1, (i * 97) % 20_000 + 1);          // a different member each time
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) rows++; }
            }
        }
        double micros = (System.nanoTime() - start) / 1_000.0 / runs;
        if (rows != runs * 10) throw new IllegalStateException("expected 10 loans per member, got " + rows);
        return micros;
    }

    static void exec(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) { st.execute(sql); }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            exec(conn, "DROP INDEX IF EXISTS idx_loans_member");
            timeQueries(conn, 20);                                  // warm-up
            System.out.printf("no index:   %8.1f µs per query%n", timeQueries(conn, 200));

            long t0 = System.nanoTime();
            exec(conn, "CREATE INDEX idx_loans_member ON loans(member_id)");
            System.out.printf("CREATE INDEX took %.0f ms%n", (System.nanoTime() - t0) / 1e6);

            timeQueries(conn, 200);                                 // warm-up
            System.out.printf("with index: %8.1f µs per query%n", timeQueries(conn, 2_000));
            exec(conn, "DROP INDEX idx_loans_member");             // leave db as found
        }
    }
}
