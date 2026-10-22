import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class SqliteBusy {
    static void sql(Connection c, String s) throws SQLException {
        try (Statement st = c.createStatement()) { st.executeUpdate(s); }
    }
    static int balance(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT balance_cents FROM accounts WHERE id = 1")) {
            rs.next(); return rs.getInt(1);
        }
    }

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("bank", ".db");
        String url = "jdbc:sqlite:" + file;
        try (Connection a = DriverManager.getConnection(url); Connection b = DriverManager.getConnection(url)) {
            sql(a, "CREATE TABLE accounts (id INTEGER PRIMARY KEY, balance_cents INTEGER NOT NULL)");
            sql(a, "INSERT INTO accounts VALUES (1, 10000)");
            sql(b, "PRAGMA busy_timeout = 200");              // wait at most 0.2 s for the write lock

            a.setAutoCommit(false);
            sql(a, "UPDATE accounts SET balance_cents = 9000 WHERE id = 1");   // a now holds the write lock
            System.out.println("b reads while a writes: " + balance(b));      // sees the committed 10000
            long t0 = System.nanoTime();
            try {
                sql(b, "UPDATE accounts SET balance_cents = 8000 WHERE id = 1");
            } catch (SQLException e) {
                System.out.println("b writes after " + (System.nanoTime() - t0) / 1_000_000 + " ms: " + e.getMessage());
            }
            a.commit();
            sql(b, "UPDATE accounts SET balance_cents = balance_cents - 1000 WHERE id = 1");
            System.out.println("after a commits, b's write works: " + balance(b));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
