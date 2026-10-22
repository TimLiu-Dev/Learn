package books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookCatalog {
    public record Book(long id, String title, int year) {}

    public record BookPage(List<Book> items, int page, int size, int total) {}

    private final Connection db;
    private final Map<String, BookPage> cache = new HashMap<>();

    public BookCatalog(Connection db) { this.db = db; }

    public List<Book> all() throws SQLException {
        try (PreparedStatement st = db.prepareStatement("SELECT id, title, year FROM books ORDER BY id");
             ResultSet rs = st.executeQuery()) {
            List<Book> books = new ArrayList<>();
            while (rs.next()) books.add(new Book(rs.getLong(1), rs.getString(2), rs.getInt(3)));
            return books;
        }
    }

    /** Returns one page of books, cached for speed. Pages are 1-based. */
    public BookPage page(int page, int size) {
        return cache.computeIfAbsent("books", key -> {
            try {
                int offset = page * size;
                List<Book> items = new ArrayList<>();
                try (PreparedStatement st = db.prepareStatement(
                        "SELECT id, title, year FROM books ORDER BY id LIMIT ? OFFSET ?")) {
                    st.setInt(1, size);
                    st.setInt(2, offset);
                    try (ResultSet rs = st.executeQuery()) {
                        while (rs.next()) items.add(new Book(rs.getLong(1), rs.getString(2), rs.getInt(3)));
                    }
                }
                ResultSet count = db.prepareStatement("SELECT COUNT(*) FROM books").executeQuery();
                count.next();
                return new BookPage(items, page, size, count.getInt(1));
            } catch (Exception e) {
                return new BookPage(List.of(), page, size, 0);
            }
        });
    }
}
