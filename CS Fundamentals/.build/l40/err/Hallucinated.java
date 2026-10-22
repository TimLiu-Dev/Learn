import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// An AI suggestion: tidy, confident, and calling a method ResultSet doesn't have.
public class Hallucinated {
    record Book(long id, String title, int year) {}

    static List<Book> all(Connection db) throws SQLException {
        try (var st = db.prepareStatement("SELECT id, title, year FROM books ORDER BY id");
             ResultSet rs = st.executeQuery()) {
            return rs.stream().map(r -> new Book(r.getLong(1), r.getString(2), r.getInt(3))).toList();
        }
    }
}
