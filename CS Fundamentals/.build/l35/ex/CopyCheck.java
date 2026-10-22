import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

// Prove the new schema rejects bad copies. Usage: ... CopyCheck.java copies.db
public class CopyCheck {
    public static void main(String[] args) throws SQLException {
        Map<String, String> attempts = new LinkedHashMap<>();
        attempts.put("duplicate barcode", "INSERT INTO copies VALUES ('C-0001', 4, 'SF-2', 'good')");
        attempts.put("unknown condition", "INSERT INTO copies VALUES ('C-0006', 4, 'SF-2', 'lost')");
        attempts.put("unknown shelf",     "INSERT INTO copies VALUES ('C-0007', 4, 'XX-9', 'good')");
        attempts.put("valid copy",        "INSERT INTO copies VALUES ('C-0008', 4, 'SF-2', 'new')");
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + args[0])) {
            try (Statement st = c.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
            for (var e : attempts.entrySet()) {
                String result;
                try (Statement st = c.createStatement()) {
                    result = "OK, " + st.executeUpdate(e.getValue()) + " row";
                } catch (SQLException ex) {
                    String m = ex.getMessage();
                    result = m.substring(m.indexOf('(') + 1, m.length() - 1);
                }
                System.out.printf("%-18s %s%n", e.getKey(), result);
            }
        }
    }
}
