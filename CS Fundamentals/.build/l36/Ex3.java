import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ex3 {
    record Hit(String title, String author, Integer year) {}

    static final Map<String, String> SORTS = Map.of("title", "b.title", "year", "b.year", "author", "a.name");

    // Build the SQL from fixed pieces only; every user VALUE goes in as a ? parameter.
    static List<Hit> findBooks(Connection c, String authorContains, Integer minYear, String sort) throws SQLException {
        List<String> where = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (authorContains != null) { where.add("a.name LIKE ?"); params.add("%" + authorContains + "%"); }
        if (minYear != null) { where.add("b.year >= ?"); params.add(minYear); }
        String orderBy = SORTS.get(sort);
        if (orderBy == null) throw new IllegalArgumentException("can't sort by '" + sort + "'");
        String sql = "SELECT b.title, a.name, b.year FROM books b JOIN authors a ON a.id = b.author_id"
                   + (where.isEmpty() ? "" : " WHERE " + String.join(" AND ", where))
                   + " ORDER BY " + orderBy + ", b.id";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<Hit> hits = new ArrayList<>();
                while (rs.next()) {
                    int y = rs.getInt(3);
                    hits.add(new Hit(rs.getString(1), rs.getString(2), rs.wasNull() ? null : y));
                }
                return hits;
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            System.out.println(findBooks(c, "Chiang", null, "title").stream().map(Hit::title).toList());
            System.out.println(findBooks(c, null, 2010, "year").stream().map(h -> h.year() + " " + h.title()).toList());
            System.out.println(findBooks(c, "Le Guin", 1970, "title").stream().map(Hit::title).toList());
            System.out.println(findBooks(c, "' OR '1'='1", null, "title").size());
            try { findBooks(c, null, null, "price_cents; DROP TABLE books"); }
            catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
        }
    }
}
