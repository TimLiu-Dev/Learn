import java.sql.*;
import java.util.*;

public class Friends {
    static final String[][] EDGES = { {"ana","ben"}, {"ben","cai"}, {"cai","dee"}, {"ana","eve"}, {"eve","cai"}, {"dee","fay"} };

    // L21's breadth-first search, stopping after maxHops.
    static Map<String, Integer> bfs(String start, int maxHops) {
        Map<String, List<String>> next = new HashMap<>();
        for (String[] e : EDGES) next.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
        Map<String, Integer> hops = new LinkedHashMap<>();
        hops.put(start, 0);
        Deque<String> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty()) {
            String p = queue.removeFirst();
            if (hops.get(p) == maxHops) continue;
            for (String q : next.getOrDefault(p, List.of()))
                if (!hops.containsKey(q)) { hops.put(q, hops.get(p) + 1); queue.addLast(q); }
        }
        return hops;
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement s = c.createStatement()) { s.execute("CREATE TABLE friends (a TEXT, b TEXT)"); }
            try (PreparedStatement ins = c.prepareStatement("INSERT INTO friends VALUES (?, ?)")) {
                for (String[] e : EDGES) { ins.setString(1, e[0]); ins.setString(2, e[1]); ins.executeUpdate(); }
            }
            String sql = """
                WITH RECURSIVE reach(person, hops) AS (
                  SELECT ?, 0
                  UNION
                  SELECT f.b, r.hops + 1 FROM reach r JOIN friends f ON f.a = r.person WHERE r.hops < ?
                )
                SELECT person, min(hops) FROM reach GROUP BY person""";
            for (int maxHops = 1; maxHops <= 4; maxHops++) {
                Map<String, Integer> fromSql = new HashMap<>();
                try (PreparedStatement q = c.prepareStatement(sql)) {
                    q.setString(1, "ana"); q.setInt(2, maxHops);
                    try (ResultSet rs = q.executeQuery()) { while (rs.next()) fromSql.put(rs.getString(1), rs.getInt(2)); }
                }
                Map<String, Integer> fromBfs = bfs("ana", maxHops);
                System.out.println("within " + maxHops + ": " + new TreeMap<>(fromBfs) + "  same as SQL: " + fromSql.equals(fromBfs));
            }
        }
    }
}
