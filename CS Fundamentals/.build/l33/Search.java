import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Search {
    static List<String> searchUnsafe(Connection db, String term) throws SQLException {
        try (Statement st = db.createStatement();
             ResultSet rs = st.executeQuery("SELECT title FROM books WHERE title LIKE '%" + term + "%' AND published")) {
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString(1));
            return out;
        }
    }

    // The wildcards go into the *parameter*, not the SQL text
    static List<String> searchSafe(Connection db, String term) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement("SELECT title FROM books WHERE title LIKE ? AND published")) {
            ps.setString(1, "%" + term + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<String> out = new ArrayList<>();
                while (rs.next()) out.add(rs.getString(1));
                return out;
            }
        }
    }

    static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }

    static String toHtml(List<String> titles) {
        StringBuilder sb = new StringBuilder();
        for (String t : titles) sb.append("<li>").append(escapeHtml(t)).append("</li>");
        return sb.toString();
    }

    public static void main(String[] args) throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:h2:mem:books")) {
            try (Statement st = db.createStatement()) {
                st.execute("CREATE TABLE books(title VARCHAR(60), published BOOLEAN)");
                st.execute("INSERT INTO books VALUES ('Kotlin in Action', TRUE), ('Java <Basics> & More', TRUE), ('Unreleased Draft', FALSE)");
            }
            String crafted = "' OR TRUE OR title LIKE '";
            System.out.println("unsafe 'Kot':   " + searchUnsafe(db, "Kot"));
            System.out.println("unsafe crafted: " + searchUnsafe(db, crafted));
            System.out.println("safe 'Kot':     " + searchSafe(db, "Kot"));
            System.out.println("safe crafted:   " + searchSafe(db, crafted));
            System.out.println("safe 'a' as HTML: " + toHtml(searchSafe(db, "a")));
        }
    }
}
