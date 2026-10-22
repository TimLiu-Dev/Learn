import java.sql.*;
import java.util.concurrent.*;

public class Deadlock {
    static final String URL = "jdbc:h2:mem:dl;DB_CLOSE_DELAY=-1";

    static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) { st.execute("SET LOCK_TIMEOUT 5000"); }
        c.setAutoCommit(false);
        return c;
    }

    static void add(Connection c, int id, int cents) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?")) {
            ps.setInt(1, cents); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    // Unordered: lock src then dst, and wait at the barrier so both threads hold their first row.
    // Ordered: always lock the lower id first. (No barrier: the second thread waits on the row lock instead.)
    static String transfer(int src, int dst, int cents, boolean ordered, CyclicBarrier barrier) throws Exception {
        try (Connection c = connect()) {
            try {
                int first = (!ordered || src < dst) ? src : dst;
                int second = first == src ? dst : src;
                add(c, first, first == src ? -cents : cents);
                if (!ordered) barrier.await();
                add(c, second, second == src ? -cents : cents);
                c.commit();
                return src + "->" + dst + " committed";
            } catch (SQLException e) {
                c.rollback();
                return src + "->" + dst + " [" + e.getSQLState() + "] " + e.getMessage().split(" Details")[0];
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection(URL); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE accounts (id INT PRIMARY KEY, balance_cents INT NOT NULL)");
            st.execute("INSERT INTO accounts VALUES (1, 10000), (2, 5000)");
        }
        for (boolean ordered : new boolean[]{false, true}) {
            System.out.println(ordered ? "== lock in id order" : "== lock in argument order");
            CyclicBarrier barrier = new CyclicBarrier(2);
            long start = System.nanoTime();
            try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
                Future<String> a = pool.submit(() -> transfer(1, 2, 100, ordered, barrier));
                Future<String> b = pool.submit(() -> transfer(2, 1, 300, ordered, barrier));
                System.out.println("  " + a.get()); System.out.println("  " + b.get());
            }
            System.out.println("  took " + (System.nanoTime() - start) / 1_000_000 + " ms");
        }
    }
}
