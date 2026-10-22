import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LostUpdate {
    static final String URL = "jdbc:h2:mem:counter;DB_CLOSE_DELAY=-1";
    static final int THREADS = 4, DEPOSITS = 250;   // each thread adds 1 cent this many times: expect 1000
    static final AtomicInteger retries = new AtomicInteger();

    @FunctionalInterface interface Deposit { void run(Connection c) throws SQLException; }

    static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) { st.execute("SET LOCK_TIMEOUT 10000"); }   // READ COMMITTED: H2's default
        c.setAutoCommit(false);
        return c;
    }
    static int intOf(Connection c, String sql, int... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setInt(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }
    static int update(Connection c, String sql, int... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setInt(i + 1, args[i]);
            return ps.executeUpdate();
        }
    }

    public static void main(String[] a) throws Exception {
        // Four ways to add one cent. Each call is one transaction.
        Map<String, Deposit> strategies = new LinkedHashMap<>();
        strategies.put("read, then write", c -> {
            int b = intOf(c, "SELECT balance_cents FROM accounts WHERE id = 1");
            update(c, "UPDATE accounts SET balance_cents = ? WHERE id = 1", b + 1);
            c.commit();
        });
        strategies.put("atomic UPDATE", c -> {
            update(c, "UPDATE accounts SET balance_cents = balance_cents + 1 WHERE id = 1");
            c.commit();
        });
        strategies.put("SELECT FOR UPDATE", c -> {
            int b = intOf(c, "SELECT balance_cents FROM accounts WHERE id = 1 FOR UPDATE");   // row locked until commit
            update(c, "UPDATE accounts SET balance_cents = ? WHERE id = 1", b + 1);
            c.commit();
        });
        strategies.put("optimistic version", c -> {
            while (true) {
                int b, v;
                try (PreparedStatement ps = c.prepareStatement("SELECT balance_cents, version FROM accounts WHERE id = 1");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next(); b = rs.getInt(1); v = rs.getInt(2);        // ONE read: a consistent pair
                }
                int changed = update(c, "UPDATE accounts SET balance_cents = ?, version = version + 1 "
                                      + "WHERE id = 1 AND version = ?", b + 1, v);
                c.commit();
                if (changed == 1) break;                  // nobody else wrote since we read
                retries.incrementAndGet();                // someone did: read again and retry
            }
        });

        for (var e : strategies.entrySet()) {
            int runs = e.getKey().equals("read, then write") ? 3 : 1;
            for (int r = 0; r < runs; r++) {
                try (Connection c = connect()) {
                    update(c, "DROP TABLE IF EXISTS accounts");
                    update(c, "CREATE TABLE accounts (id INT PRIMARY KEY, balance_cents INT NOT NULL, version INT NOT NULL)");
                    update(c, "INSERT INTO accounts VALUES (1, 0, 0)"); c.commit();
                }
                retries.set(0);
                CountDownLatch gate = new CountDownLatch(1);
                long start = System.nanoTime();
                try (ExecutorService pool = Executors.newFixedThreadPool(THREADS)) {
                    for (int t = 0; t < THREADS; t++) {
                        pool.submit(() -> {
                            try (Connection c = connect()) {
                                gate.await();
                                for (int i = 0; i < DEPOSITS; i++) e.getValue().run(c);
                            }
                            return null;
                        });
                    }
                    gate.countDown();
                }                                         // close() waits for every task
                long ms = (System.nanoTime() - start) / 1_000_000;
                int total;
                try (Connection c = connect()) { total = intOf(c, "SELECT balance_cents FROM accounts WHERE id = 1"); }
                System.out.printf("%-19s balance %4d of %d  (%d ms%s)%n", e.getKey(), total, THREADS * DEPOSITS, ms,
                        e.getKey().startsWith("optimistic") ? ", " + retries.get() + " retries" : "");
            }
        }
    }
}
