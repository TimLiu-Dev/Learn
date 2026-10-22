import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Anomalies {
    static final String URL = "jdbc:h2:mem:bank;DB_CLOSE_DELAY=-1";   // one in-memory DB, shared by name

    @FunctionalInterface interface Scenario { String run(Connection t1, Connection t2) throws SQLException; }

    static Connection open(String level) throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute("SET LOCK_TIMEOUT 300");                                   // wait 0.3 s at most
            st.execute("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL " + level);
        }
        c.setAutoCommit(false);
        return c;
    }

    static int intOf(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getInt(1); }
    }
    static void run(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) { st.executeUpdate(sql); }
    }

    static void reset() throws SQLException {
        try (Connection c = DriverManager.getConnection(URL)) {
            run(c, "DROP TABLE IF EXISTS accounts"); run(c, "DROP TABLE IF EXISTS doctors");
            run(c, "CREATE TABLE accounts (id INT PRIMARY KEY, owner VARCHAR(20), balance_cents INT NOT NULL)");
            run(c, "INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)");
            run(c, "CREATE TABLE doctors (name VARCHAR(20) PRIMARY KEY, on_call BOOLEAN)");
            run(c, "INSERT INTO doctors VALUES ('alice', TRUE), ('bob', TRUE)");
        }
    }

    // Each scenario interleaves two transactions step by step on ONE thread, so the output is reproducible.
    static String dirtyRead(Connection t1, Connection t2) throws SQLException {
        run(t1, "UPDATE accounts SET balance_cents = 0 WHERE id = 1");             // not committed
        int seen = intOf(t2, "SELECT balance_cents FROM accounts WHERE id = 1");
        t1.rollback();
        return seen == 0 ? "HAPPENS (saw 0)" : "no (saw " + seen + ")";
    }

    static String nonRepeatableRead(Connection t1, Connection t2) throws SQLException {
        int first = intOf(t1, "SELECT balance_cents FROM accounts WHERE id = 1");
        run(t2, "UPDATE accounts SET balance_cents = 9000 WHERE id = 1"); t2.commit();
        int second = intOf(t1, "SELECT balance_cents FROM accounts WHERE id = 1");
        return (first != second ? "HAPPENS (" : "no (") + first + ", " + second + ")";
    }

    static String phantomRead(Connection t1, Connection t2) throws SQLException {
        int first = intOf(t1, "SELECT COUNT(*) FROM accounts WHERE balance_cents > 1000");
        run(t2, "INSERT INTO accounts VALUES (3, 'Cy', 7000)"); t2.commit();
        int second = intOf(t1, "SELECT COUNT(*) FROM accounts WHERE balance_cents > 1000");
        return (first != second ? "HAPPENS (" : "no (") + first + ", " + second + ")";
    }

    static String lostUpdate(Connection t1, Connection t2) throws SQLException {
        int b1 = intOf(t1, "SELECT balance_cents FROM accounts WHERE id = 1");       // both read 10000
        int b2 = intOf(t2, "SELECT balance_cents FROM accounts WHERE id = 1");
        run(t1, "UPDATE accounts SET balance_cents = " + (b1 + 1000) + " WHERE id = 1"); t1.commit();
        try {
            run(t2, "UPDATE accounts SET balance_cents = " + (b2 + 1000) + " WHERE id = 1"); t2.commit();
            return "HAPPENS (final " + intOf(t1, "SELECT balance_cents FROM accounts WHERE id = 1") + ")";
        } catch (SQLException e) { return "no: T2 got " + e.getSQLState(); }
    }

    static String writeSkew(Connection t1, Connection t2) throws SQLException {
        // Rule: at least one doctor must stay on call.
        int n1 = intOf(t1, "SELECT COUNT(*) FROM doctors WHERE on_call");          // both check first: 2 on call
        int n2 = intOf(t2, "SELECT COUNT(*) FROM doctors WHERE on_call");
        if (n1 >= 2) run(t1, "UPDATE doctors SET on_call = FALSE WHERE name = 'alice'");
        if (n2 >= 2) run(t2, "UPDATE doctors SET on_call = FALSE WHERE name = 'bob'");
        t1.commit(); t2.commit();
        int left = intOf(t1, "SELECT COUNT(*) FROM doctors WHERE on_call");
        return left == 0 ? "HAPPENS (0 on call)" : "no (" + left + " on call)";
    }

    public static void main(String[] args) throws SQLException {
        List<String> levels = List.of("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE");
        Map<String, Scenario> scenarios = new LinkedHashMap<>();
        scenarios.put("dirty read", Anomalies::dirtyRead);
        scenarios.put("non-repeatable read", Anomalies::nonRepeatableRead);
        scenarios.put("phantom read", Anomalies::phantomRead);
        scenarios.put("lost update", Anomalies::lostUpdate);
        scenarios.put("write skew", Anomalies::writeSkew);
        for (String level : levels) {
            System.out.println("== " + level);
            for (var e : scenarios.entrySet()) {
                reset();
                try (Connection t1 = open(level); Connection t2 = open(level)) {
                    System.out.printf("  %-20s %s%n", e.getKey(), e.getValue().run(t1, t2));
                }
            }
        }
    }
}
