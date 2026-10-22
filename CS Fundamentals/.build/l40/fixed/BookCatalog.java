package books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookCatalog {
    public record Book(long id, String title, int year) {}

    public record BookPage(List<Book> items, int page, int size, int total) {}

    private record Key(int page, int size) {}

    private final Connection db;
    private final Map<Key, BookPage> cache = new ConcurrentHashMap<>();       // fix 5: thread-safe

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
        if (page < 1 || size < 1) throw new IllegalArgumentException("page and size must be at least 1");
        return cache.computeIfAbsent(new Key(page, size), key -> {           // fix 2: key has both params
            try {
                return load(page, size);
            } catch (SQLException e) {                                         // fix 3: rethrow, don't hide
                throw new IllegalStateException("could not load page " + page, e);
            }
        });
    }

    private BookPage load(int page, int size) throws SQLException {
        int offset = (page - 1) * size;                                        // fix 1: pages are 1-based
        List<Book> items = new ArrayList<>();
        try (PreparedStatement st = db.prepareStatement(
                "SELECT id, title, year FROM books ORDER BY id LIMIT ? OFFSET ?")) {
            st.setInt(1, size);
            st.setInt(2, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) items.add(new Book(rs.getLong(1), rs.getString(2), rs.getInt(3)));
            }
        }
        try (PreparedStatement st = db.prepareStatement("SELECT COUNT(*) FROM books");   // fix 4: closed
             ResultSet rs = st.executeQuery()) {
            rs.next();
            return new BookPage(items, page, size, rs.getInt(1));
        }
    }
}
