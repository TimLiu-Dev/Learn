import java.sql.*;

// Constraints are business rules the database enforces for you.
public class Constraints {
    static void tryUpdate(Connection c, String label, String sql) {
        try (Statement st = c.createStatement()) {
            int n = st.executeUpdate(sql);
            System.out.println(label + ": OK, " + n + " row(s)");
        } catch (SQLException e) {
            System.out.println(label + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            String badLoan = "INSERT INTO loans (book_id, member_id, loaned) VALUES (999, 1, '2026-10-21')";
            tryUpdate(c, "FK off (the default)", badLoan);               // book 999 doesn't exist!
            try (Statement st = c.createStatement()) { st.executeUpdate("DELETE FROM loans WHERE book_id = 999"); }

            try (Statement st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }  // per connection
            tryUpdate(c, "FK on", badLoan);
            tryUpdate(c, "UNIQUE", "INSERT INTO members (name, email) VALUES ('Ada 2', 'ada@example.org')");
            tryUpdate(c, "CHECK", "INSERT INTO books (title, author_id, year, price_cents) VALUES ('Time Ship', 1, 3000, 999)");
            tryUpdate(c, "NOT NULL", "INSERT INTO books (title, author_id, year) VALUES ('Free Book', 1, 2020)");
        }
    }
}
