import java.sql.*;

public class Ex1 {
    sealed interface TransferResult permits Done, Rejected {}
    record Done() implements TransferResult {}
    record Rejected(String reason) implements TransferResult {}

    static int move(Connection c, int id, int delta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE accounts SET balance_cents = balance_cents + ? WHERE id = ?")) {
            ps.setInt(1, delta); ps.setInt(2, id);
            return ps.executeUpdate();                       // number of rows changed
        }
    }

    static TransferResult transfer(Connection c, int from, int to, int cents) throws SQLException {
        if (cents <= 0) return new Rejected("amount must be positive");
        c.setAutoCommit(false);
        try {
            if (move(c, to, cents) != 1) { c.rollback(); return new Rejected("no account " + to); }
            if (move(c, from, -cents) != 1) { c.rollback(); return new Rejected("no account " + from); }
            c.commit();
            return new Done();
        } catch (SQLException e) {
            c.rollback();
            return new Rejected("overdraft");
        } finally {
            c.setAutoCommit(true);
        }
    }

    static String balances(Connection c) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT owner, balance_cents FROM accounts ORDER BY id")) {
            while (rs.next()) sb.append(sb.isEmpty() ? "" : " ").append(rs.getString(1)).append('=').append(rs.getInt(2));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE accounts (id INTEGER PRIMARY KEY, owner TEXT, balance_cents INTEGER NOT NULL CHECK (balance_cents >= 0))");
                st.executeUpdate("INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)");
            }
            int[][] cases = {{1, 2, 2500}, {1, 99, 1000}, {99, 2, 1000}, {2, 1, 99999}, {1, 2, -5}};
            for (int[] t : cases) {
                System.out.println(t[0] + " -> " + t[1] + "  " + t[2] + ": " + transfer(c, t[0], t[1], t[2]) + "  | " + balances(c));
            }
        }
    }
}
