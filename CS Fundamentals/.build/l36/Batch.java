import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class Batch {
    static final int ROWS = 10_000;
    static final String INSERT = "INSERT INTO loans (book_id, member_id, loaned) VALUES (?, ?, ?)";

    @FunctionalInterface interface Work { void run(Connection c) throws SQLException; }

    static Connection freshDb() throws Exception {
        Path file = Files.createTempFile("batch", ".db");
        file.toFile().deleteOnExit();
        Connection c = DriverManager.getConnection("jdbc:sqlite:" + file);
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE loans (id INTEGER PRIMARY KEY, book_id INTEGER, member_id INTEGER, loaned TEXT)");
        }
        return c;
    }

    static void timed(String label, Work work) throws Exception {
        try (Connection c = freshDb()) {
            long t0 = System.nanoTime();
            work.run(c);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM loans")) {
                rs.next();
                System.out.printf("%-30s %6d ms  (%d rows)%n", label, ms, rs.getInt(1));
            }
        }
    }

    static void fill(PreparedStatement ps, int i) throws SQLException {
        ps.setInt(1, i % 20 + 1); ps.setInt(2, i % 8 + 1); ps.setString(3, "2026-10-21");
    }

    public static void main(String[] args) throws Exception {
        timed("one by one, autocommit", c -> {
            try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                for (int i = 0; i < ROWS; i++) { fill(ps, i); ps.executeUpdate(); }
            }
        });
        timed("addBatch, autocommit", c -> {
            try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                for (int i = 0; i < ROWS; i++) { fill(ps, i); ps.addBatch(); }
                ps.executeBatch();
            }
        });
        timed("one by one, one transaction", c -> {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                for (int i = 0; i < ROWS; i++) { fill(ps, i); ps.executeUpdate(); }
            }
            c.commit();
        });
        timed("addBatch, one transaction", c -> {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                for (int i = 0; i < ROWS; i++) { fill(ps, i); ps.addBatch(); }
                ps.executeBatch();
            }
            c.commit();
        });
    }
}
