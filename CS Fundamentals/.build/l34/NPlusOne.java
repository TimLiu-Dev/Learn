import java.sql.*;
import java.util.*;

public class NPlusOne {
    static int queries = 0;

    static List<Map.Entry<String, Integer>> ints(Connection c, String sql, int... args) throws SQLException {
        try (PreparedStatement st = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) st.setInt(i + 1, args[i]);
            queries++;
            try (ResultSet rs = st.executeQuery()) {
                List<Map.Entry<String, Integer>> out = new ArrayList<>();
                while (rs.next()) out.add(Map.entry(rs.getString(1), rs.getInt(2)));
                return out;
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            // N+1: one query for the authors, then one more query per author
            List<Map.Entry<String, Integer>> slow = new ArrayList<>();
            for (var a : ints(conn, "SELECT name, id FROM authors ORDER BY id")) {
                int n = ints(conn, "SELECT 'n', count(*) FROM books WHERE author_id = ?", a.getValue()).get(0).getValue();
                slow.add(Map.entry(a.getKey(), n));
            }
            System.out.println("N+1 : " + queries + " queries -> " + slow);

            // One query: let the database join and count
            queries = 0;
            var fast = ints(conn, """
                SELECT a.name, count(b.id) FROM authors a
                LEFT JOIN books b ON b.author_id = a.id
                GROUP BY a.id ORDER BY a.id""");
            System.out.println("JOIN: " + queries + " query   -> " + fast);
            System.out.println("same answer: " + slow.equals(fast));
        }
    }
}
