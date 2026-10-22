import java.sql.*;

public class Autocommit {
    // java.util.function interfaces can't throw SQLException, so we declare our own.
    @FunctionalInterface
    interface SqlWork<T> { T run(Connection c) throws SQLException; }

    // Runs work inside one transaction: commit if it returns, roll back if it throws.
    static <T> T transaction(Connection c, SqlWork<T> work) throws SQLException {
        c.setAutoCommit(false);
        try {
            T result = work.run(c);
            c.commit();
            return result;
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    static void exec(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement()) { st.executeUpdate(sql); }
    }

    static String balances(Connection c) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT owner, balance_cents FROM accounts ORDER BY id")) {
            while (rs.next()) sb.append(sb.isEmpty() ? "" : " ").append(rs.getString(1)).append('=').append(rs.getInt(2));
        }
        return sb.toString();
    }

    static void reset(Connection c) throws SQLException {
        exec(c, "DROP TABLE IF EXISTS accounts");
        exec(c, "CREATE TABLE accounts (id INTEGER PRIMARY KEY, owner TEXT, "
              + "balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0))");
        exec(c, "INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)");
    }

    static Void transfer(Connection c, int from, int to, int cents, boolean crash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?")) {
            ps.setInt(1, cents); ps.setInt(2, to); ps.executeUpdate();
        }
        if (crash) throw new IllegalStateException("power cut between the two UPDATEs");
        try (PreparedStatement ps = c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents - ? WHERE id = ?")) {
            ps.setInt(1, cents); ps.setInt(2, from); ps.executeUpdate();
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            System.out.println("autocommit by default: " + c.getAutoCommit());

            reset(c);
            try { transfer(c, 1, 2, 2500, true); } catch (IllegalStateException e) { System.out.println("1. crashed: " + e.getMessage()); }
            System.out.println("   autocommit:  " + balances(c));

            reset(c);
            try { transaction(c, conn -> transfer(conn, 1, 2, 2500, true)); } catch (IllegalStateException e) { System.out.println("2. crashed: " + e.getMessage()); }
            System.out.println("   transaction: " + balances(c));

            reset(c);
            try { transaction(c, conn -> transfer(conn, 1, 2, 15000, false)); } catch (SQLException e) { System.out.println("3. " + e.getMessage()); }
            System.out.println("   transaction: " + balances(c));

            transaction(c, conn -> transfer(conn, 1, 2, 2500, false));
            System.out.println("4. committed:   " + balances(c));
        }
    }
}
