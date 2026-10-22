import java.sql.*;

public class ConnCost {
    static final int N = 500;

    static int countBooks(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
            rs.next(); return rs.getInt(1);
        }
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:library.db";
        for (int round = 1; round <= 2; round++) {            // round 1 includes JIT warm-up
            long t0 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                try (Connection c = DriverManager.getConnection(url)) { countBooks(c); }
            }
            double fresh = (System.nanoTime() - t0) / 1e6;

            t0 = System.nanoTime();
            try (Connection c = DriverManager.getConnection(url)) {
                for (int i = 0; i < N; i++) countBooks(c);
            }
            double reused = (System.nanoTime() - t0) / 1e6;
            System.out.printf("round %d: %d queries, new connection each %.1f ms, one reused connection %.1f ms%n", round, N, fresh, reused);
        }
    }
}
