import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Exercise: let the database check the fencing token.
// A nightly job rewrites report #1. Each run gets a token from the lock service.
public class FencedJob {
    interface Pause { void run() throws SQLException; }

    static void reset(Connection db) throws SQLException {
        try (Statement st = db.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS report");
            st.executeUpdate("CREATE TABLE report(id INTEGER PRIMARY KEY, body TEXT NOT NULL, fence INTEGER NOT NULL)");
            st.executeUpdate("INSERT INTO report VALUES (1, 'v0', 0)");
        }
    }

    static String body(Connection db) throws SQLException {
        try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery("SELECT body FROM report WHERE id = 1")) {
            rs.next(); return rs.getString(1);
        }
    }

    // Two statements: read the fence, then write. 'pause' runs in between.
    static boolean checkThenWrite(Connection db, String body, long token, Pause pause) throws SQLException {
        long fence;
        try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery("SELECT fence FROM report WHERE id = 1")) {
            rs.next(); fence = rs.getLong(1);
        }
        if (token < fence) return false;
        pause.run();
        try (PreparedStatement ps = db.prepareStatement("UPDATE report SET body = ?, fence = ? WHERE id = 1")) {
            ps.setString(1, body); ps.setLong(2, token); ps.executeUpdate();
        }
        return true;
    }

    // One statement: the check and the write are a single atomic step inside the database.
    static boolean fencedWrite(Connection db, String body, long token) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement("UPDATE report SET body = ?, fence = ? WHERE id = 1 AND fence <= ?")) {
            ps.setString(1, body); ps.setLong(2, token); ps.setLong(3, token);
            return ps.executeUpdate() == 1;               // 0 rows: a newer token got there first
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            reset(db);
            System.out.println("check, then write:");
            boolean ok1 = checkThenWrite(db, "client1 (stale)", 33, () -> {
                System.out.println("  client1 passed the check, then paused");
                System.out.println("  client2 writes: " + checkThenWrite(db, "client2", 34, () -> {}));
            });
            System.out.println("  client1 writes: " + ok1);
            System.out.println("  report #1 = " + body(db));

            reset(db);
            System.out.println("one conditional UPDATE:");
            System.out.println("  client2 writes: " + fencedWrite(db, "client2", 34));
            System.out.println("  client1 writes: " + fencedWrite(db, "client1 (stale)", 33));
            System.out.println("  report #1 = " + body(db));
        }
    }
}
