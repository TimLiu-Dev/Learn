package books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookCatalog {
    public record Book(long id, String title, int year) {}

    private final Connection db;

    public BookCatalog(Connection db) { this.db = db; }

    public List<Book> all() throws SQLException {
        try (PreparedStatement st = db.prepareStatement("SELECT id, title, year FROM books ORDER BY id");
             ResultSet rs = st.executeQuery()) {
            List<Book> books = new ArrayList<>();
            while (rs.next()) books.add(new Book(rs.getLong(1), rs.getString(2), rs.getInt(3)));
            return books;
        }
    }
}
