import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderBy {
    static String titles(Connection c, String sql, String bind) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (bind != null) ps.setString(1, bind);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> out = new ArrayList<>();
                while (rs.next()) out.add(rs.getString(1));
                return String.join(" | ", out);
            }
        }
    }

    // The only column names a user may sort by, mapped to real SQL. Anything else is rejected.
    static final Map<String, String> SORTABLE = Map.of("title", "title", "year", "year", "price", "price_cents");

    static String safeOrderBy(String sort, boolean descending) {
        String column = SORTABLE.get(sort);
        if (column == null) throw new IllegalArgumentException("can't sort by '" + sort + "'");
        return "ORDER BY " + column + (descending ? " DESC" : " ASC") + ", id";
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            String base = "SELECT title FROM books WHERE author_id = 5 ";

            System.out.println("1. bound   ORDER BY ?  ('year'):  " + titles(c, base + "ORDER BY ?", "year"));
            System.out.println("2. glued   ORDER BY year:         " + titles(c, base + "ORDER BY " + "year", null));

            // Blind injection: the attacker can't see members, but the sort ORDER answers a yes/no question.
            for (String guess : List.of("a", "b")) {
                String evil = "CASE WHEN (SELECT substr(email, 1, 1) FROM members WHERE id = 1) = '" + guess + "' THEN title ELSE year END";
                System.out.println("3. glued   ORDER BY <guess '" + guess + "'>: " + titles(c, base + "ORDER BY " + evil, null));
            }
            String evil = "(SELECT email FROM members WHERE id = 1)";

            for (String sort : List.of("year", "price", "email", evil)) {
                try { System.out.println("4. allowlist '" + sort + "': " + titles(c, base + safeOrderBy(sort, true), null)); }
                catch (IllegalArgumentException e) { System.out.println("4. allowlist rejected: " + e.getMessage()); }
            }
        }
    }
}
