import java.sql.*;
import java.util.List;

public class Savepoints {
    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT UNIQUE NOT NULL)");
            }
            List<String[]> signups = List.of(new String[]{"Ada", "ada@example.com"}, new String[]{"Alan", "alan@example.com"},
                                             new String[]{"Ada again", "ada@example.com"}, new String[]{"Grace", "grace@example.com"});

            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO members (name, email) VALUES (?, ?)")) {
                for (String[] s : signups) {
                    Savepoint sp = c.setSavepoint();          // a bookmark inside the transaction
                    try {
                        ps.setString(1, s[0]); ps.setString(2, s[1]); ps.executeUpdate();
                        c.releaseSavepoint(sp);
                    } catch (SQLException e) {
                        c.rollback(sp);                       // undo only this row
                        System.out.println("skipped " + s[0] + ": " + e.getMessage());
                    }
                }
            }
            c.commit();
            c.setAutoCommit(true);

            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, name FROM members ORDER BY id")) {
                while (rs.next()) System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        }
    }
}
