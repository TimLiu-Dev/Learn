import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NPlusOne {
    static int queries = 0;                                  // every SELECT goes through query(), which counts

    @FunctionalInterface interface RowMapper<T> { T map(ResultSet rs) throws SQLException; }

    static <T> List<T> query(Connection c, String sql, RowMapper<T> mapper, Object... args) throws SQLException {
        queries++;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(mapper.map(rs));
                return out;
            }
        }
    }

    record Row(String title, String author) {}
    record BookRef(String title, int authorId) {}

    static List<Row> nPlusOne(Connection c) throws SQLException {
        List<Row> rows = new ArrayList<>();
        for (BookRef b : query(c, "SELECT title, author_id FROM books ORDER BY id", rs -> new BookRef(rs.getString(1), rs.getInt(2)))) {
            String author = query(c, "SELECT name FROM authors WHERE id = ?", rs -> rs.getString(1), b.authorId()).getFirst();
            rows.add(new Row(b.title(), author));            // one more query PER book
        }
        return rows;
    }

    static List<Row> join(Connection c) throws SQLException {
        return query(c, "SELECT b.title, a.name FROM books b JOIN authors a ON a.id = b.author_id ORDER BY b.id",
                     rs -> new Row(rs.getString(1), rs.getString(2)));
    }

    @FunctionalInterface interface Loader { List<Row> load(Connection c) throws SQLException; }

    public static void main(String[] args) throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:library.db")) {
            String[] names = {"N+1", "JOIN"};
            Loader[] loaders = {NPlusOne::nPlusOne, NPlusOne::join};
            for (int i = 0; i < 2; i++) {
                queries = 0;
                long t0 = System.nanoTime();
                List<Row> rows = loaders[i].load(c);
                System.out.printf("%-4s %2d rows, %2d queries, %5.2f ms   first: %s%n",
                        names[i], rows.size(), queries, (System.nanoTime() - t0) / 1e6, rows.getFirst());
            }
        }
    }
}
