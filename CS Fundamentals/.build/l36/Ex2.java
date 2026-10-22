import java.sql.*;
import java.util.concurrent.*;

public class Ex2 {
    static final String URL = "jdbc:h2:mem:oncall;DB_CLOSE_DELAY=-1";

    static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) { st.execute("SET LOCK_TIMEOUT 5000"); }
        c.setAutoCommit(false);
        return c;
    }

    static int onCall(Connection c, boolean lock) throws SQLException {
        // FOR UPDATE locks every row it returns until commit, so a second checker must wait its turn.
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM doctors WHERE on_call" + (lock ? " FOR UPDATE" : ""))) {
            int n = 0; while (rs.next()) n++; return n;
        }
    }

    static String goOffCall(String name, boolean lock, CountDownLatch bothChecked) throws Exception {
        try (Connection c = connect()) {
            int seen = onCall(c, lock);
            bothChecked.countDown();
            if (!lock) bothChecked.await();          // unlocked run: make both check before either writes
            String result;
            if (seen >= 2) {
                try (PreparedStatement ps = c.prepareStatement("UPDATE doctors SET on_call = FALSE WHERE name = ?")) {
                    ps.setString(1, name); ps.executeUpdate();
                }
                result = name + " saw " + seen + " on call, went off";
            } else result = name + " saw " + seen + " on call, stayed";
            c.commit();
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        for (boolean lock : new boolean[]{false, true}) {
            try (Connection c = connect(); Statement st = c.createStatement()) {
                st.execute("DROP TABLE IF EXISTS doctors");
                st.execute("CREATE TABLE doctors (name VARCHAR(20) PRIMARY KEY, on_call BOOLEAN)");
                st.execute("INSERT INTO doctors VALUES ('alice', TRUE), ('bob', TRUE)");
                c.commit();
            }
            System.out.println(lock ? "== with FOR UPDATE" : "== plain SELECT");
            CountDownLatch bothChecked = new CountDownLatch(2);
            try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
                Future<String> a = pool.submit(() -> goOffCall("alice", lock, bothChecked));
                Future<String> b = pool.submit(() -> goOffCall("bob", lock, bothChecked));
                System.out.println("  " + a.get()); System.out.println("  " + b.get());
            }
            try (Connection c = connect()) { System.out.println("  on call now: " + onCall(c, false)); }
        }
    }
}
